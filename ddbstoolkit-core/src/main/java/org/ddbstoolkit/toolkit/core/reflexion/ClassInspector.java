package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.ddbstoolkit.toolkit.core.Id;

/**
 * Class which inspects an object using Java Reflection
 * User: Cyril GRANDJEAN
 * Date: 18/06/2012
 * Time: 09:59
 *
 * @version 1.0: Creation of the class
 * @version 1.1: Add of the PropertyName annotation inside the core package
 */
public class ClassInspector {

    /**
     * Get the full name of a class
     * @param o : object to inspect
     * @return full class name
     */
    public static <T> String getFullClassName(T o)
    {
        Class c = o.getClass();
        return c.getName();
    }

    /**
     * Get the name of a class
     * @param o : object to inspect
     * @return class name
     */
    public static String getClassName(Object o)
    {
        Class c = o.getClass();
        return c.getSimpleName();
    }

    /**
     * List public properties of an object
     * @param o : object to inspect
     * @return list of properties
     */
    public static ArrayList<ClassProperty> exploreProperties(Object o)
    {
        Field[] f = null;
        Class c = null;

        c = o.getClass();
        f = c.getFields();

        //Get the property
        ArrayList<ClassProperty> listProperties = new ArrayList<ClassProperty>();

        //Foreach property
        for(int i=0;i<f.length;++i)
        {

            String nameProperty = f[i].getName();
            String typeProperty = f[i].getType().getName();
            boolean isArray = f[i].getType().isArray();
            boolean isId = false;
            boolean hasAutoIncrement = true;
            Object value = null;
            String propertyName = null;

            AnnotatedElement element = (AnnotatedElement) f[i];
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
                value = f[i].get(o);
            }
            catch (IllegalAccessException e)
            {
                //System.out.println("Impossible to access to this value");
            }

            if(propertyName == null)
            {
                propertyName = nameProperty;
            }

            //Add the property in the arrayList
            listProperties.add(new ClassProperty(isId, hasAutoIncrement, isArray, nameProperty, typeProperty, value, propertyName));
        }

        return listProperties;
    }

    /**
     * Indicates if the property is a database type
     * @param myProperty : property to check
     * @return boolean if the property is compatible with databases
     */
    public static boolean isDatabaseType(ClassProperty myProperty)
    {

        //If it's one of these types
        return myProperty.getType().equals("int") || myProperty.getType().equals("long") || myProperty.getType().equals("float") || myProperty.getType().equals("java.lang.String") || myProperty.getType().equals("Timestamp");
    }

}
