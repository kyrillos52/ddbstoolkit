package org.ddbstoolkit.toolkit.core.reflexion;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.model.EntityTestV2;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for Class inspector class
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class ClassInspectorTestV2 {
	
	@Test
	public void testClassInpector()
	{
		EntityTestV2 entityTest = new EntityTestV2();
		entityTest.setIntField(1);
		entityTest.setIntegerField(2);
		entityTest.setLongField(3);
		entityTest.setLongObjectField(new Long(4));
		entityTest.setFloatField(5);
		entityTest.setFloatObjectField(new Float(6));
		entityTest.setDoubleField(7);
		entityTest.setDoubleObjectField(new Double(8));
		entityTest.setStringField("string");
		entityTest.setTimestampField(new Timestamp(10000));
		entityTest.setPeerUid("uid");
		
		ClassInspector classInspector = new ClassInspector();
		DDBSEntity<DDBSEntityProperty> ddbsEntity = DDBSEntity.getDDBSEntity(entityTest.getClass(), classInspector);
		
		/**
		 * Check entity data
		 */
		Assert.assertEquals(ddbsEntity.getFullClassName(), "org.ddbstoolkit.toolkit.core.model.EntityTestV2");
		Assert.assertEquals(ddbsEntity.getDatastoreEntityName(), "EntityTestV2");
		Assert.assertEquals(ddbsEntity.getPeerUid(entityTest), "uid");
		
		DDBSEntityIDProperty ddbsEntityIDProperty = new DDBSEntityIDProperty();
		ddbsEntityIDProperty.setAutoIncrement(false);
		
		/**
		 * Check properties
		 */
		Assert.assertEquals(ddbsEntity.getEntityProperties().size(), 11);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(0), false, "intField", "intField", "int", DDBSToolkitSupportedEntity.INTEGER, ddbsEntityIDProperty);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(1), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(2), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(3), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(4), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(5), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(6), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(7), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(8), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING,  null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(9), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityProperties().get(10), true, "entityField", "entityField", "[Lorg.ddbstoolkit.toolkit.core.model.EntityTestV2;", DDBSToolkitSupportedEntity.IENTITY_ARRAY, null);
		
		/**
		 * Check values
		 */
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(0).getValue(entityTest), 1);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(1).getValue(entityTest), 2);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(2).getValue(entityTest), 3l);
		Assert.assertEquals(ddbsEntity.getEntityProperties().get(3).getValue(entityTest), 4l);
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
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(0), false, "intField", "intField", "int", DDBSToolkitSupportedEntity.INTEGER, ddbsEntityIDProperty);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(1), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(2), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG,  null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(3), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(4), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(5), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(6), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(7), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE, null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(8), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING,null);
		checkDDBSEntityProperty(ddbsEntity.getSupportedPrimaryTypeEntityProperties().get(9), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP,null);
		
		/**
		 * Check not incrementing entities
		 */
		Assert.assertEquals(ddbsEntity.getNotIncrementingEntityProperties().size(), 10);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(0), false, "intField", "intField", "int", DDBSToolkitSupportedEntity.INTEGER, ddbsEntityIDProperty);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(1), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(2), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(3), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(4), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(5), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(6), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(7), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(8), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING,null);
		checkDDBSEntityProperty(ddbsEntity.getNotIncrementingEntityProperties().get(9), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP,null);
	
		/**
		 * Check not id entities
		 */
		Assert.assertEquals(ddbsEntity.getEntityNonIDProperties().size(), 9);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(0), false, "integerField", "customField", "java.lang.Integer", DDBSToolkitSupportedEntity.INTEGER, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(1), false, "longField", "longField", "long", DDBSToolkitSupportedEntity.LONG, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(2), false, "longObjectField", "longObjectField", "java.lang.Long", DDBSToolkitSupportedEntity.LONG, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(3), false, "floatField", "floatField", "float", DDBSToolkitSupportedEntity.FLOAT,  null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(4), false, "floatObjectField", "floatObjectField", "java.lang.Float", DDBSToolkitSupportedEntity.FLOAT, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(5), false, "doubleField", "doubleField", "double", DDBSToolkitSupportedEntity.DOUBLE,  null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(6), false, "doubleObjectField", "doubleObjectField", "java.lang.Double", DDBSToolkitSupportedEntity.DOUBLE, null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(7), false, "stringField", "stringField", "java.lang.String", DDBSToolkitSupportedEntity.STRING,  null);
		checkDDBSEntityProperty(ddbsEntity.getEntityNonIDProperties().get(8), false, "timestampField", "timestampField", "java.sql.Timestamp", DDBSToolkitSupportedEntity.TIMESTAMP,  null);
	
	}
	
	
    /**
	 * ID Property : null if the field is not an id
	 */
	protected DDBSEntityIDProperty ddbsEntityIDProperty;
	
	private void checkDDBSEntityProperty(DDBSEntityProperty ddbsEntityProperty, boolean isArray, String name, String propertyName, String type, DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity, DDBSEntityIDProperty ddbsEntityIDProperty) {
		Assert.assertEquals(ddbsEntityProperty.isArray(), isArray);
		Assert.assertEquals(ddbsEntityProperty.getName(), name);
		Assert.assertEquals(ddbsEntityProperty.getPropertyName(), propertyName);
		Assert.assertEquals(ddbsEntityProperty.getType(), type);
		Assert.assertEquals(ddbsEntityProperty.getDdbsToolkitSupportedEntity(), ddbsToolkitSupportedEntity);
		
		if(ddbsEntityIDProperty != null) {
			Assert.assertEquals(ddbsEntityProperty.getDdbsEntityIDProperty().isAutoIncrement(), ddbsEntityIDProperty.isAutoIncrement());
		} else {
			Assert.assertNull(ddbsEntityProperty.getDdbsEntityIDProperty());
		}
	}
}
