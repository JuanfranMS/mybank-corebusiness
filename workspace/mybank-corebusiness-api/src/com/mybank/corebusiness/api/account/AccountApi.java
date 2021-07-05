package com.mybank.corebusiness.api.account;

import com.mybank.corebusiness.api.rest.CallException;

/**
 * Bank account API:
 * Functions and definitions about bank accounts management available for accessing the account service.
 * TODO: this service is under construction. So far containing basic features required by transaction service. 
 */
public interface AccountApi {

    // Service base route:
    public static final String SERVICE_PATH = "accounts";
    public static final String SERVICE_ROOT = "rest";
    public static final String SERVICE_VERSION = "1.0";
    
    /** Represents the set of bank account resources. */
    public static final String RES_ACCOUNT = "account";
    
    /** Represents the set of bank account balances. */
    public static final String RES_BALANCE = "balance";
    
    /** Path parameter to hold a bank account IBAN code. */
    public static final String PAR_IBAN = "iban";
    
    /**
     * Registers a new bank account.
     * The initial amount will be 0.
     * @param iban New unique identifier for the bank account. 
     * @throws CallException On unaccepted IBAN.
     */
    void createAccount(String iban) throws CallException;
    
    /**
     * Indicates whether a given IBAN identifies an existing bank account.
     */
    boolean checkAccount(String iban) throws CallException;
    
    /**
     * Provides the information about a given bank account.
     * @param iban Unique identifier of the bank account asking for.
     * @return Associated information of the bank account (TODO: so far just the balance). Null on unknown IBAN.
     */
    Long getBalance(String iban) throws CallException;
    
    /**
     * Establishes a new value for the current amount in a bank account.
     * @param iban Unique identifier of the bank account to be modified.
     * @param balance The new total balance to be assigned to the bank account.
     * @throws CallException On unknown IBAN or unaccepted balance value.
     */
    void setBalance(String iban, long balance) throws CallException;
}
