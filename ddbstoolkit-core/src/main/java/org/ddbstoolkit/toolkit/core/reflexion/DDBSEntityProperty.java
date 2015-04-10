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
    public DDBSEntityProperty(boolean isId, boolean hasAutoIncrement,
			boolean isArray, String name, String type, Object value,
			String propertyName) {
		super();
		this.isId = isId;
		this.isAutoIncrement = hasAutoIncrement;
		this.isArray = isArray;
		this.name = name;
		this.type = type;
		this.value = value;
		this.propertyName = propertyName;
	}

	/**
     * This property indicates if the property is the id of the entity
     */
    private boolean isId = false;
    
    /**
     * Indicates if the key is auto incrementing
     */
    private boolean isAutoIncrement = true;

    /**
     * Indicates if the property is an array
     */
    private boolean isArray;

    /**
     * Name of the property
     */
    private String name;

    /**
     * Type of the property
     */
    private String type;

    /**
     * Value of the property
     */
    private Object value;

    /**
     * Name of the property: if table or predicate are different
     */
    private String propertyName;

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
     * Indicates if the property is the Id property of an object
     * @return boolean
     */
    public boolean isId() {
        return isId;
    }

    /**
     * Indicates if the property is an array
     * @return  boolean
     */
    public boolean isArray() {
        return isArray;
    }

    /**
     * Get the name of the property in a database or data source
     * @return
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Indicates if the property is auto incrementing
     * @return
     */
	public boolean isAutoIncrement() {
		return isAutoIncrement;
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
			if(ddbsToolkitSupportedEntity.getType().equals(type))
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
}
