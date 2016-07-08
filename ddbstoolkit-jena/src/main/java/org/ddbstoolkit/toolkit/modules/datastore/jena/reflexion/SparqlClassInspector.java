package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityIDProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * SPARQL Class inspectorde
 * @version 1.0: Creation of the class
 * @version 1.1: Move of the PropertyName annotation into the core package
 */
public class SparqlClassInspector extends ClassInspector {

	/**
	 * SparqlClassInspector logger
	 */
	private static final Logger logger = Logger.getLogger(SparqlClassInspector.class);
	
    /**
     * Explore properties of a Sparql object
     * @param classData Sparql object
     * @return DDBS Entity property
     */
	@SuppressWarnings("unchecked")
	@Override
    public <T extends DDBSEntityProperty> List<T> exploreProperties(Class<?> classData) {
		
        Annotation[] classAnnotations = classData.getAnnotations();
		
		//Get the default namespace
        String defaultNamespaceName = "";
        String defaultNamespaceUrl = "";
        for(Annotation annotation : classAnnotations) {
        	
            if (annotation instanceof DefaultNamespace) {
                DefaultNamespace ns = (DefaultNamespace)annotation;

                defaultNamespaceName = ns.name();
                defaultNamespaceUrl = ns.url();
            }
        }
		
        Field[] fields = classData.getDeclaredFields();

        List<T> listProperties = new ArrayList<>();
        int counterProperties = 0;
        for(Field field : fields) {
        	boolean hasGetterAndSetter = hasGetterAndSetter(classData, field.getName());
        	
        	if(!field.getName().equals(PEER_UID_PROPERTY_NAME) && !Modifier.isStatic(field.getModifiers())
        			&& (Modifier.isPublic(field.getModifiers()) || hasGetterAndSetter)
        			) {
        		
        		DDBSEntityProperty ddbsEntityProperty = new SparqlClassProperty();
            	updateDDBSEntityProperty(classData, field, ddbsEntityProperty, counterProperties, defaultNamespaceName, defaultNamespaceUrl, hasGetterAndSetter);
            	listProperties.add((T)ddbsEntityProperty);
            	counterProperties++;
        	}
        }

        return listProperties;
    }

	protected void updateDDBSEntityProperty(Class<?> classData, Field field,
			DDBSEntityProperty ddbsEntityProperty, int counterProperties, String defaultNamespaceName, String defaultNamespaceUrl, boolean hasGetterAndSetter) {
		
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
			ddbsEntityProperty.setDdbsToolkitSupportedEntity(SparqlDDBSToolkitSupportedEntity.valueOf(field));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.debug("Error while trying to retrieve the entity property type", e);
		}
    	
    	AnnotatedElement element = (AnnotatedElement) field;
        Annotation[] propertyAnnotations = element.getAnnotations();

        for(Annotation annotation : propertyAnnotations) {
            if(annotation instanceof Id) {
            	DDBSEntityIDProperty ddbsEntityIDProperty = new DDBSEntityIDProperty();
            	ddbsEntityIDProperty.setAutoIncrement(((Id)annotation).autoincrement());
            	ddbsEntityProperty.setDdbsEntityIDProperty(ddbsEntityIDProperty);
            } else if(annotation instanceof EntityName) {
                EntityName myProperty = (EntityName)annotation;
                ddbsEntityProperty.setPropertyName(myProperty.name());
            }
        }
		
		if(ddbsEntityProperty instanceof SparqlClassProperty) {
			SparqlClassProperty sparqlClassProperty = (SparqlClassProperty)ddbsEntityProperty;
			sparqlClassProperty.setNamespaceName(defaultNamespaceName);
	        sparqlClassProperty.setNamespaceURL(defaultNamespaceUrl);
			
			element = (AnnotatedElement) field;
	        Annotation[] propertiesAnnotations = element.getAnnotations();
	        
	        for(Annotation annotation : propertiesAnnotations) {
	        	if(annotation instanceof Namespace) {
	                Namespace myNamespace = (Namespace)annotation;
	                
	                sparqlClassProperty.setNamespaceName(myNamespace.name());
	                sparqlClassProperty.setNamespaceURL(myNamespace.url());
	            } else if(annotation instanceof URI) {
	            	sparqlClassProperty.setUri(true);
	            } else if(annotation instanceof Optional) {
	            	sparqlClassProperty.setOptional(true);
	            } else if(annotation instanceof EntityName) {
	                EntityName myProperty = (EntityName)annotation;
	                sparqlClassProperty.setPropertyName(myProperty.name());
	            }
	        }
		}
		
	}
	
	
}
