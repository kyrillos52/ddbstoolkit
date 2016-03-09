package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;

/**
 * DDBS Toolkit Transaction
 * @author Cyril Grandjean
 * @version 1.0 Class transaction
 */
public class DDBSTransaction implements Serializable, UpdatableEntityManager {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Transaction Id
	 */
	protected String transactionId;
	
	/**
	 * List of transaction commands
	 */
	protected List<TransactionCommand> transactionCommands;
	
	/**
	 * Distributable Entity manager
	 */
	protected DistributableEntityManager distributableEntityManager;
	
	/**
	 * List of transaction
	 */
	protected List<DDBSTransaction> transactions;

	/**
	 * Transaction
	 * @param transactionId Transaction Id
	 */
	public DDBSTransaction(String transactionId) {
		super();
		this.transactionId = transactionId;
	}

	/**
	 * Get transaction Id
	 * @return Transaction Id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Get list of transaction commands
	 * @return Transaction list of transaction commands
	 */
	public List<TransactionCommand> getTransactionCommands() {
		if(transactionCommands == null) {
			transactionCommands = new ArrayList<>();
		}
		return transactionCommands;
	}

	/**
	 * Get list of included transactions
	 * @return list of included transactions
	 */
	public List<DDBSTransaction> getTransactions() {
		if(transactions == null) {
			transactions = new ArrayList<>();
		}
		return transactions;
	}
	
	/**
	 * Set list of included transactions
	 * @param transactions list of included transactions
	 */
	public void setTransactions(List<DDBSTransaction> transactions) {
		this.transactions = transactions;
	}
	
	/**
	 * Set entity manager
	 * @param distributableEntityManager Distributed Entity Manager
	 */
	public void setDistributableEntityManager(
			DistributableEntityManager distributableEntityManager) {
		this.distributableEntityManager = distributableEntityManager;
	}

	@Override
	public boolean add(IEntity objectToAdd) throws DDBSToolkitException {
		return getTransactionCommands().add(new TransactionCommand(TransactionAction.ADD, objectToAdd));
	}

	@Override
	public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {
		return getTransactionCommands().add(new TransactionCommand(TransactionAction.UPDATE, objectToUpdate));
	}

	@Override
	public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {
		return getTransactionCommands().add(new TransactionCommand(TransactionAction.DELETE, objectToDelete));
	}

	@Override
	public boolean createEntity(IEntity objectToCreate)
			throws DDBSToolkitException {
		return getTransactionCommands().add(new TransactionCommand(TransactionAction.CREATE_ENTITY, objectToCreate));
	}
	
    /**
     * Commit a transaction
     * @throws DDBSToolkitException : Error to commit the transaction
     */
    public void commit() throws DDBSToolkitException {
    	if(distributableEntityManager != null) {
    		distributableEntityManager.commit(this);
    	} else {
    		throw new IllegalStateException("Impossible to commit. Transaction has not been executed");
    	}
    }

    /**
     * Rollback the transaction
     * @throws DDBSToolkitException : Error to rollback the connection
     */
    public void rollback() throws DDBSToolkitException {
    	if(distributableEntityManager != null) {
    		distributableEntityManager.rollback(this);
    	} else {
    		throw new IllegalStateException("Impossible to rollback. Transaction has not been executed");
    	}
    }
}
