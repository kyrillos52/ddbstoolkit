package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;


import org.ddbstoolkit.toolkit.core.IEntity;
import org.junit.Assert;
import org.junit.Test;

public class SparqlDDBSToolkitSupportedEntityTest implements IEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private class TestClass {
		@SuppressWarnings("unused")
		public int intValue;
		
		@SuppressWarnings("unused")
		public Integer integerValue;
		
		@SuppressWarnings("unused")
		public long longValue;
		
		@SuppressWarnings("unused")
		public Long longObjectValue;
		
		@SuppressWarnings("unused")
		public float floatValue;
		
		@SuppressWarnings("unused")
		public Float floatObjectValue;
		
		@SuppressWarnings("unused")
		public double doubleValue;
		
		@SuppressWarnings("unused")
		public Double doubleObjectValue;
		
		@SuppressWarnings("unused")
		public String stringObjectValue;
		
		@SuppressWarnings("unused")
		public SparqlDDBSToolkitSupportedEntityTest[] entities;
		
		@SuppressWarnings("unused")
		public Integer[] integerArrayObject;
		
		@SuppressWarnings("unused")
		public Long[] longArrayObject;
		
		@SuppressWarnings("unused")
		public Float[] floatArrayObject;
		
		@SuppressWarnings("unused")
		public Double[] doubleArrayObject;
		
		@SuppressWarnings("unused")
		public String[] stringArrayObject;
		
		@SuppressWarnings("unused")
		public int[] integerArray;
		
		@SuppressWarnings("unused")
		public long[] longArray;
		
		@SuppressWarnings("unused")
		public float[] floatArray;
		
		@SuppressWarnings("unused")
		public double[] doubleArray;
	}
	
	@Test
	public void testDDBSToolkitSupportedEntity() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
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
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("integerArrayObject")).equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longArrayObject")).equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatArrayObject")).equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleArrayObject")).equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("integerArray")).equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("longArray")).equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("floatArray")).equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("doubleArray")).equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY));
		Assert.assertTrue(SparqlDDBSToolkitSupportedEntity.valueOf(new TestClass().getClass().getField("stringArrayObject")).equals(SparqlDDBSToolkitSupportedEntity.STRING_ARRAY));
	}
}
