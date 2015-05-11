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
public class DDBSEntity {

	/**
	 * Entity name
	 */
	private String entityName;

	/**
	 * Entity properties
	 */
	private List<DDBSEntityProperty> entityProperties;

	public String getEntityName() {
		return entityName;
	}

	/**
	 * Get Entity properties
	 * 
	 * @return Entity properties
	 */
	public List<DDBSEntityProperty> getEntityProperties() {
		return entityProperties;
	}

	/**
	 * Get Entity properties
	 * 
	 * @return Entity properties
	 */
	public List<DDBSEntityProperty> getEntityPropertiesWithoutPeerUid() {

		List<DDBSEntityProperty> listWithoutPeerUID = new ArrayList<>();
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (!ddbsEntityProperty.isPeerUid()
					&& ddbsEntityProperty.isDDBSToolkitSupportedEntity()) {
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
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
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
	public List<DDBSEntityProperty> getNotIncrementingEntityProperties() {
		List<DDBSEntityProperty> listProperties = new ArrayList<>();
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (!ddbsEntityProperty.isPeerUid()
					&& ddbsEntityProperty.isDDBSToolkitSupportedEntity()
					&& ((ddbsEntityProperty instanceof DDBSEntityIDProperty && !((DDBSEntityIDProperty) ddbsEntityProperty)
							.isAutoIncrement()) || !(ddbsEntityProperty instanceof DDBSEntityIDProperty))) {
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
	public List<DDBSEntityProperty> getEntityNonIDProperties() {
		List<DDBSEntityProperty> listNonIdProperties = new ArrayList<>();
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (!(ddbsEntityProperty instanceof DDBSEntityIDProperty)
					&& !ddbsEntityProperty.isPeerUid() && ddbsEntityProperty.isDDBSToolkitSupportedEntity()) {
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
	public DDBSEntityProperty getDDBSEntityProperty(String propertyName) {
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty.getPropertyName().equals(propertyName)) {
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
	private DDBSEntity(IEntity iEntity) {
		this.entityName = ClassInspector.getClassInspector().getClassName(
				iEntity);
		this.entityProperties = ClassInspector.getClassInspector()
				.exploreProperties(iEntity);
	}

	/**
	 * Get DDBSEntity entity
	 * 
	 * @param iEntity
	 *            IEntity
	 */
	public static DDBSEntity getDDBSEntity(IEntity iEntity) {
		return new DDBSEntity(iEntity);
	}
}
