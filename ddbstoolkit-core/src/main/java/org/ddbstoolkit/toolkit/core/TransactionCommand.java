package org.ddbstoolkit.toolkit.core;

/**
 * DDBS data manipulation action 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class TransactionCommand {

	/**
	 * Data manipulation action
	 */
	private TransactionAction dataAction;
	
	/**
	 * Entity
	 */
	private IEntity entity;

	/**
	 * Get data manipulation action
	 * @return Data manipulation action
	 */
	public TransactionAction getDataAction() {
		return dataAction;
	}

	/**
	 * Set data manipulation action
	 * @param dataAction Data manipulation action
	 */
	public void setDataAction(TransactionAction dataAction) {
		this.dataAction = dataAction;
	}

	/**
	 * Get the entity
	 * @return Entity
	 */
	public IEntity getEntity() {
		return entity;
	}

	/**
	 * Set entity
	 * @param entity Entity
	 */
	public void setEntity(IEntity entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "DataCommand [dataAction=" + dataAction + ", entity=" + entity
				+ "]";
	}
}
