package com.mybank.corebusiness.backend.account;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.mybank.corebusiness.api.account.AccountApi;
import com.mybank.corebusiness.api.rest.CallException;
import com.mybank.corebusiness.backend.Ctx;
import com.mybank.corebusiness.backend.common.AuthMiddlewareApi;

/**
 * REST web service entry point for bank account management.
 * Note: this service is under construction.
 */
@Path(AccountApi.SERVICE_VERSION + "/" + AccountApi.SERVICE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountRs {

    /**
     * @see AccountApi#createAccount(String)  
     */
    @POST
    @Path(AccountApi.RES_ACCOUNT)
    public void createAccount(String iban, @CookieParam(AuthMiddlewareApi.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        AccountApi accountService = Ctx.get().getAccountService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        accountService.createAccount(iban);
    }

    /**
     * @see AccountApi#checkAccount(String)
     */
    @GET
    @Path(AccountApi.RES_ACCOUNT + "/{" + AccountApi.PAR_IBAN + "}")
    public boolean checkAccount(@PathParam(AccountApi.PAR_IBAN) String iban, @CookieParam(AuthMiddlewareApi.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        AccountApi accountService = Ctx.get().getAccountService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        return accountService.checkAccount(iban);
    }
    
    /**
     * @see AccountApi#getBalance(String)
     */
    @GET
    @Path(AccountApi.RES_ACCOUNT + "/{" + AccountApi.PAR_IBAN + "}/" + AccountApi.RES_BALANCE)
    public Long getBalance(@PathParam(AccountApi.PAR_IBAN) String iban, @CookieParam(AuthMiddlewareApi.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        AccountApi accountService = Ctx.get().getAccountService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        return accountService.getBalance(iban);
    }

    /**
     * @see AccountApi#setBalance(String, long)
     */
    @PUT
    @Path(AccountApi.RES_ACCOUNT + "/{" + AccountApi.PAR_IBAN + "}/" + AccountApi.RES_BALANCE)
    public void setBalance(@PathParam(AccountApi.PAR_IBAN) String iban, long balance, @CookieParam(AuthMiddlewareApi.JWTTOKEN) String jwttoken) throws CallException {
        
        // Context.
        AuthMiddlewareApi authMiddleware = Ctx.get().getAuthMiddleware();
        AccountApi accountService = Ctx.get().getAccountService();
        Ctx.get().setBaseUrl(uri.getBaseUri().toString());

        // Authorize request.
        authMiddleware.checkAuthToken(jwttoken);
        
        // Process request.
        accountService.setBalance(iban, balance);
    }
    
    @Context
    UriInfo uri;
}
