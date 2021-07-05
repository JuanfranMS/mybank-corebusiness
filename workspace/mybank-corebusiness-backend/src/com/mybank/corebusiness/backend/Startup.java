package com.mybank.corebusiness.backend;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mybank.corebusiness.backend.persistence.HibernateDatastore;
import com.mybank.corebusiness.backend.persistence.HibernateDatastore.Stores;
import com.mybank.corebusiness.backend.persistence.PersistenceApi;

@WebListener
public class Startup implements ServletContextListener {

    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        // Warm up database.
        mWarmupDbPublic = new HibernateDatastore(Stores.LIVE);
        mWarmupDbPrivate = new HibernateDatastore(Stores.ARCHIVE); // TODO: not used so far.
        
        // Starting up context.
        Ctx.get();
    }

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
        // Stopping context.
        Ctx.get().shutdown();
        
        // Terminating datastores.
        mWarmupDbPublic.shutdown();
        mWarmupDbPrivate.shutdown();
    }
    
    private PersistenceApi mWarmupDbPublic;
    private PersistenceApi mWarmupDbPrivate;
}
