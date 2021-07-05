package com.mybank.corebusiness.sdk;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.mybank.corebusiness.api.account.AccountApi;
import com.mybank.corebusiness.api.rest.CallException;

/**
 * Provides access to bank accounts service's features.
 * TODO: the account service is under construction. 
 *       To be replaced by the actual account service access client (interface will probably change). 
 */
public class AccountClient implements AccountApi {

    /**
     * Base URL where the remote service is located.
     * @param appUrl Example: "http://localhost:8080/application"
     */
    public AccountClient(String appUrl) {
        mServiceUrl = appUrl + "/" + 
                      AccountApi.SERVICE_ROOT + "/" + 
                      AccountApi.SERVICE_VERSION;
        
        // Initializing REST requests.
        mClient = ClientBuilder.newClient();
        mClient.register(GsonJerseyProvider.class);
    }
    
    @Override
    public void createAccount(String iban) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(AccountApi.SERVICE_PATH)
                .path(AccountApi.RES_ACCOUNT);
         
        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .post(Entity.entity(iban, MT));

        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return;
        } else if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
            return;
        } else {
            String msg = response.readEntity(String.class);
            throw new CallException(CallException.extractMsg(msg));
        }
    }

    @Override
    public boolean checkAccount(String iban) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(AccountApi.SERVICE_PATH)
                .path(AccountApi.RES_ACCOUNT)
                .path(iban);
         
        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .get();
        
        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Boolean.class);
        } else {
            String msg = response.readEntity(String.class);
            throw new CallException(CallException.extractMsg(msg));
        }
    }

    @Override
    public Long getBalance(String iban) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(AccountApi.SERVICE_PATH)
                .path(AccountApi.RES_ACCOUNT)
                .path(iban)
                .path(AccountApi.RES_BALANCE);
         
        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .get();
        
        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Long.class);
        } else {
            String msg = response.readEntity(String.class);
            throw new CallException(CallException.extractMsg(msg));
        }
    }

    @Override
    public void setBalance(String iban, long balance) throws CallException {
        // Build full URL for the request.
        WebTarget webTarget = mClient.target(mServiceUrl).path(AccountApi.SERVICE_PATH)
                .path(AccountApi.RES_ACCOUNT)
                .path(iban)
                .path(AccountApi.RES_BALANCE);
         
        // Remote call.
        Response response = webTarget.request(MT)
                .cookie(null) // TODO
                .put(Entity.entity(balance, MT));

        // Extract return value or exception.
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return;
        } else if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
            return;
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
