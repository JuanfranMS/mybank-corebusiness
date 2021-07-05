package com.mybank.corebusiness.backend.transaction;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.mybank.corebusiness.api.rest.CallException;
import com.mybank.corebusiness.api.transaction.Transaction;
import com.mybank.corebusiness.api.transaction.TransactionApi;
import com.mybank.corebusiness.api.transaction.TransactionQuery;
import com.mybank.corebusiness.api.transaction.TransactionStatusRequest;
import com.mybank.corebusiness.api.transaction.TransactionStatusResponse;
import com.mybank.corebusiness.backend.Ctx;
import com.mybank.corebusiness.backend.common.AuthMiddleware;
import com.mybank.corebusiness.backend.common.AuthMiddlewareApi;

/**
 * REST web service entry point for transaction management. 
 */
@Path(TransactionApi.SERVICE_VERSION + "/" + TransactionApi.SERVICE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionRs {

    /**
     * @see TransactionApi#createTransaction(Transaction)
     */
    @POST
    @Path(TransactionApi.RES_TRANSACTION)
    public String createTransaction(Transaction transaction, @CookieParam(AuthMiddlewareApi.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        TransactionApi transactionService = Ctx.get().getTransactionService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        return transactionService.createTransaction(transaction);
    }

    /**
     * @see TransactionApi#queryTransactions(TransactionQuery)
     */
    @POST
    @Path(TransactionApi.RES_QUERY)
    public ArrayList<Transaction> queryTransactions(TransactionQuery query, @CookieParam(AuthMiddleware.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        TransactionApi transactionService = Ctx.get().getTransactionService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        return transactionService.queryTransactions(query);
    }

    /**
     * @see TransactionApi#getTransactionStatus(TransactionStatusRequest)
     */
    @POST
    @Path(TransactionApi.RES_STATUS)
    public TransactionStatusResponse getTransactionStatus(TransactionStatusRequest statusRequest, @CookieParam(AuthMiddleware.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        TransactionApi transactionService = Ctx.get().getTransactionService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        return transactionService.getTransactionStatus(statusRequest);
    }
    
    @Context
    UriInfo uri;
}
