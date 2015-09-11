package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;

/**
 * DDBS data manipulation action 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class TransactionCommand implements Serializable {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Data manipulation action
	 */
	private TransactionAction dataAction;
	
	/**
	 * Entity
	 */
	private IEntity entity;
	
	/**
	 * Constructor
	 * @param dataAction Transaction action
	 * @param entity entity
	 */
	public TransactionCommand(TransactionAction dataAction, IEntity entity) {
		super();
		this.dataAction = dataAction;
		this.entity = entity;
	}

	/**
	 * Get data manipulation action
	 * @return Data manipulation action
	 */
	public TransactionAction getDataAction() {
		return dataAction;
	}

	/**
	 * Get the entity
	 * @return Entity
	 */
	public IEntity getEntity() {
		return entity;
	}

	@Override
	public String toString() {
		return "DataCommand [dataAction=" + dataAction + ", entity=" + entity
				+ "]";
	}
}
