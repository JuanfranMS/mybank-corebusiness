package com.mybank.corebusiness.sdk.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.mybank.corebusiness.api.rest.CallException;
import com.mybank.corebusiness.api.transaction.Transaction;
import com.mybank.corebusiness.api.transaction.TransactionQuery;
import com.mybank.corebusiness.api.transaction.TransactionStatusRequest;
import com.mybank.corebusiness.api.transaction.TransactionStatusResponse;
import com.mybank.corebusiness.sdk.AccountClient;
import com.mybank.corebusiness.sdk.TransactionClient;

/**
 * Acceptance tests for transaction service.
 * Note these tests are run on the same externally launched server. 
 * No database clean up occurs before each test.
 * It is required to restart the externally launched server for every run.
 * Anyway all test cases must run independently one each other in any order.
 */
public class TransactionTest {

    /**
     * Some transactions are created and queried on valid bank accounts.
     */
    @Test
    public void someValidTransactions() throws CallException {
        // Service access clients.
        TransactionClient transactionClient = new TransactionClient(SERVER_URL);
        AccountClient accountClient = new AccountClient(SERVER_URL);
        
        // Create two accounts.
        String iban1 = "ES123456789";
        String iban2 = "ES987654321";
        accountClient.createAccount(iban1);
        accountClient.createAccount(iban2);
        
        //
        // Generating transactions:
        //
        
        // Generate transaction for account 1.
        String reference1 = "REF1111";
        Transaction transaction1 = new Transaction();
        long amount1 = 10050; // 100.50 monetary units
        long fee1 = 100; // 1.00 monetary units
        transaction1.setReference(reference1); // Providing reference.
        transaction1.setAccountIban(iban1);
        transaction1.setDateEpoch(System.currentTimeMillis()); // Providing time-stamp.
        transaction1.setDescription("transaction one");
        transaction1.setAmount(amount1);
        transaction1.setFee(fee1);
        assertEquals(reference1, transactionClient.createTransaction(transaction1));
        
        // Account 1 should have balance. Account 2 should remain empty.
        assertEquals(amount1 - fee1, (long) accountClient.getBalance(iban1));
        assertEquals(0, (long) accountClient.getBalance(iban2));

        // Generate transaction for account 2.
        Transaction transaction2 = new Transaction();
        long amount2 = 555599; // 5555.99 monetary units
        transaction2.setAccountIban(iban2);
        transaction2.setAmount(amount2);
        String reference2 = transactionClient.createTransaction(transaction2);
        assertNotNull(reference2);
        assertNotEquals(reference1, reference2);
        
        // Account 2 should have balance. Account 1 should keep its balance.
        assertEquals(amount2, (long) accountClient.getBalance(iban2));
        assertEquals(amount1 - fee1, (long) accountClient.getBalance(iban1));
        
        //
        // Query transactions:
        //

        // Retrieve transaction 1.
        TransactionQuery query1 = new TransactionQuery();
        query1.setAccountIban(iban1);
        query1.setPageNumber(0);
        query1.setPageSize(10);
        ArrayList<Transaction> queryResult1 = transactionClient.queryTransactions(query1);
        assertEquals(1, queryResult1.size());
        assertEquals(reference1, queryResult1.get(0).getReference());
        assertEquals(iban1, queryResult1.get(0).getAccountIban());
        assertEquals(fee1, (long) queryResult1.get(0).getFee());

        // Retrieve transaction 2.
        TransactionQuery query2 = new TransactionQuery();
        query2.setAccountIban(iban2);
        query2.setPageNumber(0);
        query2.setPageSize(10);
        ArrayList<Transaction> queryResult2 = transactionClient.queryTransactions(query2);
        assertEquals(1, queryResult2.size());
        assertEquals(reference2, queryResult2.get(0).getReference());
        assertEquals(iban2, queryResult2.get(0).getAccountIban());
        assertNull(queryResult2.get(0).getFee());
        assertNotNull(queryResult2.get(0).getDateEpoch());
    }
    
