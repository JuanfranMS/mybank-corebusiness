package com.mybank.corebusiness.api.transaction;

import java.io.Serializable;

/**
 * The information provided by the system on a transaction information request through a channel. 
 */
@SuppressWarnings("serial")
public class TransactionStatusResponse implements Serializable {

    /** Unique identifier of the transaction the information is about. */
    private String reference;
    
    /** Code of the status the transaction is in the moment of the request. */
    private String status;
    
    /** Amount of monetary units (in cents) the transaction modified the account. 
     *  A positive value meant addition (credit), a negative value meant subtraction (debit). */
    private Long amount;
    
    /** Amount of monetary units (in cents) that was deduced from the amount. Always positive. */
    private Long fee;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }
}
