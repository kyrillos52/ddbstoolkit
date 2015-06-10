package org.ddbstoolkit.toolkit.core.reflexion;

import java.util.HashMap;
import java.util.Map;

import org.ddbstoolkit.toolkit.core.IEntity;

/**
 * DDBSEntity manager
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
@SuppressWarnings("rawtypes")
public class DDBSEntityManager<T extends DDBSEntity> {

	/**
	 * Class inspector
	 */
	protected ClassInspector classInspector;
	
	/**
	 * DDBS Entity Manager
	 * @param classInspector
	 */
	public DDBSEntityManager(ClassInspector classInspector) {
		super();
		this.classInspector = classInspector;
	}

	/**
	 * Map of computed entities
	 */
	protected Map<Class, T> mapEntities = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public DDBSEntity<DDBSEntityProperty> getDDBSEntity(IEntity object) {
		
		DDBSEntity<DDBSEntityProperty> ddbsEntity = mapEntities.get(object.getClass());
		
		if(ddbsEntity == null) {
			ddbsEntity = DDBSEntity.getDDBSEntity(object, classInspector);
			mapEntities.put(object.getClass(), (T)ddbsEntity);
		}
		
		return ddbsEntity;
		
	}
}