    /**
     * Transaction status is requested from different channels
     * for past, present and future. 
     */
    @Test
    public void transactionStatus() throws CallException {
        // Service access clients.
        TransactionClient transactionClient = new TransactionClient(SERVER_URL);
        AccountClient accountClient = new AccountClient(SERVER_URL);
        
        // Create test account.
        String iban = "ES000000000";
        accountClient.createAccount(iban);
        
        //
        // Generating transactions:
        //
        
        // Generate transaction yesterday.
        Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        Transaction transactionYesterday = new Transaction();
        transactionYesterday.setReference("REF_YESTERDAY");
        transactionYesterday.setAccountIban(iban);
        transactionYesterday.setDateEpoch(yesterday.getTimeInMillis());
        transactionYesterday.setDescription("transaction yesterday");
        transactionYesterday.setAmount(4000L);
        transactionYesterday.setFee(100L);
        transactionClient.createTransaction(transactionYesterday);
        
        // Generate transaction today, one hour ago (this test fails at 00:00h, assumed).
        Calendar todaySonner = new GregorianCalendar();
        todaySonner.add(Calendar.MINUTE, -1);
        Transaction transactionTodaySonner = new Transaction();
        transactionTodaySonner.setReference("REF_TODAY_SONNER");
        transactionTodaySonner.setAccountIban(iban);
        transactionTodaySonner.setDateEpoch(todaySonner.getTimeInMillis());
        transactionTodaySonner.setDescription("transaction today sonner");
        transactionTodaySonner.setAmount(5000L);
        transactionTodaySonner.setFee(50L);
        transactionClient.createTransaction(transactionTodaySonner);

        // Generate transaction today, one minute in the future (this test fails at 23:59h, assumed).
        Calendar todayLater = new GregorianCalendar();
        todayLater.add(Calendar.MINUTE, +1);
        Transaction transactionTodayLater = new Transaction();
        transactionTodayLater.setReference("REF_TODAY_LATER");
        transactionTodayLater.setAccountIban(iban);
        transactionTodayLater.setDateEpoch(todayLater.getTimeInMillis());
        transactionTodayLater.setDescription("transaction today later");
        transactionTodayLater.setAmount(6000L);
        transactionTodayLater.setFee(25L);
        transactionClient.createTransaction(transactionTodayLater);
        
        // Generate transaction tomorrow.
        Calendar tomorrow = new GregorianCalendar();
        tomorrow.add(Calendar.DAY_OF_MONTH, +1);
        Transaction transactionTomorrow = new Transaction();
        transactionTomorrow.setReference("REF_TOMORROW");
        transactionTomorrow.setAccountIban(iban);
        transactionTomorrow.setDateEpoch(tomorrow.getTimeInMillis());
        transactionTomorrow.setDescription("transaction tomorrow");
        transactionTomorrow.setAmount(7000L);
        transactionTomorrow.setFee(1L);
        transactionClient.createTransaction(transactionTomorrow);
        
        //
        // Requesting statuses through INTERNAL channel:
        //
        TransactionStatusResponse response;
        TransactionStatusRequest request = new TransactionStatusRequest();
        request.setChannel("INTERNAL");
        
        // Invalid reference.
        request.setReference("REF_INVALID");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_INVALID", response.getReference());
        assertEquals("INVALID", response.getStatus());
        assertNull(response.getAmount());
        assertNull(response.getFee());
        
        // Yesterday.
        request.setReference("REF_YESTERDAY");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_YESTERDAY", response.getReference());
        assertEquals("SETTLED", response.getStatus());
        assertEquals(4000L, (long) response.getAmount());
        assertEquals(100L, (long) response.getFee());

        // Today sooner.
        request.setReference("REF_TODAY_SONNER");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TODAY_SONNER", response.getReference());
        assertEquals("PENDING", response.getStatus());
        assertEquals(5000L, (long) response.getAmount());
        assertEquals(50L, (long) response.getFee());

        // Today later.
        request.setReference("REF_TODAY_LATER");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TODAY_LATER", response.getReference());
        assertEquals("PENDING", response.getStatus());
        assertEquals(6000L, (long) response.getAmount());
        assertEquals(25L, (long) response.getFee());

        // Tomorrow.
        request.setReference("REF_TOMORROW");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TOMORROW", response.getReference());
        assertEquals("FUTURE", response.getStatus());
        assertEquals(7000L, (long) response.getAmount());
        assertEquals(1L, (long) response.getFee());

        //
        // Requesting statuses through CLIENT channel:
        //
        request.setChannel("CLIENT");
        
        // Invalid reference.
        request.setReference("REF_INVALID");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_INVALID", response.getReference());
        assertEquals("INVALID", response.getStatus());
        assertNull(response.getAmount());
        assertNull(response.getFee());
        
        // Yesterday.
        request.setReference("REF_YESTERDAY");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_YESTERDAY", response.getReference());
        assertEquals("SETTLED", response.getStatus());
        assertEquals(4000L - 100L, (long) response.getAmount());
        assertNull(response.getFee());

        // Today sooner.
        request.setReference("REF_TODAY_SONNER");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TODAY_SONNER", response.getReference());
        assertEquals("PENDING", response.getStatus());
        assertEquals(5000L - 50L, (long) response.getAmount());
        assertNull(response.getFee());

        // Today later.
        request.setReference("REF_TODAY_LATER");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TODAY_LATER", response.getReference());
        assertEquals("PENDING", response.getStatus());
        assertEquals(6000L - 25L, (long) response.getAmount());
        assertNull(response.getFee());

        // Tomorrow.
        request.setReference("REF_TOMORROW");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TOMORROW", response.getReference());
        assertEquals("FUTURE", response.getStatus());
        assertEquals(7000L - 1L, (long) response.getAmount());
        assertNull(response.getFee());
        
        //
        // Requesting statuses through ATM channel:
        //
        request.setChannel("ATM");
        
        // Invalid reference.
        request.setReference("REF_INVALID");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_INVALID", response.getReference());
        assertEquals("INVALID", response.getStatus());
        assertNull(response.getAmount());
        assertNull(response.getFee());
        
        // Yesterday.
        request.setReference("REF_YESTERDAY");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_YESTERDAY", response.getReference());
        assertEquals("SETTLED", response.getStatus());
        assertEquals(4000L - 100L, (long) response.getAmount());
        assertNull(response.getFee());

        // Today sooner.
        request.setReference("REF_TODAY_SONNER");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TODAY_SONNER", response.getReference());
        assertEquals("PENDING", response.getStatus());
        assertEquals(5000L - 50L, (long) response.getAmount());
        assertNull(response.getFee());

        // Today later.
        request.setReference("REF_TODAY_LATER");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TODAY_LATER", response.getReference());
        assertEquals("PENDING", response.getStatus());
        assertEquals(6000L - 25L, (long) response.getAmount());
        assertNull(response.getFee());

        // Tomorrow.
        request.setReference("REF_TOMORROW");
        response = transactionClient.getTransactionStatus(request);
        assertEquals("REF_TOMORROW", response.getReference());
        assertEquals("PENDING", response.getStatus()); // Be aware ATM future transactions appear as pending, not future.
        assertEquals(7000L - 1L, (long) response.getAmount());
        assertNull(response.getFee());
    }
    
