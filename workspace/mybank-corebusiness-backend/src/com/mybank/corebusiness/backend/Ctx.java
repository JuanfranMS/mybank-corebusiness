package com.mybank.corebusiness.backend;

import java.util.logging.Logger;

import com.mybank.corebusiness.api.account.AccountApi;
import com.mybank.corebusiness.api.transaction.TransactionApi;
import com.mybank.corebusiness.backend.account.AccountService;
import com.mybank.corebusiness.backend.common.AuthMiddleware;
import com.mybank.corebusiness.backend.common.AuthMiddlewareApi;
import com.mybank.corebusiness.backend.persistence.HibernateDatastore;
import com.mybank.corebusiness.backend.persistence.HibernateDatastore.Stores;
import com.mybank.corebusiness.backend.persistence.PersistenceApi;
import com.mybank.corebusiness.backend.transaction.TransactionService;
import com.mybank.corebusiness.sdk.AccountClient;

/**
 * Container of global components for the back-end.
 * Dependency injection tool.
 */
public class Ctx {

    /**
     * Initializes the background tasks and other context components.
     */
    public Ctx() {
        log.info("Ctx starting");
        
        // Static tools instantiation.
        mAuthMiddleware = new AuthMiddleware();
        
        // Service instantiation. Business logic static components.
        mTransactionService = new TransactionService();
        mAccountService = new AccountService();
        
        log.info("Ctx started");
    }
    
    /**
     * Stops the execution of background tasks. None yet.
     */
    public void shutdown() {
        log.info("Ctx shutdown");
    }
    
    //
    // Access to singleton context.
    //
    
    public static Ctx get() {
        if (mCtxSingleton == null) mCtxSingleton = new Ctx();
        return mCtxSingleton;
    }
    
    private static Ctx mCtxSingleton;
    
    //
    // Access to context components.
    //
    
    // Tools.
    public AuthMiddlewareApi getAuthMiddleware() { return mAuthMiddleware; }
    public PersistenceApi getPersistenceLive() { return new HibernateDatastore(Stores.LIVE); } // Per request instantiation.
    public AccountApi getAccountClient() { 
        if (mAccountClient == null) mAccountClient = new AccountClient(mServicesBaseUrl);
        return mAccountClient;
    }
    
    // Services.
    public TransactionApi getTransactionService() { return mTransactionService; }
    public AccountApi getAccountService() { return mAccountService; }

    // Discover own base URL. TODO: improve this.
    public void setBaseUrl(String baseUrl) {
        mServicesBaseUrl = baseUrl.substring(0, baseUrl.length() - "/rest/".length());
    }
    
    //
    // Context static components.
    //
    
    // Tools.
    private AuthMiddlewareApi mAuthMiddleware;
    private AccountApi mAccountClient;
    
    // Services.
    private TransactionApi mTransactionService;
    private AccountApi mAccountService;

    private String mServicesBaseUrl;
    
    private static Logger log = Logger.getLogger(Ctx.class.getName());
}
