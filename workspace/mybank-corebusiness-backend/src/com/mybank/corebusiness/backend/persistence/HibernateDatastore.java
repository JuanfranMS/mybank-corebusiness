package com.mybank.corebusiness.backend.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class HibernateDatastore implements PersistenceApi {

    /**
     * Available datastores. Different schemas to store information. 
     */
    public enum Stores {
        /** The store for real-time information. */
        LIVE,
        /** The store for historical information. */
        ARCHIVE,
    };

    /**
     * Creates a connection to the root datastore.
     */
    public HibernateDatastore() {
        mStore = Stores.LIVE;
        if (mEmf.containsKey(Stores.LIVE) && mEmf.get(Stores.LIVE).isOpen()) return;
        mEmf.put(Stores.LIVE, Persistence.createEntityManagerFactory("persistence-unit-live"));
    }

    /**
     * Creates a connection to one of the available datastores.
     */
    public HibernateDatastore(Stores store) {
        mStore = store;
        String unit = "";
        switch (store) {
        case LIVE: unit = "persistence-unit-live"; break;
        case ARCHIVE: unit = "persistence-unit-archive"; break;
        }
        
        if (mEmf.containsKey(store) && mEmf.get(store).isOpen()) return;
        mEmf.put(store, Persistence.createEntityManagerFactory(unit));
    }

    @Override
    public void shutdown() {
        for (EntityManagerFactory emf : mEmf.values()) if (emf.isOpen()) emf.close();
    }

    @Override
    public boolean isUnitTestSchema() {
        if (mTestModeFlag != null) return mTestModeFlag;
        
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return true;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            Query q = em.createNativeQuery("SELECT * FROM UNITTESTSCHEMA"); // Create this table in database to set it as a testing database. Data may be completely lost by running this mode. 
            q.getResultList();
            return mTestModeFlag = true;
        } catch (Exception e) {
            return mTestModeFlag = false;
        } finally {
            if (em != null) em.close();
        }
    }
    
    @Override
    public void save(Object key, Object entity) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public Object load(Class<?> entityClass, Object key) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return null;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            return em.find(entityClass, key);
        } finally {
            if (em != null) em.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> loadIf(Class<?> entityClass, String where) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return null;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            Query query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE " + where);
            return query.getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> loadSome(Class<?> entityClass, Integer first, Integer last, String orderBy, String where) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return null;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            Query query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e " +
                                         " WHERE " + where +
                                         " ORDER BY " + orderBy);
            if (first != null) query.setFirstResult(first);
            if (last != null) query.setMaxResults(last - first + 1);
            return query.getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    
    @Override
    public boolean delete(Class<?> entityClass, Object key) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return false;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            em.getTransaction().begin();
            Object toRemove = em.find(entityClass, key);
            if (toRemove != null) em.remove(toRemove);
            em.getTransaction().commit();
            return toRemove != null;
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public long count(Class<?> entityClass, String where) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return 0;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            Query query = em.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName() + " e " +
                                         " WHERE " + where);
            return (long) query.getSingleResult();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public long update(Class<?> entityClass, String set, String where) {
        // Shutting down. Ignore call.
        if (!mEmf.get(mStore).isOpen()) return 0;
        
        EntityManager em = null;
        try {
            em = mEmf.get(mStore).createEntityManager();
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE " + entityClass.getSimpleName() + 
                                         " SET " + set +
                                         " WHERE " + where);
            long count = query.executeUpdate();
            em.getTransaction().commit();
            return count;
        } finally {
            if (em != null) em.close();
        }
    }
    
    /** The database schema this datastore is connected to. */
    private Stores mStore;
    
    /** Unique factory for JPA access to database engine, one for each available store in this datastore. */
    private static Map<Stores, EntityManagerFactory> mEmf = new HashMap<>();
    
    /** Indicates that this datastore is running on a testing database. */
    private Boolean mTestModeFlag;
}
