package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;

import com.esotericsoftware.reflectasm.ConstructorAccess;

/**
 * DDBS Entity
 * 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DDBSEntity<T extends DDBSEntityProperty> {
	
	/**
	 * Class data
	 */
	protected Class<?> classData;

	/**
	 * Entity properties
	 */
	protected List<T> entityProperties;
	
	/**
	 * Datastore entity name
	 */
	protected String datastoreEntityName;
	
	/**
	 * Reflect ASM Constructor Access
	 */
	private ConstructorAccess<?> access;
	
	public Object newInstance() {
		if(access == null) {
			access = ConstructorAccess.get(classData);
		}
		return access.newInstance();
	}

	/**
	 * Datastore entity name
	 * 
	 * @return
	 */
	public String getDatastoreEntityName() {
		return this.datastoreEntityName;
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
	 * Get the full class name of an object
	 * @return Full class name
	 */
	public String getFullClassName() {
		return classData.getName();
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
	public static DDBSEntity<DDBSEntityProperty> getDDBSEntity(Class<?> classData, ClassInspector classInspector) {
		return new DDBSEntity<DDBSEntityProperty>(classData, classInspector);
	}
	
	/**
	 * Constructor
	 * 
	 * @param iEntity
	 * @param classInspector
	 */
	protected DDBSEntity(Class<?> classData, ClassInspector classInspector) {
		this.classData = classData;
		this.entityProperties = classInspector.exploreProperties(classData);
		this.datastoreEntityName = classData.getSimpleName();
		
		AnnotatedElement element = (AnnotatedElement) classData;
        Annotation[] propertyAnnotations = element.getAnnotations();

        for(Annotation annotation : propertyAnnotations)
        {
            if(annotation instanceof EntityName)
            {
                EntityName myProperty = (EntityName)annotation;
                this.datastoreEntityName = myProperty.name();
            }
        }
	}

	/**
	 * Get Entity properties
	 * 
	 * @return Entity properties
	 */
	public List<DDBSEntityProperty> getSupportedPrimaryTypeEntityProperties() {

		List<DDBSEntityProperty> listWithoutPeerUID = new ArrayList<>();
		for (DDBSEntityProperty ddbsEntityProperty : entityProperties) {
			if (ddbsEntityProperty.getDdbsToolkitSupportedEntity() != null && !ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
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
			if (ddbsEntityProperty.isIDEntity() && ddbsEntityProperty.getDdbsToolkitSupportedEntity() != null) {
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
					&& ddbsEntityProperty.getDdbsToolkitSupportedEntity() != null && !ddbsEntityProperty.getDdbsToolkitSupportedEntity()
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
					&& ddbsEntityProperty.getDdbsToolkitSupportedEntity() != null && !ddbsEntityProperty.getDdbsToolkitSupportedEntity()
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

	@Override
	public String toString() {
		return "DDBSEntity [classData=" + classData + ", entityProperties="
				+ entityProperties + ", access=" + access + "]";
	}
}
