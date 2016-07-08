package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;

/**
 * SPARQL DDBS Entity
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlDDBSEntity<T extends SparqlClassProperty> extends DDBSEntity<T> {
	
	public SparqlDDBSEntity(IEntity iEntity, ClassInspector classInspector) {	
		super(iEntity.getClass(), classInspector);
	}
	
	/**
	 * Get Sparql service URL
	 * @param entityObject Entity object
	 * @return service url or null
	 */
	public String getServiceUrl(IEntity entityObject) {
		Annotation[] annotations = entityObject.getClass().getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Service) {
				Service myService = (Service) annotation;
				return myService.url();
			}
		}
		return null;
	}
	
	/**
	 * Get default namespace
	 * @param entityObject Entity object
	 * @return Default namespace
	 */
	public String getDefaultNamespace(IEntity entityObject) {
		Annotation[] classAnnotations = entityObject.getClass().getAnnotations();

		for (Annotation annotation : classAnnotations) {
			if (annotation instanceof DefaultNamespace) {
				DefaultNamespace ns = (DefaultNamespace) annotation;

				return ns.url();
			}
		}
		return null;
	}
	
	
	
	/**
	 * Get ID Entity properties
	 * 
	 * @return ID Entity properties
	 */
	public List<SparqlClassProperty> getSparqlEntityIDProperties() {
		List<SparqlClassProperty> listIDProperties = new ArrayList<>();
		for (T ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty.isIDEntity()) {
				listIDProperties.add(ddbsEntityProperty);
			}
		}

		return listIDProperties;
	}
	
	/**
	 * Get DDBSEntity entity
	 * 
	 * @param iEntity IEntity
	 * @param classInspector Class inspector
	 * @return DDBS Entity
	 */
	@SuppressWarnings({ "rawtypes" })
	public static SparqlDDBSEntity getDDBSEntity(IEntity iEntity, ClassInspector classInspector) {
		return new SparqlDDBSEntity<SparqlClassProperty>(iEntity, classInspector);
	}
	
	public SparqlClassProperty getUri() {
		for (SparqlClassProperty sparqlClassProperties : entityProperties) {
			if (sparqlClassProperties.isUri()) {
				return sparqlClassProperties;
			}
		}
		return null;
	}
	
	/**
	 * Get Object variable
	 * @param object Entity
	 * @return Object variable
	 */
	public String getObjectVariable(IEntity object) {
		if (object != null) {
			
			SparqlClassProperty uri = getUri();
			
			if(uri != null) {
				return "?"+uri.getName();
			}

			return "?"
					+ getDatastoreEntityName();
		} else {
			return null;
		}
	}
	
	public List<SparqlClassProperty> getSupportedPrimaryTypeEntityPropertiesWithoutURI() {

		List<SparqlClassProperty> listWithoutPeerUID = new ArrayList<>();
		for (SparqlClassProperty sparqlClassProperties : entityProperties) {
			if (!sparqlClassProperties.isUri() && !sparqlClassProperties.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.IENTITY_ARRAY)) {
				listWithoutPeerUID.add(sparqlClassProperties);
			}
		}
		return listWithoutPeerUID;
	}
}
