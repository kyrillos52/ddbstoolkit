package org.ddbstoolkit.toolkit.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ddbstoolkit.toolkit.core.model.EntityTest;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.orderby.OrderByType;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test Object comparator
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class ObjectComparatorTest {

	@SuppressWarnings("rawtypes")
	private DDBSEntityManager<DDBSEntity> entityManager;
	
	private DDBSEntity<DDBSEntityProperty> ddbsEntity;
	
	@SuppressWarnings("rawtypes")
	@Before
	public void setupManager() {
		
		entityManager = new DDBSEntityManager<DDBSEntity>(new ClassInspector());
		ddbsEntity = entityManager.getDDBSEntity(new EntityTest());
	}
	
	@Test
	public void testInt() {
		
		EntityTest entity1 = new EntityTest();
		entity1.intField = 8;
		
		EntityTest entity2 = new EntityTest();
		entity2.intField = 2;
		
		EntityTest entity3 = new EntityTest();
		entity3.intField = 6;
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("intField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("intField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testInteger() {
		
		EntityTest entity1 = new EntityTest();
		entity1.integerField = 8;
		
		EntityTest entity2 = new EntityTest();
		entity2.integerField = 2;
		
		EntityTest entity3 = new EntityTest();
		entity3.integerField = 6;
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("integerField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("integerField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testLongField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.longField = 8;
		
		EntityTest entity2 = new EntityTest();
		entity2.longField = 2;
		
		EntityTest entity3 = new EntityTest();
		entity3.longField = 6;
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("longField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("longField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testLongObjectField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.longObjectField = new Long(8);
		
		EntityTest entity2 = new EntityTest();
		entity2.longObjectField = new Long(2);
		
		EntityTest entity3 = new EntityTest();
		entity3.longObjectField = new Long(6);
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("longObjectField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("longObjectField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testFloatField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.floatField = 8;
		
		EntityTest entity2 = new EntityTest();
		entity2.floatField = 2;
		
		EntityTest entity3 = new EntityTest();
		entity3.floatField = 6;
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("floatField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("floatField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testFloatObjectField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.floatObjectField = new Float(8);
		
		EntityTest entity2 = new EntityTest();
		entity2.floatObjectField = new Float(2);
		
		EntityTest entity3 = new EntityTest();
		entity3.floatObjectField = new Float(6);
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("floatObjectField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("floatObjectField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testDoubleField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.doubleField = 8;
		
		EntityTest entity2 = new EntityTest();
		entity2.doubleField = 2;
		
		EntityTest entity3 = new EntityTest();
		entity3.doubleField = 6;
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("doubleField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("doubleField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testDoubleObjectField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.doubleObjectField = new Double(8);
		
		EntityTest entity2 = new EntityTest();
		entity2.doubleObjectField = new Double(2);
		
		EntityTest entity3 = new EntityTest();
		entity3.doubleObjectField = new Double(6);
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("doubleObjectField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("doubleObjectField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testStringField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.stringField = new String("8");
		
		EntityTest entity2 = new EntityTest();
		entity2.stringField = new String("2");
		
		EntityTest entity3 = new EntityTest();
		entity3.stringField = new String("6");
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("stringField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("stringField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
	
	@Test
	public void testTimestampField() {
		
		EntityTest entity1 = new EntityTest();
		entity1.timestampField = new Timestamp(8);
		
		EntityTest entity2 = new EntityTest();
		entity2.timestampField = new Timestamp(2);
		
		EntityTest entity3 = new EntityTest();
		entity3.timestampField = new Timestamp(6);
		
		EntityTest entityNoValue = new EntityTest();
		
		List<EntityTest> listEntities = new ArrayList<>();
		listEntities.add(entity1);
		listEntities.add(entity2);
		listEntities.add(entity3);
		listEntities.add(entityNoValue);
		
		ObjectComparator objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("timestampField", OrderByType.ASC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entityNoValue);
		Assert.assertTrue(listEntities.get(1) == entity2);
		Assert.assertTrue(listEntities.get(2) == entity3);
		Assert.assertTrue(listEntities.get(3) == entity1);
		
		objectComparator = new ObjectComparator(ddbsEntity, OrderBy.get("timestampField", OrderByType.DESC));
		Collections.sort(listEntities, objectComparator);
		
		Assert.assertTrue(listEntities.get(0) == entity1);
		Assert.assertTrue(listEntities.get(1) == entity3);
		Assert.assertTrue(listEntities.get(2) == entity2);
		Assert.assertTrue(listEntities.get(3) == entityNoValue);	
	}
}