    /**
     * Transactions producing negative balance in bank account.
     * Other invalid transaction cases.
     */
    @Test
    public void invalidTransactions() throws CallException {
        // Service access clients.
        TransactionClient transactionClient = new TransactionClient(SERVER_URL);
        AccountClient accountClient = new AccountClient(SERVER_URL);
        
        // Create two accounts.
        String iban = "ESIBANFORINVALIDTRANSACTIONS";
        accountClient.createAccount(iban);
        
        // Case of transaction directly with negative amount.
        Transaction transaction = new Transaction();
        transaction.setAccountIban(iban);
        transaction.setAmount(-1L); // One negative cent.
        assertThrows(CallException.class, () -> { transactionClient.createTransaction(transaction); });
        
        // Case of transaction with fee greater than main amount.
        transaction.setAccountIban(iban);
        transaction.setAmount(100L);
        transaction.setFee(101L);
        assertThrows(CallException.class, () -> { transactionClient.createTransaction(transaction); });
        
        // Case of initial transaction OK but next reaches negative balance.
        transaction.setAccountIban(iban);
        transaction.setAmount(100L);
        transaction.setFee(99L);
        transactionClient.createTransaction(transaction);
        //
        transaction.setAmount(-1);
        transaction.setFee(null);
        transactionClient.createTransaction(transaction); // This is OK, reached balance 0.
        //
        transaction.setAmount(-1);
        transaction.setFee(null);
        assertThrows(CallException.class, () -> { transactionClient.createTransaction(transaction); });
        
        // Fee cannot be greater than main amount even preserving positive balance.
        transaction.setAmount(100L);
        transaction.setFee(null);
        transactionClient.createTransaction(transaction);
        //
        transaction.setAccountIban(iban);
        transaction.setAmount(10L);
        transaction.setFee(11L);
        assertThrows(CallException.class, () -> { transactionClient.createTransaction(transaction); });
    }
    
    private static final String SERVER_URL = "http://localhost:8080/mybank-corebusiness-backend";
}

