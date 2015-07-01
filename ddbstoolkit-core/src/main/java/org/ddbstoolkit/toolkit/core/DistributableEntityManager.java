package org.ddbstoolkit.toolkit.core;

import java.util.List;

import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;

/**
 * Interface to access to distributed data sources
 * @author Cyril GRANDJEAN
 * @version 1.0 Class creation
 */
public interface DistributableEntityManager {
    
    /**
     * Check if the connection is open
     * @return State of the connection
     * @throws DDBSToolkitException Error to check the state of the connection
     */
    public boolean isOpen() throws DDBSToolkitException;

    /**
     * Open the data source connection
     * @throws DDBSToolkitException : Error to open the connection
     */
    public void open() throws DDBSToolkitException;

    /**
     * Close the data source connection
     * @throws DDBSToolkitException : Error to close the connection
     */
    public void close() throws DDBSToolkitException;
    
    /**
     * Set the auto-commit for the data source
     * @param isAutoCommit Boolean returning if there is auto-commit in the transaction
     * @throws DDBSToolkitException : Error to set the auto-commit value
     */
    public void setAutoCommit(boolean isAutoCommit) throws DDBSToolkitException;
    
    /**
     * Commit a transaction
     * @throws DDBSToolkitException : Error to commit the transaction
     */
    public void commit() throws DDBSToolkitException;

    /**
     * Rollback the transaction
     * @throws DDBSToolkitException : Error to rollback the connection
     */
    public void rollback() throws DDBSToolkitException;
    
    /**
     * Execute a list of actions
     * @param transactionCommands List of commands
     * @return transaction transaction
     */
    public DDBSTransaction executeTransaction(List<TransactionCommand> transactionCommands) throws DDBSToolkitException; 
    
    /**
     * Commit a transaction
     * @return transactionId Transaction Id
     * @throws DDBSToolkitException : Error to commit the transaction
     */
    public void commit(DDBSTransaction transaction) throws DDBSToolkitException;

    /**
     * Rollback the transaction
     * @return transactionId Transaction Id
     * @throws DDBSToolkitException : Error to rollback the connection
     */
    public void rollback(DDBSTransaction transaction) throws DDBSToolkitException;
    
    /**
     * List all entities of a specific object
     * @param object Object to search
     * @param conditions Conditions to filter the results
     * @param orderBy Order By Object
     * @return list of entities that match the request
     * @throws DDBSToolkitException Error during the process
     */
    public <T extends IEntity> List<T> listAll(T object, Conditions conditions, OrderBy orderBy) throws DDBSToolkitException;
    
    /**
     * List all entities of a specific object
     * @param object Object to search
     * @param conditionQueryString Condition query string to filter the results
     * @param orderBy Order By Object
     * @return list of entities that match the request
     * @throws DDBSToolkitException Error during the process
     */
    public <T extends IEntity> List<T> listAllWithQueryString(T object, String conditionQueryString, OrderBy orderBy) throws DDBSToolkitException;
    
    /**
     * Read details about an object
     * @param object object to read
     * @return Object with all the details
     * @throws DDBSToolkitException Error during reading
     */
    public <T extends IEntity> T read(T object) throws DDBSToolkitException;

    /**
     * Read the last element added of a table or a type of data source (Id annotation has to be used)
     * @param object object to read
     * @return Last object added
     * @throws DDBSToolkitException Error during reading
     */
    public <T extends IEntity> T readLastElement(T object) throws DDBSToolkitException;

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
     * Load a property of the object corresponding to an array
     * @param objectToLoad Object to load
     * @param field Array to load
     * @param orderBy Order By Object
     * @return the entity with the new field loaded
     * @throws DDBSToolkitException Problem during operation
     */
    public <T extends IEntity> T loadArray(T objectToLoad, String field, OrderBy orderBy) throws DDBSToolkitException;

    /**
     * Create the structure of the entity
     * @param objectToCreate Object to create
     * @return boolean if the transaction has succeeded
     * @throws DDBSToolkitException Problem during operation
     */
    public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException;
}
