package com.mybank.corebusiness.api.transaction;

import java.io.Serializable;

/**
 * Criteria for the request of information about some transactions stored in the system.
 * Conditions for those transactions to meet in order to be provided in a request.  
 */
@SuppressWarnings("serial")
public class TransactionQuery implements Serializable {

	/** Unique identifier of the account the requested transactions applied on. */
	private String accountIban;
	
	/** Whether the provided transactions will be given sorted by amount, otherwise by applicable date. */
	private boolean sortByAmount;
	
	/** Whether the provided transactions will be given in descending order, otherwise ascending. */
	private boolean sortDescending;
	
	/** Earliest time-stamp of the range the provided transactions must fall into. Pass 0 for unlimited. */
	private long since;
	
	/** Latest time-stamp of the range the provided transactions must fall into. Pass 0 for unlimited. */
	private long until;
	
	/** Pagination in case of too much content: page number to be provided, 0-based. */
	private int pageNumber;
	
	/** Pagination in case of too much content: maximum number of transactions to provide per page. */
	private int pageSize;

    public String getAccountIban() {
        return accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public boolean isSortByAmount() {
        return sortByAmount;
    }

    public void setSortByAmount(boolean sortByAmount) {
        this.sortByAmount = sortByAmount;
    }

    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }

    public long getUntil() {
        return until;
    }

    public void setUntil(long until) {
        this.until = until;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
