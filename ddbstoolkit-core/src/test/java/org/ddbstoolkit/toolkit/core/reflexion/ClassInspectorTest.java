package org.ddbstoolkit.toolkit.core.reflexion;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.model.EntityTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for Class inspector class
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class ClassInspectorTest {
	
    /**
	 * ID Property : null if the field is not an id
	 */
	protected DDBSEntityIDProperty ddbsEntityIDProperty;
	
	@Test
	public void testClassInpector() {
		EntityTest entityTest = new EntityTest();
		entityTest.intField = 1;
		entityTest.integerField = 2;
		entityTest.longField = 3;
		entityTest.longObjectField = new Long(4);
		entityTest.floatField = 5;
		entityTest.floatObjectField = new Float(6);
		entityTest.doubleField = 7;
		entityTest.doubleObjectField = new Double(8);
		entityTest.stringField = "string";
		entityTest.timestampField = new Timestamp(10000);
		entityTest.setPeerUid("uid");
		
		ClassInspector classInspector = new ClassInspector();
		DDBSEntity<DDBSEntityProperty> ddbsEntity = DDBSEntity.getDDBSEntity(entityTest.getClass(), classInspector);
		
		/**
		 * Check entity data
		 */
		Assert.assertEquals(ddbsEntity.getFullClassName(), "org.ddbstoolkit.toolkit.core.model.EntityTest");
		Assert.assertEquals(ddbsEntity.getDatastoreEntityName(), "EntityTest");
		Assert.assertEquals(ddbsEntity.getPeerUid(entityTest), "uid");
		
		DDBSEntityIDProperty ddbsEntityIDProperty = new DDBSEntityIDProperty();
		ddbsEntityIDProperty.setAutoIncrement(false);
		
		/**
		 * Check properties
		 */
		Assert.assertEquals(ddbsEntity.getEntityProperties().size(), 11);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(0), false, "intField", "intField", "int", DDBSToolkitSupportedEntity.INTEGER, 0, ddbsEntityIDProperty);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(1), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, 1, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(2), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, 2, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(3), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,3, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(4), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT, 4, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(5), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,5, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(6), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE, 6, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(7), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE,7, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(8), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING, 8, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(9), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP, 9, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(10), true, "entityField", "entityField", "[Lorg.ddbstoolkit.toolkit.core.model.EntityTest;", DDBSToolkitSupportedEntity.IENTITY_ARRAY, 10, null);
		
		/**
		 * Check values
		 */
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(0).getValue(entityTest), 1);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(1).getValue(entityTest), 2);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(2).getValue(entityTest), 3L);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(3).getValue(entityTest), 4L);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(4).getValue(entityTest), 5f);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(5).getValue(entityTest), 6f);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(6).getValue(entityTest), 7d);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(7).getValue(entityTest), 8d);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(8).getValue(entityTest), "string");
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(9).getValue(entityTest), new Timestamp(10000));
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(10).getValue(entityTest), null);
		
		/**
		 * Check ID properties
		 */
		Assert.assertEquals(ddbsEntity.getEntityIDProperties().size(), 1);
		Assert.assertEquals(ddbsEntity.getEntityIDProperties().get(0).getValue(entityTest), 1);
		
		/**
		 * Check supported primary types
		 */
		Assert.assertEquals(ddbsEntity.getSupportedPrimaryTypeEntityProperties().size(), 10);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(0), false, "intField", "intField", "int", DDBSToolkitSupportedEntity.INTEGER, 0, ddbsEntityIDProperty);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(1), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, 1, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(2), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, 2, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(3), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,3, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(4), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT, 4, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(5), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,5, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(6), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE, 6, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(7), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE,7, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(8), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING, 8, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(9), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP, 9, null);
		
		/**
		 * Check not incrementing entities
		 */
		Assert.assertEquals(ddbsEntity.getNotIncrementingEntityProperties().size(), 10);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(0), false, "intField", "intField", "int", DDBSToolkitSupportedEntity.INTEGER, 0, ddbsEntityIDProperty);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(1), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, 1, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(2), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, 2, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(3), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,3, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(4), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT, 4, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(5), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,5, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(6), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE, 6, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(7), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE,7, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(8), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING, 8, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(9), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP, 9, null);
	
		/**
		 * Check not id entities
		 */
		Assert.assertEquals(ddbsEntity.getEntityNonIDProperties().size(), 9);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(0), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, 1, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(1), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, 2, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(2), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,3, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(3), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT, 4, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(4), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,5, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(5), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE, 6, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(6), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE,7, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(7), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING, 8, null);
	}
	
	private void checkDDBSEntityProperty(DDBSEntityProperty ddbsEntityProperty, boolean isArray, String name, String propertyName, String type, DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity, int fieldIndex, DDBSEntityIDProperty ddbsEntityIDProperty) {
		Assert.assertEquals(ddbsEntityProperty.isArray(), isArray);
		Assert.assertEquals(ddbsEntityProperty.getName(), name);
		Assert.assertEquals(ddbsEntityProperty.getPropertyName(), propertyName);
		Assert.assertEquals(ddbsEntityProperty.getType(), type);
		Assert.assertEquals(ddbsEntityProperty.getDdbsToolkitSupportedEntity(), ddbsToolkitSupportedEntity);
		Assert.assertEquals(ddbsEntityProperty.getFieldIndex(), fieldIndex);
		
		if(ddbsEntityIDProperty != null) {
			Assert.assertEquals(ddbsEntityProperty.getDdbsEntityIDProperty().isAutoIncrement(), ddbsEntityIDProperty.isAutoIncrement());
		} else {
			Assert.assertNull(ddbsEntityProperty.getDdbsEntityIDProperty());
		}
	}
}
