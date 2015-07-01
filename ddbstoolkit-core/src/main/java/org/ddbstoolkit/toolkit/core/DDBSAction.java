package org.ddbstoolkit.toolkit.core;

/**
 * DDBS action
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public enum DDBSAction {
	
	LIST_ALL(1),
	READ(2),
	READ_LAST_ELEMENT(3),
	ADD(4),
	UPDATE(5),
	DELETE(6),
	LIST_PEERS(7),
	LOAD_ARRAY(8),
	CREATE_ENTITY(9),
	IS_AUTOCOMMIT(10),
	COMMIT(11),
	ROLLBACK(12);
	
	/**
	 * Action code
	 */
	private int actionCode;
	
	/**
	 * Get action code
	 * @return Action code
	 */
	public int getActionCode() {
		return actionCode;
	}
	
    private DDBSAction(int actionCode) {
		this.actionCode = actionCode;
	}
}
