package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * SPARQL DDBS Entity
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlDDBSEntity<T extends SparqlClassProperty> extends DDBSEntity<T> {

	public SparqlDDBSEntity(IEntity iEntity) {
		super(iEntity, SparqlClassInspector.getClassInspector());
	}
	
	/**
	 * Get DDBSEntity entity
	 * 
	 * @param iEntity
	 *            IEntity
	 */
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
