package org.ddbstoolkit.toolkit.core.reflexion;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.junit.Assert;
import org.junit.Test;

public class DDBSToolkitSupportedEntityTest implements IEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class TestClass {
		public int intValue;
		
		public Integer integerValue;
		
		public long longValue;
		
		public Long longObjectValue;
		
		public float floatValue;
		
		public Float floatObjectValue;
		
		public double doubleValue;
		
		public Double doubleObjectValue;
		
		public String stringObjectValue;
		
		public Timestamp timestampObjectValue;
		
		public DDBSToolkitSupportedEntityTest[] entities;
	}
	
	@Test
	public void testDDBSToolkitSupportedEntity() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("intValue")).equals(DDBSToolkitSupportedEntity.INTEGER));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("integerValue")).equals(DDBSToolkitSupportedEntity.INTEGER));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longValue")).equals(DDBSToolkitSupportedEntity.LONG));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longObjectValue")).equals(DDBSToolkitSupportedEntity.LONG));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatValue")).equals(DDBSToolkitSupportedEntity.FLOAT));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatObjectValue")).equals(DDBSToolkitSupportedEntity.FLOAT));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleValue")).equals(DDBSToolkitSupportedEntity.DOUBLE));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleObjectValue")).equals(DDBSToolkitSupportedEntity.DOUBLE));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("stringObjectValue")).equals(DDBSToolkitSupportedEntity.STRING));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("timestampObjectValue")).equals(DDBSToolkitSupportedEntity.TIMESTAMP));
		Assert.assertTrue(DDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("entities")).equals(DDBSToolkitSupportedEntity.IENTITY_ARRAY));
	}
}
