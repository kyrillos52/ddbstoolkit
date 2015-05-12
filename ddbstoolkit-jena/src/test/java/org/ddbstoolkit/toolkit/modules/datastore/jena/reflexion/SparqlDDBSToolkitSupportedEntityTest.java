package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;


import org.ddbstoolkit.toolkit.core.IEntity;
import org.junit.Assert;
import org.junit.Test;

public class SparqlDDBSToolkitSupportedEntityTest {

	private class EntityTest implements IEntity {
		
	}
	
	private class TestClass
	{
		public int intValue;
		
		public Integer integerValue;
		
		public long longValue;
		
		public Long longObjectValue;
		
		public float floatValue;
		
		public Float floatObjectValue;
		
		public double doubleValue;
		
		public Double doubleObjectValue;
		
		public String stringObjectValue;
		
		public EntityTest[] entities;
		
		public Integer[] integerArray;
		
		public Long[] longArray;
		
		public Float[] floatArray;
		
		public Double[] doubleArray;
		
		public String[] stringArray;
	}
	
	@Test
	public void testDDBSToolkitSupportedEntity() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("intValue")).equals(SparqlDDBSToolkitSupportedEntity.INTEGER));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("integerValue")).equals(SparqlDDBSToolkitSupportedEntity.INTEGER));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longValue")).equals(SparqlDDBSToolkitSupportedEntity.LONG));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longObjectValue")).equals(SparqlDDBSToolkitSupportedEntity.LONG));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatValue")).equals(SparqlDDBSToolkitSupportedEntity.FLOAT));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatObjectValue")).equals(SparqlDDBSToolkitSupportedEntity.FLOAT));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleValue")).equals(SparqlDDBSToolkitSupportedEntity.DOUBLE));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleObjectValue")).equals(SparqlDDBSToolkitSupportedEntity.DOUBLE));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("stringObjectValue")).equals(SparqlDDBSToolkitSupportedEntity.STRING));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("entities")).equals(SparqlDDBSToolkitSupportedEntity.IENTITY_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("integerArray")).equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longArray")).equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatArray")).equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleArray")).equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("stringArray")).equals(SparqlDDBSToolkitSupportedEntity.STRING_ARRAY));
	}
}
