package com.mybank.corebusiness.backend.transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.logging.Logger;

import com.mybank.corebusiness.api.account.AccountApi;
import com.mybank.corebusiness.api.rest.CallException;
import com.mybank.corebusiness.api.transaction.Channels;
import com.mybank.corebusiness.api.transaction.Statuses;
import com.mybank.corebusiness.api.transaction.Transaction;
import com.mybank.corebusiness.api.transaction.TransactionApi;
import com.mybank.corebusiness.api.transaction.TransactionQuery;
import com.mybank.corebusiness.api.transaction.TransactionStatusRequest;
import com.mybank.corebusiness.api.transaction.TransactionStatusResponse;
import com.mybank.corebusiness.backend.Ctx;
import com.mybank.corebusiness.backend.persistence.PersistenceApi;

/**
 * Business logic for transaction management.
 */
public class TransactionService implements TransactionApi {

    @Override
    public String createTransaction(Transaction transaction) throws CallException {
        // Context.
        AccountApi accountCli = Ctx.get().getAccountClient();
        PersistenceApi datastore = Ctx.get().getPersistenceLive();

        // Check incoming content.
        if (transaction.getAccountIban() == null || transaction.getAccountIban().isEmpty()) throw new CallException("Invalid transaction: account IBAN not provided");
        if (transaction.getFee() != null && transaction.getFee() < 0) throw new CallException("Invalid transaction: fee cannot be negative");
        if (transaction.getFee() != null && Math.abs(transaction.getAmount()) < transaction.getFee()) throw new CallException("Invalid transaction: fee cannot be greater than amount");
        if (!accountCli.checkAccount(transaction.getAccountIban())) throw new CallException("Invalid transaction: account IBAN does not exist");
        
        // Check provided reference is not used yet.
        if (transaction.getReference() != null && 0 < datastore.count(Transaction.class, "reference = '" + transaction.getReference() + "'")) {
            throw new CallException("Invalid transaction: provided reference is not available");
        }
        
        // Calculate net amount.
        long netAmmount = subtractFeeToAmount(transaction.getAmount(), transaction.getFee() != null ? transaction.getFee() : 0L);
        long amountBefore = accountCli.getBalance(transaction.getAccountIban());
        
        // Check the final balance is acceptable.
        long amountAfter = amountBefore + netAmmount; 
        if (amountAfter < 0) throw new CallException("Transaction not accepted: account would reach balance below 0");
        
        // Apply transaction.
        accountCli.setBalance(transaction.getAccountIban(), amountAfter);
        String newReference = transaction.getReference() != null ? transaction.getReference() : generateNewReference(datastore);
        Long applicableDate = transaction.getDateEpoch() != null ? transaction.getDateEpoch() : System.currentTimeMillis(); // Set server time if not received.
        transaction.setReference(newReference);
        transaction.setDateEpoch(applicableDate);
        datastore.save(Transaction.class, transaction);
        
        log.info("Transaction created '" + transaction.getReference() + "' for account '" + transaction.getAccountIban() + "'");
        return newReference;
    }

    @Override
    public ArrayList<Transaction> queryTransactions(TransactionQuery query) throws CallException {
        // Context.
        PersistenceApi datastore = Ctx.get().getPersistenceLive();

        // Check incoming content.
        if (query.getAccountIban() == null || query.getAccountIban().isEmpty()) throw new CallException("Invalid request: account IBAN not provided");
        if (query.getSince() > query.getUntil()) throw new CallException("Invalid request: initial time range is greater than final time range");
        if (query.getPageNumber() < 0) throw new CallException("Invalid request: page number cannot be negative (first valid page is 0)");
        if (query.getPageSize() <= 0) throw new CallException("Invalid request: page size must be greater than 0");
        // Note: account IBAN not checked for existence against account service, no transactions will be found, ok.
        
        // Load from database the available transactions.
        int first = query.getPageNumber() * query.getPageSize();
        int last = first + query.getPageSize() - 1;
        String orderBy = query.isSortByAmount() ? "amount" : "dateEpoch";
        String orderDirection = query.isSortDescending() ? "DESC" : "ASC";
        String where = "accountIban = '" + query.getAccountIban().trim() + "' " + 
                       (query.getSince() > 0 ? "AND dateEpoch >= " + query.getSince() + " " : "") +
                       (query.getUntil() > 0 ? "AND dateEpoch <= " + query.getUntil() : "");
        ArrayList<Transaction> transactionResult = new ArrayList<>();
        for (Object transactionObj : datastore.loadSome(Transaction.class, first, last, orderBy + " " + orderDirection, where)) {
            transactionResult.add((Transaction) transactionObj);
        }
        
        return transactionResult;
    }

