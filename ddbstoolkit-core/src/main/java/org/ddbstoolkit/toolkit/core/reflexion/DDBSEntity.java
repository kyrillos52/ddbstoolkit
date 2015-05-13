package org.ddbstoolkit.toolkit.core.reflexion;

import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.IEntity;

/**
 * DDBS Entity
 * 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DDBSEntity<T extends DDBSEntityProperty> {

	/**
	 * Entity element
	 */
	protected IEntity entityObject;

	/**
	 * Entity name
	 */
	protected String entityName;

	/**
	 * Entity properties
	 */
	protected List<T> entityProperties;

	/**
	 * Get Entity object
	 * 
	 * @return
	 */
	public IEntity getEntityObject() {
		return entityObject;
	}

	/**
	 * Entity name
	 * 
	 * @return
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Get Entity properties
	 * 
	 * @return Entity properties
	 */
	public List<T> getEntityProperties() {
		return entityProperties;
	}

	/**
	 * Get Entity properties
	 * 
	 * @return Entity properties
	 */
	public List<T> getSupportedPrimaryTypeEntityProperties() {

		List<T> listWithoutPeerUID = new ArrayList<>();
		for (T ddbsEntityProperty : entityProperties) {
			if (!ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.IENTITY_ARRAY)) {
				listWithoutPeerUID.add(ddbsEntityProperty);
			}
		}
		return listWithoutPeerUID;
	}

	/**
	 * Get ID Entity properties
	 * 
	 * @return ID Entity properties
	 */
	public List<DDBSEntityIDProperty> getEntityIDProperties() {
		List<DDBSEntityIDProperty> listIDProperties = new ArrayList<>();
		for (T ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty instanceof DDBSEntityIDProperty) {
				listIDProperties.add((DDBSEntityIDProperty) ddbsEntityProperty);
			}
		}

		return listIDProperties;
	}

	/**
	 * Get Non incrementing Entity properties
	 * 
	 * @return ID Entity properties
	 */
	public List<T> getNotIncrementingEntityProperties() {
		List<T> listProperties = new ArrayList<>();
		for (T ddbsEntityProperty : entityProperties) {
			if (((ddbsEntityProperty instanceof DDBSEntityIDProperty && !((DDBSEntityIDProperty) ddbsEntityProperty)
					.isAutoIncrement()) || !(ddbsEntityProperty instanceof DDBSEntityIDProperty))
					&& !ddbsEntityProperty.getDdbsToolkitSupportedEntity()
							.equals(DDBSToolkitSupportedEntity.IENTITY_ARRAY)) {
				listProperties.add(ddbsEntityProperty);
			}
		}

		return listProperties;
	}

	/**
	 * Get Non ID Entity properties
	 * 
	 * @return ID Entity properties
	 */
	public List<T> getEntityNonIDProperties() {
		List<T> listNonIdProperties = new ArrayList<>();
		for (T ddbsEntityProperty : entityProperties) {
			if (!(ddbsEntityProperty instanceof DDBSEntityIDProperty)
					&& !ddbsEntityProperty.getDdbsToolkitSupportedEntity()
							.equals(DDBSToolkitSupportedEntity.IENTITY_ARRAY)) {
				listNonIdProperties.add(ddbsEntityProperty);
			}
		}

		return listNonIdProperties;
	}

	/**
	 * Get a DDBSEntity property
	 * 
	 * @param propertyName
	 *            Property name
	 * @return DDBSEntity property
	 */
	public T getDDBSEntityProperty(String name) {
		for (T ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty.getName().equals(name)) {
				return ddbsEntityProperty;
			}
		}
		return null;
	}

	/**
	 * Constructor
	 * 
	 * @param iEntity
	 */
	protected DDBSEntity(IEntity iEntity) {
		this(iEntity, ClassInspector.getClassInspector());
	}

	/**
	 * Constructor
	 * 
	 * @param iEntity
	 * @param classInspector
	 */
	protected DDBSEntity(IEntity iEntity, ClassInspector classInspector) {
		this.entityObject = iEntity;
		this.entityName = classInspector.getClassName(iEntity);
		this.entityProperties = classInspector.exploreProperties(iEntity);
	}

	/**
	 * Get DDBSEntity entity
	 * 
	 * @param iEntity
	 *            IEntity
	 */
	public static DDBSEntity<DDBSEntityProperty> getDDBSEntity(IEntity iEntity) {
		return new DDBSEntity<DDBSEntityProperty>(iEntity);
	}
}
