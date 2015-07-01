package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * Class which inspects an object using Java Reflection
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 * @version 1.1: Add of the PropertyName annotation inside the core package
 */
public class ClassInspector {

	/**
	 * Peer UID property name
	 */
	protected static final String PEER_UID_PROPERTY_NAME = "peerUid";

    /**
     * List public properties of an object
     * @param object : object to inspect
     * @return list of properties
     */
	@SuppressWarnings("unchecked")
	public <T extends DDBSEntityProperty> List<T> exploreProperties(Class<?> classData)
    {		
        Field[] fields = classData.getDeclaredFields();

        List<T> listProperties = new ArrayList<>();
        int counterProperties = 0;
        for(Field field : fields)
        {
        	boolean hasGetterAndSetter = hasGetterAndSetter(classData, field.getName());
        	
        	if(!field.getName().equals(PEER_UID_PROPERTY_NAME) && !Modifier.isStatic(field.getModifiers())
        			&& (Modifier.isPublic(field.getModifiers()) || hasGetterAndSetter)
        			) {
        		
        		DDBSEntityProperty ddbsEntityProperty = new DDBSEntityProperty();
            	updateDDBSEntityProperty(classData,field, ddbsEntityProperty, counterProperties, hasGetterAndSetter);
            	listProperties.add((T)ddbsEntityProperty);
            	counterProperties++;
        	}
        }

        return listProperties;
    }
	
	/**
	 * Has getters and setters
	 * @param classData Class object
	 * @param fieldName Field name
	 * @return
	 */
	protected boolean hasGetterAndSetter(Class<?> classData, String fieldName) {
		
		boolean hasGetter = false;
		boolean hasSetter = false;
		
		String expectedGetterName = retrieveGetterMethodName(fieldName);
		String expectedSetterName = retrieveSetterMethodName(fieldName);
		for(Method method : classData.getMethods()) {
			
			if(method.getName().equals(expectedGetterName)) {
				hasGetter = true;
			} else if(method.getName().equals(expectedSetterName)) {
				hasSetter = true;
			}
		}
		
		return hasGetter && hasSetter;
	}
	
	/**
	 * retrieve the last part getter and setter
	 * @param fieldName Field Name
	 * @return
	 */
	private String retrieveLastPartGetterAndSetter(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
	}
	
	/**
	 * Retrieve getter method name
	 * @return  Getter method name
	 */
	protected String retrieveGetterMethodName(String fieldName) {
		return "get"+retrieveLastPartGetterAndSetter(fieldName);
	}
	
	/**
	 * Retrieve setter method name
	 * @return  Setter method name
	 */
	protected String retrieveSetterMethodName(String fieldName) {
		return "set"+retrieveLastPartGetterAndSetter(fieldName);
	}
    
    /**
     * Update a DDBSEntity property
     * @param field Field
     * @return DDBSEntityProperty
     */
    protected void updateDDBSEntityProperty(Class<?> classData, Field field, DDBSEntityProperty ddbsEntityProperty, int counterProperties, Boolean hasGetterAndSetter) {
    	
    	ddbsEntityProperty.setType(field.getType().getName());
    	ddbsEntityProperty.setName(field.getName());
    	ddbsEntityProperty.setPropertyName(field.getName());
    	ddbsEntityProperty.setArray(field.getType().isArray());
    	if(!hasGetterAndSetter) {
    		ddbsEntityProperty.setFieldIndex(counterProperties);
    	} else {
    		MethodAccess access = MethodAccess.get(classData);
    		ddbsEntityProperty.setGetterIndex(access.getIndex(retrieveGetterMethodName(field.getName())));
    		ddbsEntityProperty.setSetterIndex(access.getIndex(retrieveSetterMethodName(field.getName())));
    	}
    	
    	ddbsEntityProperty.setEncapsulated(hasGetterAndSetter);
    	try {
			ddbsEntityProperty.setDdbsToolkitSupportedEntity(DDBSToolkitSupportedEntity.valueOf(field));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			//Do Nothing
		};
    	
    	AnnotatedElement element = (AnnotatedElement) field;
        Annotation[] propertyAnnotations = element.getAnnotations();

        for(Annotation annotation : propertyAnnotations)
        {
            if(annotation instanceof Id)
            {
            	DDBSEntityIDProperty ddbsEntityIDProperty = new DDBSEntityIDProperty();
            	ddbsEntityIDProperty.setAutoIncrement(((Id)annotation).autoincrement());
            	ddbsEntityProperty.setDdbsEntityIDProperty(ddbsEntityIDProperty);
            }
            else if(annotation instanceof EntityName)
            {
                EntityName myProperty = (EntityName)annotation;
                ddbsEntityProperty.setPropertyName(myProperty.name());
            }
        }
        
        
    	
    }

}
