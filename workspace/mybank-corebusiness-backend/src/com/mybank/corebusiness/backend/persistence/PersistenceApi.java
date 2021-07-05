package com.mybank.corebusiness.backend.persistence;

import java.util.List;

/**
 * Persistence features.
 */
public interface PersistenceApi {
    
    /**
     * Indicates whether this is a unit testing datastore, so it is auto-removed. 
     */
    boolean isUnitTestSchema();
    
    /**
     * Stores a new or existing entity into the datastore.
     * @param key Unique identifier of the entity.
     * @param entity The entity to save.
     */
    void save(Object key, Object entity);
    
    /**
     * Retrieves from datastore an entity given its key.
     * @param entityClass The kind of entity asking for.
     * @param key Unique identifier of the entity.
     * @return The entity object filled in with in information as stored.
     */
    Object load(Class<?> entityClass, Object key);
    
    /**
     * Retrieves from datastore all those entities fulfilling a condition.
     * @param entityClass The kind of entity looking up.
     * @param where Condition expression for entities to retrieve. 
     * @return Sequence of entities loaded from storage.
     */
    List<Object> loadIf(Class<?> entityClass, String where);
    
    /**
     * Retrieves from datastore some entities fulfilling a condition.
     * @param entityClass The kind of entity looking up.
     * @param first The row order number (base 0) to be the first to provide from the total set of entries fulfilling the condition.
     * @param last The row order number (base 0) to be the last to provide from the total set of entries fulfilling the condition.
     * @param orderBy Order by expression to sort entities found. They will be provided up to the specified limit.
     * @param where Condition expression for entities to retrieve.
     * @return Sequence of entities loaded from storage.
     */
    List<Object> loadSome(Class<?> entityClass, Integer first, Integer last, String orderBy, String where);
    
    /**
     * Deletes from persistence an entity.
     * @param entityClass The kind of entity to remove.
     * @param key The key of the entity instance to remove from persistence.
     * @return Whether the deletion succeeded. If not, maybe the key was not in persistence.
     */
    boolean delete(Class<?> entityClass, Object key);

    /**
     * Counts the number of entities currently stored fulfilling a condition.
     * @param entityClass The kind of entity to count.
     * @param where Condition expression for entities to count.
     * @return Number of entities counted.
     */
    long count(Class<?> entityClass, String where);

    /**
     * Modifies some entities
     * @param entityClass The kind of entity to modify.
     * @param set The new values to be set to entities' fields.
     * @param where Condition expression for entities to be affected by the update.
     * @return Number of entities modified.
     */
    long update(Class<?> entityClass, String set, String where);
    
    /**
     * The datastore is notified that the system is about to be shutdown.
     * Any open connection and in progress tasks must be stopped now.
     */
    void shutdown();
}
