package org.ddbstoolkit.toolkit.core.reflexion;

/**
 * Class representing a class property
 * User: Cyril GRANDJEAN
 * Date: 18/06/2012
 * Time: 09:52
 * Class
 * @version 1.0: Creation of the class
 * @version 1.1: Add of propertyName property
 */
public class ClassProperty {

    public ClassProperty(boolean isId, boolean hasAutoIncrement,
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
     * Indicate if the key has auto-increment
     */
    private boolean isAutoIncrement = true;

    /**
     * Indicate if the property is an array
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
     * @return  type of the property
     */
    public String getType() {
        return type;
    }

    /**
     * Get the name of the property
     * @return name of the property
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
     * Get the name of the property in a database or datasource
     * @return
     */
    public String getPropertyName() {
        return propertyName;
    }

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ClassProperty)
		{
			ClassProperty classProperty = (ClassProperty)obj;
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
