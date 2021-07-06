package com.mybank.corebusiness.api.account;

import java.io.Serializable;

/**
 * A bank account refers to the information about an account owned by a client
 * whose current amount is the money the client has stored in the bank.
 */
@SuppressWarnings("serial")
public class BankAccount implements Serializable {

    /** Unique public identifier of a bank account. */
    private String accountIban;
    
    /** Current consolidated amount of money (cents of monetary units). TODO: required more precision than cents? */
    private long balance;

    // TODO: add more information about a bank account (owner name, NIF...)

    public String getAccountIban() {
        return accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
