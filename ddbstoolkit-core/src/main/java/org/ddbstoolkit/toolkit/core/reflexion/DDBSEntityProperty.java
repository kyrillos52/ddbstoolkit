package org.ddbstoolkit.toolkit.core.reflexion;

import org.ddbstoolkit.toolkit.core.IEntity;

import com.esotericsoftware.reflectasm.FieldAccess;

/**
 * Class representing a DDBS Entity property
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 * @version 1.1: Add of propertyName property
 * @version 1.2: Add of fieldIndex
 */
public class DDBSEntityProperty {
    
    /**
     * Indicates if the property is an array
     */
    protected boolean isArray;

    /**
     * Name of the property
     */
    protected String name;
    
    /**
     * Name of the property: if table or predicate are different
     */
    protected String propertyName;
    
    /**
     * Property type
     */
    protected String type;

    /**
     * Type of the property
     */
    protected DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity;
    
    /**
     * Reflection field index
     */
    protected int fieldIndex;
    
    /**
	 * ID Property : null if the field is not an id
	 */
	protected DDBSEntityIDProperty ddbsEntityIDProperty;
    
    /**
     * Get object type name
     * @return Object type name
     */
    public String getObjectTypeName()
    {
    	return type.substring(2, type.length()-1);
    }
    
    /**
     * Get value of the object
     * @param iEntity Entity
     * @return
     */
    public Object getValue(IEntity iEntity)
    {
    	FieldAccess access = FieldAccess.get(iEntity.getClass());
    	return access.get(iEntity, fieldIndex);
    }
    
    /**
     * Set value of the object
     * @param iEntity Entity
     * @param object Object
     * @return
     */
    public void setValue(IEntity iEntity, Object object)
    {
    	FieldAccess access = FieldAccess.get(iEntity.getClass());
    	access.set(iEntity, fieldIndex, object);
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

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDdbsToolkitSupportedEntity(
			DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity) {
		this.ddbsToolkitSupportedEntity = ddbsToolkitSupportedEntity;
	}

	public DDBSEntityIDProperty getDdbsEntityIDProperty() {
		return ddbsEntityIDProperty;
	}

	public void setDdbsEntityIDProperty(DDBSEntityIDProperty ddbsEntityIDProperty) {
		this.ddbsEntityIDProperty = ddbsEntityIDProperty;
	}
	
	public boolean isIDEntity()
	{
		return ddbsEntityIDProperty != null;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}

	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DDBSEntityProperty)
		{
			DDBSEntityProperty classProperty = (DDBSEntityProperty)obj;
			if(classProperty.getPropertyName().equals(this.name))
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
				+ ", propertyName=" + propertyName + ", type=" + type
				+ ", ddbsToolkitSupportedEntity=" + ddbsToolkitSupportedEntity
				+ ", fieldIndex=" + fieldIndex + ", ddbsEntityIDProperty="
				+ ddbsEntityIDProperty + "]";
	}
}
