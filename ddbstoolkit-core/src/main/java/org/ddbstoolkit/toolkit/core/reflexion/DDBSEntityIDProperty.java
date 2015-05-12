package org.ddbstoolkit.toolkit.core.reflexion;

/**
 * Class representing a DDBS ID Entity property
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 */
public class DDBSEntityIDProperty extends DDBSEntityProperty {

    /**
     * Indicates if the key is auto incrementing
     */
    private boolean isAutoIncrement = true;

	public DDBSEntityIDProperty(boolean isArray, String name, String type, DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity,
			Object value, String propertyName, boolean isAutoIncrement) {
		super(isArray, name, type, ddbsToolkitSupportedEntity, value, propertyName);
		this.isAutoIncrement = isAutoIncrement;
	}
	
    /**
     * Indicates if the property is auto incrementing
     * @return
     */
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	@Override
	public String toString() {
		return "DDBSEntityIDProperty [isAutoIncrement=" + isAutoIncrement
				+ ", isArray=" + isArray + ", name=" + name + ", type=" + type
				+ ", ddbsToolkitSupportedEntity=" + ddbsToolkitSupportedEntity
				+ ", value=" + value + ", propertyName=" + propertyName + "]";
	}
}
