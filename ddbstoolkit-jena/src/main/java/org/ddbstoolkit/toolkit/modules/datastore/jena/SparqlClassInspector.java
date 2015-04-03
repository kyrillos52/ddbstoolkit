package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.ddbstoolkit.toolkit.core.Id;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.ClassProperty;
import org.ddbstoolkit.toolkit.core.reflexion.PropertyName;

/**
 * SPARQL Class inspector
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 10:19
 *
 * @version 1.0: Creation of the class
 * @version 1.1: Move of the PropertyName annotation into the core package
 */
public class SparqlClassInspector extends ClassInspector {

    /**
     * Method to detect if the property is allowed by SPARQL
     * @param myProperty Property to inspect
     * @return
     */
    public static boolean isSparqlType(ClassProperty myProperty)
    {
        String property;
        //System.out.println(myProperty.getType());
        if(myProperty.isArray())
        {
            property = myProperty.getType();
            if(property.length() > 4)
            {
                property = myProperty.getType().substring(2, myProperty.getType().length()-1);
            }
            //If it's one of these types
            return property.equals("[J") || property.equals("[F") || property.equals("[I") || property.equals("int") || property.equals("long") || property.equals("float") || property.equals("java.lang.String;") || property.equals("java.lang.String");
        }
        else
        {
            property = myProperty.getType();
            //If it's one of these types
            return property.equals("int") || property.equals("long") || property.equals("float") || property.equals("java.lang.String");
        }


    }

    /**
     * Explore properties of a Sparql object
     * @param o Sparql object
     * @return
     */
    public static ArrayList<SparqlClassProperty> explorePropertiesForSPARQL(Object o)
    {
        Field[] f = null;
        Class c = null;

        c = o.getClass();
        f = c.getFields();

        Annotation[] classAnnotations = c.getAnnotations();

        //Get the default namespace
        String defaultNamespaceName = "";
        String defaultNamespaceUrl = "";
        for(Annotation annotation : classAnnotations)
        {
            if (annotation instanceof DefaultNamespace)
            {
                DefaultNamespace ns = (DefaultNamespace)annotation;

                defaultNamespaceName = ns.name();
                defaultNamespaceUrl = ns.url();
            }
        }

        //Get the properties
        ArrayList<SparqlClassProperty> listProperties = new ArrayList<SparqlClassProperty>();

        //Foreach property
        for(int i=0;i<f.length;++i)
        {

            String name = f[i].getName();
            String type = f[i].getType().getName();
            boolean isArray = f[i].getType().isArray();
            Object value = null;
            String namespaceName = "";
            String namespaceUrl = "";
            boolean isId = false;
            boolean isUri = false;
            boolean optional = false;
            String propertyName = null;

            try
            {
                value = f[i].get(o);
            }
            catch (IllegalAccessException e)
            {
                System.out.println("Impossible to access to this value");
            }

            AnnotatedElement element = (AnnotatedElement) f[i];
            Annotation[] propertiesAnnotations = element.getAnnotations();

            //Look for a namespace
            boolean isAnotherNamespace = false;
            for(Annotation annotation : propertiesAnnotations)
            {
                if(annotation instanceof Namespace)
                {
                    isAnotherNamespace = true;

                    Namespace myNamespace = (Namespace)annotation;

                    namespaceName = myNamespace.name();
                    namespaceUrl = myNamespace.url();
                }
                else if(annotation instanceof Id)
                {
                    isId = true;
                }
                else if(annotation instanceof URI)
                {
                    isUri = true;
                }
                else if(annotation instanceof Optional)
                {
                    optional = true;
                }
                else if(annotation instanceof PropertyName)
                {
                    PropertyName myProperty = (PropertyName)annotation;

                    propertyName = myProperty.name();
                }
            }

            if(propertyName == null)
            {
                propertyName = name;
            }

            //If no namespace specified, use the default namespace
            if(!isAnotherNamespace)
            {
                namespaceName = defaultNamespaceName;
                namespaceUrl = defaultNamespaceUrl;
            }

            SparqlClassProperty myProperty = new SparqlClassProperty(isId, isArray, name, type, value, namespaceName, namespaceUrl, isUri, optional, propertyName);

            //Add the property in the arrayList
            listProperties.add(myProperty);
        }

        return listProperties;
    }
}
