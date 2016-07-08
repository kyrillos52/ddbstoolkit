package org.ddbstoolkit.toolkit.core.reflexion;

import org.ddbstoolkit.toolkit.core.IEntity;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

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
     * Reflection getter index
     */
    protected int getterIndex;
    
    /**
     * Reflection setter index
     */
    protected int setterIndex;
    
    /**
	 * ID Property : null if the field is not an id
	 */
	protected DDBSEntityIDProperty ddbsEntityIDProperty;
	
	/**
	 * Indicate if the field is encapsulated with getter and setter
	 */
	protected boolean isEncapsulated;
    
    /**
     * Get object type name
     * @return Object type name
     */
    public String getObjectTypeName() {
    	return type.substring(2, type.length()-1);
    }
    
    /**
     * Get value of the object
     * @param iEntity Entity
     * @return Entity property value
     */
    public Object getValue(IEntity iEntity) {
    	if(!isEncapsulated) {
    		FieldAccess access = FieldAccess.get(iEntity.getClass());
        	return access.get(iEntity, fieldIndex);
    	} else {
    		MethodAccess access = MethodAccess.get(iEntity.getClass());
    		return access.invoke(iEntity, getterIndex);
    	}
    	
    }
    
    /**
     * Set value of the object
     * @param iEntity Entity
     * @param object Object
     */
    public void setValue(IEntity iEntity, Object object) {
    	if(!isEncapsulated) {
    		FieldAccess access = FieldAccess.get(iEntity.getClass());
        	access.set(iEntity, fieldIndex, object);
    	} else {
    		MethodAccess access = MethodAccess.get(iEntity.getClass());
    		access.invoke(iEntity, setterIndex, object);
    	}
    	
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
     * @return Indicates if it's an array
     */
    public boolean isArray() {
        return isArray;
    }
    
    /**
     * Get DDBSToolkitSupported Entity
     * @return Supported entity
     */
    public DDBSToolkitSupportedEntity getDdbsToolkitSupportedEntity() {
		return ddbsToolkitSupportedEntity;
	}

	/**
     * Get the name of the property in a database or data source
     * @return Property name
     */
    public String getPropertyName() {
        return propertyName;
    }

	/**
     * Set if the element is an array
     * @param isArray Boolean indicating if the element is an array
     */
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	/**
     * Set the name
     * @param name name
     */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * Set the property name
     * @param propertyName Property name
     */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
     * Set the property type
     * @param type Property type
     */
	public void setType(String type) {
		this.type = type;
	}

	/**
     * Set the DDBS Toolkit Supporty entity
     * @param ddbsToolkitSupportedEntity DDBS Toolkit Supporty entity
     */
	public void setDdbsToolkitSupportedEntity(
			DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity) {
		this.ddbsToolkitSupportedEntity = ddbsToolkitSupportedEntity;
	}

	/**
     * Get DDBS Entity ID Property
     * @return DDBS Entity ID Property
     */
	public DDBSEntityIDProperty getDdbsEntityIDProperty() {
		return ddbsEntityIDProperty;
	}

	/**
     * Set the DDBS Toolkit ID property
     * @param ddbsEntityIDProperty DDBS Toolkit ID property
     */
	public void setDdbsEntityIDProperty(DDBSEntityIDProperty ddbsEntityIDProperty) {
		this.ddbsEntityIDProperty = ddbsEntityIDProperty;
	}
	
	/**
     * Indicates if it's an ID Entity
     * @return Indicates if it's an ID Entity
     */
	public boolean isIDEntity()
	{
		return ddbsEntityIDProperty != null;
	}

	/**
     * Get Field index
     * @return Field index
     */
	public int getFieldIndex() {
		return fieldIndex;
	}

	/**
     * Set the field index
     * @param fieldIndex Field index
     */
	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

	/**
     * Indicates if the property is encapsulated
     * @return Indicates if the property is encapsulated
     */
	public boolean isEncapsulated() {
		return isEncapsulated;
	}

	/**
     * Set the encapsulated property
     * @param isEncapsulated encapsulated property
     */
	public void setEncapsulated(boolean isEncapsulated) {
		this.isEncapsulated = isEncapsulated;
	}

	/**
     * Get Getter index
     * @return Getter index
     */
	public int getGetterIndex() {
		return getterIndex;
	}

	/**
     * Set the getter index
     * @param getterIndex Getter index
     */
	public void setGetterIndex(int getterIndex) {
		this.getterIndex = getterIndex;
	}

	/**
     * Get Setter index
     * @return Setter index
     */
	public int getSetterIndex() {
		return setterIndex;
	}

	/**
     * Set the setterIndex index
     * @param setterIndex Setter index
     */
	public void setSetterIndex(int setterIndex) {
		this.setterIndex = setterIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DDBSEntityProperty) {
			DDBSEntityProperty classProperty = (DDBSEntityProperty)obj;
			return classProperty.getPropertyName().equals(this.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if(this.name != null) {
			return this.name.hashCode();
		}
		return 0;
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
