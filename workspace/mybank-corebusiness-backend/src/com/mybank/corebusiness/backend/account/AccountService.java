package com.mybank.corebusiness.backend.account;

import java.util.logging.Logger;

import com.mybank.corebusiness.api.account.AccountApi;
import com.mybank.corebusiness.api.account.BankAccount;
import com.mybank.corebusiness.api.rest.CallException;
import com.mybank.corebusiness.backend.Ctx;
import com.mybank.corebusiness.backend.persistence.PersistenceApi;

/**
 * Business logic for bank account management.
 * TODO: work in progress, to be extended in future iterations. 
 */
public class AccountService implements AccountApi {

    @Override
    public void createAccount(String iban) throws CallException {
        // Context.
        PersistenceApi datastore = Ctx.get().getPersistenceLive();
        
        // Check the IBAN already exists.
        if (0 < datastore.count(BankAccount.class, "accountIban = '" + iban.trim() + "'")) throw new CallException("Bank account NOT created. Provided IBAN is not available");

        // Process bank account creation.
        BankAccount newAccount = new BankAccount();
        newAccount.setAccountIban(iban.trim());
        newAccount.setBalance(0); // Initially with empty amount.
        datastore.save(newAccount.getAccountIban(), newAccount);
        
        log.info("Bank account created '" + newAccount.getAccountIban() + "'");
    }

    @Override
    public boolean checkAccount(String iban) throws CallException {
        // Context.
        PersistenceApi datastore = Ctx.get().getPersistenceLive();
        
        // Check the IBAN exists.
        return 0 < datastore.count(BankAccount.class, "accountIban = '" + iban.trim() + "'");
    }

    @Override
    public Long getBalance(String iban) throws CallException {
        // Context.
        PersistenceApi datastore = Ctx.get().getPersistenceLive();
        
        // Check the IBAN exists.
        BankAccount account = (BankAccount) datastore.load(BankAccount.class, iban.trim());
        
        // Case of missing IBAN.
        if (account == null) throw new CallException("Bank account '" + iban + "' does not exist");
        
        // Provide balance.
        return account.getBalance();
    }

    @Override
    public void setBalance(String iban, long balance) throws CallException {
        // Context.
        PersistenceApi datastore = Ctx.get().getPersistenceLive();
        
        // Check the IBAN exists.
        BankAccount account = (BankAccount) datastore.load(BankAccount.class, iban.trim());
        
        // Case of missing IBAN.
        if (account == null) throw new CallException("Bank account '" + iban + "' does not exist");

        // Case of negative balance.
        if (balance < 0) throw new CallException("Balance not updated. Its value cannot be negative. Received value " + balance);
        
        // Process balance change.
        long prevBalance = account.getBalance();
        account.setBalance(balance);
        datastore.save(account.getAccountIban(), account);
        
        log.info("Balance of account '" + account.getAccountIban() + "' changed from " + prevBalance + " to " + balance);
    }

    private static Logger log = Logger.getLogger(AccountService.class.getName());
}
