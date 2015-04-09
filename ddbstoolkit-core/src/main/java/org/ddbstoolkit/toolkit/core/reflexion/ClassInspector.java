package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.ddbstoolkit.toolkit.core.Id;

/**
 * Class which inspects an object using Java Reflection
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 * @version 1.1: Add of the PropertyName annotation inside the core package
 */
public class ClassInspector {

    /**
     * Get the full class name of an object
     * @param object : object to inspect
     * @return Object full class name
     */
    public static <T> String getFullClassName(T object)
    {
        return object.getClass().getName();
    }

    /**
     * Get the class name of an object
     * @param object : object to inspect
     * @return Object class name
     */
    public static String getClassName(Object object)
    {
        return object.getClass().getSimpleName();
    }

    /**
     * List public properties of an object
     * @param object : object to inspect
     * @return list of properties
     */
    public static ArrayList<ClassProperty> exploreProperties(Object object)
    {
        Field[] fields = object.getClass().getFields();

        ArrayList<ClassProperty> listProperties = new ArrayList<ClassProperty>();

        for(int counterProperties = 0; counterProperties < fields.length; ++counterProperties)
        {
            String nameProperty = fields[counterProperties].getName();
            String typeProperty = fields[counterProperties].getType().getName();
            boolean isArray = fields[counterProperties].getType().isArray();
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

            if(propertyName == null)
            {
                propertyName = nameProperty;
            }

            listProperties.add(new ClassProperty(isId, hasAutoIncrement, isArray, nameProperty, typeProperty, value, propertyName));
        }

        return listProperties;
    }

    /**
     * Indicates if the property is a database type
     * @param property : property to check
     * @return boolean indicating if the property is compatible with DDBSToolkit supported types
     */
    public static boolean isDatabaseType(ClassProperty property)
    {
        return property.getType().equals("int") || property.getType().equals("long") || property.getType().equals("float") || property.getType().equals("java.lang.String") || property.getType().equals("Timestamp");
    }

}
