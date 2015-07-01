package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;

/**
 * DDBS Toolkit Transaction
 * @author Cyril Grandjean
 * @version 1.0 Class transaction
 */
public class DDBSTransaction implements Serializable {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Transaction Id
	 */
	protected String transactionId;

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
}
