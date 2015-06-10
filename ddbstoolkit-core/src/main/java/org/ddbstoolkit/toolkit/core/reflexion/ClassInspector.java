package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.core.annotations.PropertyName;

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
     * Get the full class name of an object
     * @param object : object to inspect
     * @return Object full class name
     */
    public <T> String getFullClassName(T object)
    {
        return object.getClass().getName();
    }

    /**
     * Get the class name of an object
     * @param object : object to inspect
     * @return Object class name
     */
    public String getClassName(Object object)
    {
        return object.getClass().getSimpleName();
    }

    /**
     * List public properties of an object
     * @param object : object to inspect
     * @return list of properties
     */
	@SuppressWarnings("unchecked")
	public <T extends DDBSEntityProperty> List<T> exploreProperties(Object object)
    {
        Field[] fields = object.getClass().getFields();

        List<T> listProperties = new ArrayList<>();

        for(int counterProperties = 0; counterProperties < fields.length; ++counterProperties)
        {
        	DDBSEntityProperty ddbsEntityProperty = new DDBSEntityProperty();
        	updateDDBSEntityProperty(fields[counterProperties], ddbsEntityProperty, counterProperties);
        	listProperties.add((T)ddbsEntityProperty);
        }

        return listProperties;
    }
    
    /**
     * Update a DDBSEntity property
     * @param field Field
     * @return DDBSEntityProperty
     */
    protected void updateDDBSEntityProperty(Field field, DDBSEntityProperty ddbsEntityProperty, int counterProperties) {
    	
    	ddbsEntityProperty.setType(field.getType().getName());
    	ddbsEntityProperty.setName(field.getName());
    	ddbsEntityProperty.setPropertyName(field.getName());
    	ddbsEntityProperty.setArray(field.getType().isArray());
    	ddbsEntityProperty.setFieldIndex(counterProperties);
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
            else if(annotation instanceof PropertyName)
            {
                PropertyName myProperty = (PropertyName)annotation;
                ddbsEntityProperty.setPropertyName(myProperty.name());
            }
        }
    	
    }

}
