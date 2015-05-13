package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.Id;

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
	 * Class inspector
	 */
	protected static ClassInspector classInspector;
	
	public static ClassInspector getClassInspector()
	{
		if(classInspector == null)
		{
			classInspector = new ClassInspector();
		}
		return classInspector;
	}
	
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
            String nameProperty = fields[counterProperties].getName();
            boolean isArray = fields[counterProperties].getType().isArray();
            String type = fields[counterProperties].getType().getName();

            boolean isId = false;
            boolean hasAutoIncrement = true;
            Object value = null;
            String propertyName = null;

            AnnotatedElement element = (AnnotatedElement) fields[counterProperties];
            Annotation[] propertyAnnotations = element.getAnnotations();

            for(Annotation annotation : propertyAnnotations)
            {
                if(annotation instanceof Id)
                {
                    isId = true;
                    hasAutoIncrement = ((Id)annotation).autoincrement();
                }
                else if(annotation instanceof PropertyName)
                {
                    PropertyName myProperty = (PropertyName)annotation;

                    propertyName = myProperty.name();
                }
            }
            try
            {
                value = fields[counterProperties].get(object);
            }
            catch (IllegalAccessException e)
            {
                //TODO Log
            }
            
            DDBSToolkitSupportedEntity typeDDBSProperty = null;
			try {
				typeDDBSProperty = DDBSToolkitSupportedEntity.valueOf(fields[counterProperties]);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            

            if(propertyName == null)
            {
                propertyName = nameProperty;
            }

            if(!nameProperty.equals(PEER_UID_PROPERTY_NAME) && typeDDBSProperty != null)
            {
            	if(isId)
                {
                	listProperties.add((T)new DDBSEntityIDProperty(isArray, nameProperty, type, typeDDBSProperty, value, propertyName, hasAutoIncrement));
                }
                else
                {
                	listProperties.add((T)new DDBSEntityProperty(isArray, nameProperty, type, typeDDBSProperty, value, propertyName));
                }
            }
        }

        return listProperties;
    }

}