    @Override
    public TransactionStatusResponse getTransactionStatus(TransactionStatusRequest statusRequest) throws CallException {
        // Context.
        PersistenceApi datastore = Ctx.get().getPersistenceLive();

        // Check incoming content.
        if (statusRequest.getReference() == null || statusRequest.getReference().isEmpty()) throw new CallException("Invalid status request: reference not provided");
        if (statusRequest.getChannel() == null || statusRequest.getChannel().isEmpty()) throw new CallException("Invalid status request: channel not provided");
        if (null == Channels.valueOf("CHANNEL_" + statusRequest.getChannel())) throw new CallException("Invalid status request: unknown channel");
        
        //
        // Business rules:
        //
        Transaction transaction = (Transaction) datastore.load(Transaction.class, statusRequest.getReference());
        
        // Case of unknown transaction reference.
        if (transaction == null) {
            TransactionStatusResponse response = new TransactionStatusResponse();
            response.setReference(statusRequest.getReference());
            response.setStatus(Statuses.STATUS_INVALID.name().split("_", 2)[1]);
            return response;
        }

        // Generate values to be provided eventually.
        long today = getTodayMidnight();
        long tomorrow = getTomorrowMidnight();
        boolean isInternalChannel = statusRequest.getChannel().equals(Channels.CHANNEL_INTERNAL.name().split("_",2)[1]);
        boolean isAtmChannel = statusRequest.getChannel().equals(Channels.CHANNEL_ATM.name().split("_",2)[1]);
        Long fee = isInternalChannel ? transaction.getFee() : null;
        Long amount = isInternalChannel ? transaction.getAmount() 
                                        : subtractFeeToAmount(transaction.getAmount(), (transaction.getFee() == null) ? 0 : transaction.getFee());
        TransactionStatusResponse response = new TransactionStatusResponse();
        
        // Cases of past.
        if (transaction.getDateEpoch() < today) {
            response.setStatus(Statuses.STATUS_SETTLED.name().split("_", 2)[1]);
        }
        
        // Cases of present.
        else if (transaction.getDateEpoch() >= today && transaction.getDateEpoch() < tomorrow) {
            response.setStatus(Statuses.STATUS_PENDING.name().split("_", 2)[1]);
        }
        
        // Cases of future.
        else if (transaction.getDateEpoch() >= tomorrow) {
            if (isAtmChannel) response.setStatus(Statuses.STATUS_PENDING.name().split("_", 2)[1]);
            else response.setStatus(Statuses.STATUS_FUTURE.name().split("_", 2)[1]);
        }
        
        // Providing result.
        response.setReference(statusRequest.getReference());
        response.setAmount(amount);
        response.setFee(fee);
        return response;
    }

    /**
     * Generates a random transaction reference that is not already used
     * as a transaction unique identifier.
     */
    private String generateNewReference(PersistenceApi datastore) {
        while (true) {
            // Generate candidate.
            String reference = UUID.randomUUID().toString().split("-", 2)[0].toUpperCase();
            
            // Check not repeated.
            if (0 == datastore.count(Transaction.class, "reference = '" + reference + "'")) return reference;
        }
    }
    
    /**
     * Provides the time-stamp in milliseconds from epoch of the previous midnight,
     * i.e. the time-stamp when the current day started (server's local time).
     */
    private long getTodayMidnight() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTimeInMillis();
    }
    
    /**
     * Provides the time-stamp in milliseconds from epoch of the next midnight,
     * i.e. the time-stamp when the current day ends (server's local time). 
     */
    private long getTomorrowMidnight() {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        return date.getTimeInMillis();
    }
    
    /**
     * Calculates the final amount after applying a fee.
     * @param amount Initial amount (positive or negative)
     * @param fee Applied fee (always positive)
     * @return The final amount once the fee is applied.
     */
    private long subtractFeeToAmount(long amount, long fee) {
        return amount < 0 ? amount + fee
                          : amount - fee;
    }

    private static Logger log = Logger.getLogger(TransactionService.class.getName());
}
