package org.ddbstoolkit.toolkit.core;

import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;

/**
 * Interface to update an entity
 * @author Cyril GRANDJEAN
 * @version 1.0 Class creation
 */
public interface UpdatableEntityManager {

    /**
     * Add an element to a data source
     * @param objectToAdd Object to add in a data source
     * @return boolean if the transaction has succeeded
     * @throws DDBSToolkitException Problem during operation
     */
    public boolean add(IEntity objectToAdd) throws DDBSToolkitException;

    /**
     * Update an element in a data source
     * @param objectToUpdate Object to update
     * @return boolean if the transaction has succeeded
     * @throws DDBSToolkitException  Problem during operation
     */
    public boolean update(IEntity objectToUpdate) throws DDBSToolkitException;

    /**
     * Delete an object in a data source
     * @param objectToDelete Object to delete
     * @return boolean if the transaction has succeeded
     * @throws DDBSToolkitException Problem during operation
     */
    public boolean delete(IEntity objectToDelete) throws DDBSToolkitException;
    
    /**
     * Create the structure of the entity
     * @param objectToCreate Object to create
     * @return boolean if the transaction has succeeded
     * @throws DDBSToolkitException Problem during operation
     */
    public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException;
}
