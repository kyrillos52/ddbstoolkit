package org.ddbstoolkit.toolkit.core.reflexion;

/**
 * Class representing a DDBS ID Entity property
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 */
public class DDBSEntityIDProperty {

    /**
     * Indicates if the key is auto incrementing
     */
    private boolean isAutoIncrement = true;
	
    /**
     * Indicates if the property is auto incrementing
     * @return
     */
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	@Override
	public String toString() {
		return "DDBSEntityIDProperty [isAutoIncrement=" + isAutoIncrement + "]";
	}
}
