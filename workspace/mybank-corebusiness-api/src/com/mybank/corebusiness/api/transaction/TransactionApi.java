package com.mybank.corebusiness.api.transaction;

import java.util.ArrayList;

import com.mybank.corebusiness.api.rest.CallException;

/**
 * Transaction API:
 * Functions and definitions about transactions available for accessing the transaction service. 
 */
public interface TransactionApi {
	
    // Service base route:
    public static final String SERVICE_PATH = "transactions";
    public static final String SERVICE_ROOT = "rest";
    public static final String SERVICE_VERSION = "1.0";

    /** Represents the set of account transaction resources. */
    public static final String RES_TRANSACTION = "transaction";
    
    /** Represents sets of account transactions meeting some conditions. */
    public static final String RES_QUERY = "query";
    
    /** Represents the resources containing the current status of transactions. */
    public static final String RES_STATUS = "status";

    /**
     * Stores a new transaction into the system.
     * @param transaction Information about the transaction. A valid reference will be generated if not provided. 
     * @return The eventual reference of the stored transaction.
     * @throws CallException On invalid transaction. Account balance must be preserved non-negative.
     */
    String createTransaction(Transaction transaction) throws CallException;
    
    /**
     * Provides the information of some transactions in the system.
     * @param query Conditions to meet for those transactions that will be provided.
     * @return Transactions found complying conditions and sorted as requested.
     * @throws CallException On invalid query.
     */
    ArrayList<Transaction> queryTransactions(TransactionQuery query) throws CallException;
    
    /**
     * Provides the current status of a transaction previously processed by the system. 
     * @param statusRequest Identification of the transaction and incoming requesting channel.
     * @return Up to date information about the status of the transaction asking for. 
     * @throws CallException On invalid request.
     */
    TransactionStatusResponse getTransactionStatus(TransactionStatusRequest statusRequest) throws CallException;
}
