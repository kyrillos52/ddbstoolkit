package org.ddbstoolkit.toolkit.core.reflexion;

/**
 * Class representing a DDBS Entity property
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 * @version 1.1: Add of propertyName property
 */
public class DDBSEntityProperty {

	/**
	 * Peer UID property name
	 */
	private static final String PEER_UID_PROPERTY_NAME = "peerUid";
	
	/**
	 * Class property constructor
	 * @param isId Indicates if the field is a data source id
	 * @param hasAutoIncrement Indicates if the field is auto incrementing
	 * @param isArray Indicate if the field is an array
	 * @param name Name of the field
	 * @param type Type of the field
	 * @param value Value of the field
	 * @param propertyName Property name
	 */
    public DDBSEntityProperty(
			boolean isArray, String name, String type, DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity, Object value,
			String propertyName) {
		super();
		this.isArray = isArray;
		this.name = name;
		this.type = type;
		this.ddbsToolkitSupportedEntity = ddbsToolkitSupportedEntity;
		this.value = value;
		this.propertyName = propertyName;
	}

    /**
     * Indicates if the property is an array
     */
    protected boolean isArray;

    /**
     * Name of the property
     */
    protected String name;
    
    /**
     * Property type
     */
    protected String type;

    /**
     * Type of the property
     */
    protected DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity;

    /**
     * Value of the property
     */
    protected Object value;

    /**
     * Name of the property: if table or predicate are different
     */
    protected String propertyName;
    
    /**
     * Get object type name
     * @return Object type name
     */
    public String getObjectTypeName()
    {
    	return type.substring(2, type.length()-1);
    }

    /**
     * Get the type of the property
     * @return Type of the property
     */
    public String getType() {
        return type;
    }

    /**
     * Get the name of the property
     * @return Name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of the property
     * @return value of the property
     */
    public Object getValue() {
        return value;
    }

    /**
     * Indicates if the property is an array
     * @return  boolean
     */
    public boolean isArray() {
        return isArray;
    }
    
    /**
     * Get DDBSToolkitSupported Entity
     * @return
     */
    public DDBSToolkitSupportedEntity getDdbsToolkitSupportedEntity() {
		return ddbsToolkitSupportedEntity;
	}

	/**
     * Get the name of the property in a database or data source
     * @return
     */
    public String getPropertyName() {
        return propertyName;
    }
	
	/**
	 * Indicates if its the Peer UID property
	 * @return boolean indicating if the property is the Peer UID property
	 */
	public boolean isPeerUid() {
		return name.equals(PEER_UID_PROPERTY_NAME);
	}
	
	/**
	 * Indicates if it is a DDBSToolkit supported entity
	 * @return boolean indicating if the property is a DDBSToolkit supported entity
	 */
	public boolean isDDBSToolkitSupportedEntity()
	{
		for(DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity : DDBSToolkitSupportedEntity.values())
		{
			if(ddbsToolkitSupportedEntity.equals(this.ddbsToolkitSupportedEntity))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DDBSEntityProperty)
		{
			DDBSEntityProperty classProperty = (DDBSEntityProperty)obj;
			if(classProperty.getPropertyName().equals(this.propertyName))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "DDBSEntityProperty [isArray=" + isArray + ", name=" + name
				+ ", type=" + type + ", ddbsToolkitSupportedEntity="
				+ ddbsToolkitSupportedEntity + ", value=" + value
				+ ", propertyName=" + propertyName + "]";
	}
}
