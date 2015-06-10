package org.ddbstoolkit.toolkit.core.reflexion;

import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;

/**
 * DDBS Entity
 * 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DDBSEntity<T extends DDBSEntityProperty> {
	
	/**
	 * Full class Name
	 */
	protected String fullClassName;
	
	/**
	 * Datastore entity name
	 */
	protected String datastoreEntityName;

	/**
	 * Entity properties
	 */
	protected List<T> entityProperties;

	/**
	 * Datastore entity name
	 * 
	 * @return
	 */
	public String getDatastoreEntityName() {
		return datastoreEntityName;
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
	 * Get full class name
	 * @return Full class name
	 */
	public String getFullClassName() {
		return fullClassName;
	}
	
	/**
     * Get Peer UID
     * @param entity Entity
     * @return Peer UID
     */
	public String getPeerUid(IEntity entity) {
		if(entity instanceof DistributedEntity) {
			return ((DistributedEntity)entity).getPeerUid();
		}
		return null;
	}
	
	/**
     * Set Peer UID
     * @param entity Entity
     * @param Peer UID
     */
	public void setPeerUid(IEntity entity, String peerUid) {
		if(entity instanceof DistributedEntity) {
			((DistributedEntity)entity).setPeerUid(peerUid);
		}
	}
	
	/**
	 * Get DDBSEntity entity
	 * 
	 * @param iEntity
	 *            IEntity
	 */
	public static DDBSEntity<DDBSEntityProperty> getDDBSEntity(IEntity iEntity, ClassInspector classInspector) {
		return new DDBSEntity<DDBSEntityProperty>(iEntity, classInspector);
	}
	
	/**
	 * Constructor
	 * 
	 * @param iEntity
	 * @param classInspector
	 */
	protected DDBSEntity(IEntity iEntity, ClassInspector classInspector) {
		this.datastoreEntityName = classInspector.getClassName(iEntity);
		this.fullClassName = classInspector.getFullClassName(iEntity);
		this.entityProperties = classInspector.exploreProperties(iEntity);
	}

	/**
	 * Get Entity properties
	 * 
	 * @return Entity properties
	 */
	public List<DDBSEntityProperty> getSupportedPrimaryTypeEntityProperties() {

		List<DDBSEntityProperty> listWithoutPeerUID = new ArrayList<>();
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
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
	public List<DDBSEntityProperty> getEntityIDProperties() {
		List<DDBSEntityProperty> listIDProperties = new ArrayList<>();
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty.getDdbsEntityIDProperty() != null) {
				listIDProperties.add(ddbsEntityProperty);
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
			if ((ddbsEntityProperty.isIDEntity() && !ddbsEntityProperty.getDdbsEntityIDProperty()
					.isAutoIncrement()) || (!ddbsEntityProperty.isIDEntity()
					&& !ddbsEntityProperty.getDdbsToolkitSupportedEntity()
							.equals(DDBSToolkitSupportedEntity.IENTITY_ARRAY))) {
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
			if (!ddbsEntityProperty.isIDEntity()
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
	@SuppressWarnings("unchecked")
	public T getDDBSEntityProperty(String name) {
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty.getName().equals(name)) {
				return (T)ddbsEntityProperty;
			}
		}
		return null;
	}
}
