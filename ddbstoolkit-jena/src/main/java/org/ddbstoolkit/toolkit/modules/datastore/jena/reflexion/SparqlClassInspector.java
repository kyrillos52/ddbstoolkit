package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.annotations.PropertyName;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

/**
 * SPARQL Class inspectorde
 * @version 1.0: Creation of the class
 * @version 1.1: Move of the PropertyName annotation into the core package
 */
public class SparqlClassInspector extends ClassInspector {

    /**
     * Explore properties of a Sparql object
     * @param o Sparql object
     * @return
     */
	@SuppressWarnings("unchecked")
	@Override
    public <T extends DDBSEntityProperty> List<T> exploreProperties(Class<?> classData)
    {
        Annotation[] classAnnotations = classData.getAnnotations();
		
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
		
		Field[] fields = classData.getFields();

        List<T> listProperties = new ArrayList<>();

        for(int counterProperties = 0; counterProperties < fields.length; ++counterProperties)
        {
        	SparqlClassProperty ddbsEntityProperty = new SparqlClassProperty();
        	updateDDBSEntityProperty(fields[counterProperties], ddbsEntityProperty, counterProperties, defaultNamespaceName, defaultNamespaceUrl);
        	listProperties.add((T)ddbsEntityProperty);
        }

        return listProperties;
    }

	protected void updateDDBSEntityProperty(Field field,
			DDBSEntityProperty ddbsEntityProperty, int counterProperties, String defaultNamespaceName, String defaultNamespaceUrl) {
		super.updateDDBSEntityProperty(field, ddbsEntityProperty, counterProperties);
		
		if(ddbsEntityProperty instanceof SparqlClassProperty)
		{
			SparqlClassProperty sparqlClassProperty = (SparqlClassProperty)ddbsEntityProperty;
			sparqlClassProperty.setNamespaceName(defaultNamespaceName);
	        sparqlClassProperty.setNamespaceURL(defaultNamespaceUrl);
			
			AnnotatedElement element = (AnnotatedElement) field;
	        Annotation[] propertiesAnnotations = element.getAnnotations();
	        
	        for(Annotation annotation : propertiesAnnotations)
	        {
	        	if(annotation instanceof Namespace)
	            {
	                Namespace myNamespace = (Namespace)annotation;
	                
	                sparqlClassProperty.setNamespaceName(myNamespace.name());
	                sparqlClassProperty.setNamespaceURL(myNamespace.url());
	            }
	            else if(annotation instanceof URI)
	            {
	            	sparqlClassProperty.setUri(true);
	            }
	            else if(annotation instanceof Optional)
	            {
	            	sparqlClassProperty.setOptional(true);
	            }
	            else if(annotation instanceof PropertyName)
	            {
	                PropertyName myProperty = (PropertyName)annotation;
	                sparqlClassProperty.setPropertyName(myProperty.name());
	            }
	        }
		}
		
	}
	
	
}
