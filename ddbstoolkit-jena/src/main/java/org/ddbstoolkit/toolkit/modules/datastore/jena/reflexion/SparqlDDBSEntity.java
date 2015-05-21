package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.IEntity;
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

	/**
	 * Get Sparql service URL
	 * @return service url or null
	 */
	public String getServiceUrl()
	{
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
	 * @return Default namespace
	 */
	public String getDefaultNamespace()
	{
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
	public List<SparqlClassIdProperty> getSparqlEntityIDProperties() {
		List<SparqlClassIdProperty> listIDProperties = new ArrayList<>();
		for (T ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty instanceof SparqlClassIdProperty) {
				listIDProperties.add((SparqlClassIdProperty) ddbsEntityProperty);
			}
		}

		return listIDProperties;
	}
	
	public SparqlDDBSEntity(IEntity iEntity) {
		super(iEntity, SparqlClassInspector.getClassInspector());
	}
	
	/**
	 * Get DDBSEntity entity
	 * 
	 * @param iEntity
	 *            IEntity
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static SparqlDDBSEntity getDDBSEntity(IEntity iEntity) {
		return new SparqlDDBSEntity<SparqlClassProperty>(iEntity);
	}
	
	public SparqlClassProperty getUri()
	{
		for (SparqlClassProperty sparqlClassProperties : entityProperties) {
			if (sparqlClassProperties.isUri()) {
				return sparqlClassProperties;
			}
		}
		return null;
	}
	
	/**
	 * Get Object variable
	 * @param object
	 * @return
	 */
	public String getObjectVariable(IEntity object) {
		if (object != null) {
			
			SparqlClassProperty uri = getUri();
			
			if(uri != null)
			{
				return "?"+uri.getName();
			}

			return "?"
					+ getEntityName();
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
