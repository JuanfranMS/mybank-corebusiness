package com.mybank.corebusiness.sdk;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.mybank.corebusiness.api.rest.CallException;
import com.mybank.corebusiness.api.transaction.Transaction;
import com.mybank.corebusiness.api.transaction.TransactionApi;
import com.mybank.corebusiness.api.transaction.TransactionQuery;
import com.mybank.corebusiness.api.transaction.TransactionStatusRequest;
import com.mybank.corebusiness.api.transaction.TransactionStatusResponse;

/**
 * Provides access to transaction service's features. 
 */
public class TransactionClient implements TransactionApi {

    /**
     * Base URL where the remote service is located.
     * @param appUrl Example: "http://localhost:8080/application"
     */
    public TransactionClient(String appUrl) {
        mServiceUrl = appUrl + "/" + 
                      TransactionApi.SERVICE_ROOT + "/" + 
                      TransactionApi.SERVICE_VERSION;
        
        // Initializing REST requests.
        mClient = ClientBuilder.newClient();
        mClient.register(GsonJerseyProvider.class);
    }
    
    @Override
    public String createTransaction(Transaction transaction) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(TransactionApi.SERVICE_PATH)
                .path(TransactionApi.RES_TRANSACTION);

        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .post(Entity.entity(transaction, MT));

        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(String.class);
        } else {
            String msg = response.readEntity(String.class);
            throw new CallException(CallException.extractMsg(msg));
        }
    }

    @Override
    public ArrayList<Transaction> queryTransactions(TransactionQuery query) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(TransactionApi.SERVICE_PATH)
                .path(TransactionApi.RES_QUERY);

        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .post(Entity.entity(query, MT));

        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<ArrayList<Transaction>>() {});
        } else {
            String msg = response.readEntity(String.class);
            throw new CallException(CallException.extractMsg(msg));
        }
    }

    @Override
    public TransactionStatusResponse getTransactionStatus(TransactionStatusRequest statusRequest) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(TransactionApi.SERVICE_PATH)
                .path(TransactionApi.RES_STATUS);
                
        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .post(Entity.entity(statusRequest, MT));
        
        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(TransactionStatusResponse.class);
        } else {
            String msg = response.readEntity(String.class);
            throw new CallException(CallException.extractMsg(msg));
        }
    }

    /** Base URI for all calls to remote service. */
    private String mServiceUrl;
    
    /** REST request provider used for all calls from this instance. */
    private Client mClient;
    
    /** REST media type used for all calls. */
    private static final String MT = MediaType.APPLICATION_JSON;
}
