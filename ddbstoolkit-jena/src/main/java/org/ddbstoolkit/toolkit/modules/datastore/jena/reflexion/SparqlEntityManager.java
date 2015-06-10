package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;

/**
 * Sparql Entity Manager
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlEntityManager<T extends DDBSEntity<SparqlClassProperty>> extends DDBSEntityManager<SparqlDDBSEntity<SparqlClassProperty>> {

	public SparqlEntityManager(ClassInspector classInspector) {
		super(classInspector);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public SparqlDDBSEntity getDDBSEntity(IEntity object) {
		
		SparqlDDBSEntity<SparqlClassProperty> ddbsEntity = mapEntities.get(object.getClass());
		
		if(ddbsEntity == null) {
			ddbsEntity = SparqlDDBSEntity.getDDBSEntity(object, classInspector);
			mapEntities.put(object.getClass(), ddbsEntity);
		}
		
		return ddbsEntity;
		
	}
	
	

}
