package org.ddbstoolkit.toolkit.core;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.orderby.OrderByType;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for all data Modules
 * 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class DataModuleTest {

	/**
	 * Distributed entity manager
	 */
	protected DistributableEntityManager manager;

	/**
	 * Instantiate a distributable entity manager
	 * 
	 * @throws Exception
	 */
	public abstract void instantiateManager() throws Exception;

	/**
	 * Add receiver peer uid
	 * 
	 * @param distributedEntity
	 *            Distributed Entity
	 */
	protected abstract void addReceiverPeerUID(IEntity iEntity);

	/**
	 * Create an empty film object
	 * 
	 * @return an empty film object
	 */
	protected abstract FilmBase createFilm();

	/**
	 * Create an empty actor
	 * 
	 * @return An empty actor
	 */
	protected abstract ActorBase createActor();
	
	/**
	 * Get the string expression that match contains "2" in the film name
	 * @return String like expression
	 */
	protected abstract String getLikeExpression();
	
	/**
	 * Create an empty film object
	 * 
	 * @return an empty film object
	 */
	protected abstract FilmBase createFilm(Integer filmID, String filmName, Integer duration,
			Timestamp creationDate, Long longField, Float floatField);

	/**
	 * Create an empty actor
	 * 
	 * @return An empty actor
	 */
	protected abstract ActorBase createActor(Integer actorId, String actorName, Integer filmId);

	/**
	 * Clean data inside the data source
	 * 
	 * @throws ClassNotFoundException
	 * @throws DDBSToolkitException
	 */
	@Before
	public void setUp() throws Exception {

		instantiateManager();
		
		if(!manager.isOpen()) {
			manager.open();
		}
		
		for (ActorBase actor : manager.listAllWithQueryString(
				createActor(), null, null)) {
			manager.delete(actor);
		}

		for (FilmBase actor : manager.listAllWithQueryString(createFilm(),
				null, null)) {
			manager.delete(actor);
		}
	}

	/**
	 * JUnit tests to check that the last film element corresponds 
	 * to the object in parameter
	 * 
	 * @param objectToCheck Object to check
	 * @throws DDBSToolkitException Toolkit exception
	 */
	protected void testReadLastFilmElement(FilmBase objectToCheck) throws DDBSToolkitException {

		FilmBase lastFilm = createFilm();
		addReceiverPeerUID(lastFilm);
		lastFilm = manager.readLastElement(lastFilm);

		compareFilmElement(objectToCheck, lastFilm);
	}

	/**
	 * Compare a reference object with an object to check
	 * 
	 * @param referenceObject Reference object
	 * @param objectToCheck Object to check
	 */
	protected void compareFilmElement(FilmBase referenceObject,
			FilmBase objectToCheck) {

		Assert.assertEquals( objectToCheck.getFilmName(),referenceObject.getFilmName());
		Assert.assertEquals(objectToCheck.getDuration(),
				referenceObject.getDuration());
		Assert.assertEquals(objectToCheck.getFloatField(), referenceObject
				.getFloatField());
		Assert.assertEquals(objectToCheck.getLongField(),
				referenceObject.getLongField());
		Assert.assertEquals(objectToCheck.getCreationDate(),referenceObject.getCreationDate());
	}

	/**
	 * Test data source connections
	 * 
	 * @throws Exception Exception thrown
	 */
	@Test
	public void testIsOpen() throws Exception {

		instantiateManager();

		manager.close();
		
		Assert.assertEquals(manager.isOpen(), false);

		manager.open();

		Assert.assertEquals(manager.isOpen(), true);

		manager.close();

		Assert.assertEquals(manager.isOpen(), false);
	}

	/**
	 * Test the add() method with a null value
	 * We should throw an illegal argument exception
	 * 
	 * @throws DDBSToolkitException
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testAddNullValue() throws DDBSToolkitException {
		manager.add(null);
	}

	/**
	 * Test the add() method of entities with different values
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testAdd() throws DDBSToolkitException {

		//Expected no entities
		int numberOfElement = 0;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		//We add an empty object with no parameter set
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		//We add an object with only the film name set
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 1");
		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		//We add an object with only the film name and duration set
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 2");
		filmToAdd.setDuration(20);

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		//We add an object with only the film name, the duration and the float field set
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 3");
		filmToAdd.setDuration(30);
		filmToAdd.setFloatField(new Float(300));

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		//We add an object with only the film name, the duration, the float field and the long field set
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 4");
		filmToAdd.setDuration(40);
		filmToAdd.setFloatField(new Float(400));
		filmToAdd.setLongField(new Long(4000));

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		//We add an object with all parameters set
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 5");
		filmToAdd.setDuration(50);
		filmToAdd.setFloatField(new Float(50));
		filmToAdd.setLongField(new Long(500));
		filmToAdd.setCreationDate(new Timestamp(50000));

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
	}

	/**
	 * Test list() method with null value
	 * We expect to have an illegal argument exception
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testListAllWithNullValue() throws DDBSToolkitException {
		manager.listAllWithQueryString(null, null, null);
	}

	/**
	 * Test list() all method with no data
	 * @throws DDBSToolkitException DDBS Toolkit exceptions
	 */
	@Test
	public void testListAll() throws DDBSToolkitException {

		Assert.assertNotNull(manager.listAllWithQueryString(createFilm(), null,
				null));
		Assert.assertEquals(manager.listAllWithQueryString(createFilm(), null,
				null).size(), 0);

		Assert.assertNotNull(manager.listAllWithQueryString(createFilm(), null,
				OrderBy.get("filmID", OrderByType.ASC)));
		Assert.assertEquals(manager.listAllWithQueryString(createFilm(), null,
				OrderBy.get("filmID", OrderByType.ASC)).size(), 0);
	}

	/**
	 * Test read() method with null value
	 * We expect to have an illegal argument exception
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testReadNullValue() throws DDBSToolkitException {
		manager.read(null);
	}

	/**
	 * Test read() method
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testRead() throws DDBSToolkitException {

		//Test to retrieve an element with no data
		FilmBase filmToRead = createFilm();

		addReceiverPeerUID(filmToRead);
		filmToRead.setFilmID(-1);

		Assert.assertNull(manager.read(filmToRead));

		//Add an element
		FilmBase filmToAdd = createFilm(1, "Test JUnit 1", 10,
				new Timestamp(10000), new Long(1000), new Float(100));

		manager.add(filmToAdd);

		//Read the last element
		FilmBase lastFilm = createFilm();
		addReceiverPeerUID(lastFilm);
		filmToRead = manager.read(manager.readLastElement(lastFilm));

		compareFilmElement(filmToAdd, filmToRead);
	}

	/**
	 * Test read() method with null value
	 * We expect to have an illegal argument exception
	 * 
	 * @throws DDBSToolkitException DDBS Toolkit Exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testReadLastElementNullValue() throws DDBSToolkitException {
		manager.readLastElement(null);
	}

	/**
	 * Test update() method with null value
	 * We expect to have an illegal argument exception
	 * 
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testUpdateNullValue() throws DDBSToolkitException {
		manager.update(null);
	}

	/**
	 * Test update() method
	 * @throws DDBSToolkitException Toolkit exception
	 */
	@Test
	public void testUpdate() throws DDBSToolkitException {

		// If there is no primary key, must return false
		Assert.assertEquals(manager.update(createFilm()), false);

		// Add a movie
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 1");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(100));
		filmToAdd.setLongField(new Long(1000));
		filmToAdd.setCreationDate(new Timestamp(10000));

		manager.add(filmToAdd);

		//Check the last element
		testReadLastFilmElement(filmToAdd);

		// Modify the last element
		FilmBase lastFilmAdded = createFilm();
		addReceiverPeerUID(lastFilmAdded);
		lastFilmAdded = manager.readLastElement(lastFilmAdded);
		lastFilmAdded.setFilmName("Test JUnit Updated");
		lastFilmAdded.setDuration(20);
		lastFilmAdded.setFloatField(new Float(200));
		lastFilmAdded.setLongField(new Long(2000));
		lastFilmAdded.setCreationDate(new Timestamp(20000));

		manager.update(lastFilmAdded);

		// Check the last element
		testReadLastFilmElement(lastFilmAdded);
	}

	/**
	 * Test delete() method with null value
	 * 
	 * @throws DDBSToolkitException Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testDeleteNullValue() throws DDBSToolkitException {
		manager.delete(null);
	}

	/**
	 * Test delete() method
	 * @throws DDBSToolkitException DDBS Toolkit Exception
	 */
	@Test
	public void testDelete() throws DDBSToolkitException  {

		// If there is no primary key, must return false
		Assert.assertEquals(manager.delete(createFilm()), false);

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				0);

		// Add a movie
		FilmBase filmToAdd = createFilm(1, "Test JUnit", 10,
				new Timestamp(10000), new Long(1000), new Float(100));
		addReceiverPeerUID(filmToAdd);

		manager.add(filmToAdd);

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				1);

		FilmBase lastFilm = createFilm();
		addReceiverPeerUID(lastFilm);
		FilmBase filmToDelete = manager.readLastElement(lastFilm);

		manager.delete(filmToDelete);

		// Check if all entities has been deleted
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				0);
	}

	/**
	 * Test loadArray() method with null value in 1st parameter
	 * We expect to have an illegal argument exception
	 * 
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testLoadArrayNullValue1stParameter()
			throws DDBSToolkitException {
		// No object : must return null
		manager.loadArray(null, null, null);
	}

	/**
	 * Test loadArray() method with null value in 2nd parameter
	 * We expect to have an illegal argument exception
	 * 
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testLoadArrayNullValue2ndParameter()
			throws DDBSToolkitException {
		manager.loadArray(createFilm(), null, null);
	}

	/**
	 * Test loadArray() method with empty value in 2nd parameter
	 * We expect to have an illegal argument exception
	 * 
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testLoadArrayEmptyParameter() throws DDBSToolkitException {
		manager.loadArray(createFilm(), "", null);
	}

	/**
	 * Test loadArray() method
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testLoadArray() throws DDBSToolkitException {

		// Add a movie
		FilmBase filmToAdd = createFilm(1, "Test JUnit", 10,
				new Timestamp(10000), new Long(30), new Float(20));
		addReceiverPeerUID(filmToAdd);

		manager.add(filmToAdd);

		// Check the last element
		testReadLastFilmElement(filmToAdd);

		FilmBase lastFilmAdded = createFilm();
		addReceiverPeerUID(lastFilmAdded);
		lastFilmAdded = manager.readLastElement(lastFilmAdded);

		// Add 3 actors
		for (int counterActor = 0; counterActor < 3; counterActor++) {
			ActorBase actorToAdd = createActor();
			addReceiverPeerUID(actorToAdd);
			actorToAdd.setActorName("actor " + counterActor);
			actorToAdd.setFilmId(lastFilmAdded.getFilmID());

			manager.add(actorToAdd);
		}

		// Check if the 3 elements have been added
		List<ActorBase> listActors = manager.listAllWithQueryString(
				createActor(), "film_id = " + lastFilmAdded.getFilmID(), null);
		Assert.assertEquals(listActors.size(), 3);

		// Loader load the array of actors
		lastFilmAdded = manager.loadArray(lastFilmAdded, "actors",
				OrderBy.get("actorId", OrderByType.ASC));

		Assert.assertEquals(lastFilmAdded.getActors().length, 3);

		// Check that the elements are corrects
		for (int counterActor = 0; counterActor < lastFilmAdded.getActors().length; counterActor++) {
			ActorBase anActor = lastFilmAdded.getActors()[counterActor];
			Assert.assertEquals(anActor.getActorName(), "actor " + counterActor);
			Assert.assertEquals(anActor.getFilmId(), lastFilmAdded.getFilmID());
		}
	}

	/**
	 * Create sample data
	 * 
	 * @throws DDBSToolkitException
	 */
	protected Map<String, FilmBase> createSampleData()
			throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = new HashMap<String, FilmBase>();

		FilmBase film1 = createFilm(1, "Film 1", 10,
				new Timestamp(10000), new Long(30), new Float(20));
		addReceiverPeerUID(film1);

		mapFilms.put("film1", film1);
		manager.add(film1);

		FilmBase film2 = createFilm(2, "Film 2", 20,
				new Timestamp(20000), new Long(40), new Float(30));
		addReceiverPeerUID(film2);

		mapFilms.put("film2", film2);
		manager.add(film2);

		FilmBase film3 = createFilm(3, "Film 3", 30,
				new Timestamp(60000), new Long(50), new Float(40));
		addReceiverPeerUID(film3);

		mapFilms.put("film3", film3);
		manager.add(film3);

		FilmBase filmNull = createFilm(4, null, null,
				null, null, null);

		mapFilms.put("filmNull", filmNull);
		manager.add(filmNull);

		return mapFilms;
	}

	/**
	 * Test list() method with equal condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionEquals() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsString = Conditions.createConditions().add(
				Conditions.eq("filmName", "Film 2"));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsString, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.eq("duration", 20));
		results = manager.listAll(createFilm(), conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.eq("floatField", new Float(30)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.eq("longField", new Long(40)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.eq("creationDate", new Timestamp(20000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
	}

	/**
	 * Test list() method with not equal condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionNotEquals() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionNotEqualsString = Conditions.createConditions()
				.add(Conditions.ne("filmName", "Film 2"));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionNotEqualsString, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionNotEqualsInteger = Conditions.createConditions()
				.add(Conditions.ne("duration", 20));
		results = manager
				.listAll(createFilm(), conditionNotEqualsInteger, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionNotEqualsFloat = Conditions.createConditions().add(
				Conditions.ne("floatField", new Float(30)));
		results = manager.listAll(createFilm(), conditionNotEqualsFloat, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionNotEqualsLong = Conditions.createConditions().add(
				Conditions.ne("longField", new Long(40)));
		results = manager.listAll(createFilm(), conditionNotEqualsLong, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionNotEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.ne("creationDate", new Timestamp(20000)));
		results = manager.listAll(createFilm(), conditionNotEqualsTimestamp,
				null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
	}

	/**
	 * Test list() method with less than condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionLessThan() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.lt("duration", 20));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film1"), results.get(0));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.lt("floatField", new Float(30)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film1"), results.get(0));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.lt("longField", new Long(40)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film1"), results.get(0));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.lt("creationDate", new Timestamp(20000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
	}

	/**
	 * Test list() method with greater than condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionGreaterThan() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.gt("duration", 20));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film3"), results.get(0));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.gt("floatField", new Float(30)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film3"), results.get(0));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.gt("longField", new Long(40)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film3"), results.get(0));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.gt("creationDate", new Timestamp(20000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film3"), results.get(0));
	}

	/**
	 * Test list() method with less than or equal condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionLessThanOrEqual() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.le("duration", 20));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.le("floatField", new Float(30)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.le("longField", new Long(40)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.le("creationDate", new Timestamp(20000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));
	}

	/**
	 * Test list() method with greater than or equal condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionGreaterThanOrEqual() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.ge("duration", 20));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.ge("floatField", new Float(30)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.ge("longField", new Long(40)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.ge("creationDate", new Timestamp(20000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
	}

	/**
	 * Test list() method with between condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionBetween() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.between("duration", 15, 25));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.between("floatField", new Float(25), new Float(35)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.between("longField", new Long(35), new Long(45)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.between("creationDate", new Timestamp(15000),
						new Timestamp(25000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
	}

	/**
	 * Test list() method with not between condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionNotBetween() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.notBetween("duration", 15, 25));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, OrderBy.get("filmName", OrderByType.ASC));
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.notBetween("floatField", new Float(25),
						new Float(35)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, OrderBy.get("filmName", OrderByType.ASC));
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.notBetween("longField", new Long(35), new Long(45)));
		results = manager.listAll(createFilm(), conditionEqualsLong, OrderBy.get("filmName", OrderByType.ASC));
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.notBetween("creationDate",
						new Timestamp(15000), new Timestamp(25000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, OrderBy.get("filmName", OrderByType.ASC));
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
	}

	/**
	 * Test list() method with like condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionLike() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.like("filmName", getLikeExpression()));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
	}

	/**
	 * Test list() method with in condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionIn() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsString = Conditions.createConditions().add(
				Conditions.in("filmName", new String[]{"Film 2"}));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsString, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.in("duration", new Integer[]{20}));
		results = manager.listAll(createFilm(), conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.in("floatField", new Float[]{30f}));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.in("longField", new Long[]{40l}));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.in("creationDate", new Timestamp[] { new Timestamp(20000)}));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
	}
	
	/**
	 * Test list() method with not in condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionNotIn() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsString = Conditions.createConditions().add(
				Conditions.notIn("filmName", new String[]{"Film 2"}));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsString, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
		
		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.notIn("duration", new Integer[]{20}));
		results = manager.listAll(createFilm(), conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
		
		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.notIn("floatField", new Float[]{30f}));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.notIn("longField", new Long[]{40l}));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.notIn("creationDate", new Timestamp[] { new Timestamp(20000)}));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
	}

	/**
	 * Test list() method with is null condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionIsNull() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsString = Conditions.createConditions().add(
				Conditions.isNull("filmName"));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsString, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("filmNull"), results.get(0));

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.isNull("duration"));
		results = manager.listAll(createFilm(), conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("filmNull"), results.get(0));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.isNull("floatField"));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("filmNull"), results.get(0));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.isNull("longField"));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("filmNull"), results.get(0));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.isNull("creationDate"));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("filmNull"), results.get(0));
	}
	
	/**
	 * Test list() method with is not null condition
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testConditionNotNull() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsString = Conditions.createConditions().add(
				Conditions.isNotNull("filmName"));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsString, null);
		Assert.assertEquals(results.size(), 3);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));
		compareFilmElement(mapFilms.get("film3"), results.get(2));
		
		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.isNotNull("duration"));
		results = manager.listAll(createFilm(), conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 3);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));
		compareFilmElement(mapFilms.get("film3"), results.get(2));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.isNotNull("floatField"));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 3);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));
		compareFilmElement(mapFilms.get("film3"), results.get(2));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.isNotNull("longField"));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 3);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));
		compareFilmElement(mapFilms.get("film3"), results.get(2));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.isNotNull("creationDate"));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 3);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film2"), results.get(1));
		compareFilmElement(mapFilms.get("film3"), results.get(2));
	}
	
	/**
	 * Test executeTransaction() method with add transaction
	 * Test commit and rollback conditions
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testTransactionsAdd() throws DDBSToolkitException {
		
		manager.setAutoCommit(false);
		
		//Test to add an element with commit
		DDBSTransaction transactionAddCommit = new DDBSTransaction("transactionAddCommit");
		
		int numberOfElement = 0;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		FilmBase filmToAdd = createFilm(1, "Test JUnit 5", 10,
				new Timestamp(100000), new Long(100), new Float(20));
		addReceiverPeerUID(filmToAdd);
		
		Assert.assertTrue(transactionAddCommit.add(filmToAdd));
		
		manager.executeTransaction(transactionAddCommit);
		
		numberOfElement++;
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		manager.commit(transactionAddCommit);
		
		DDBSTransaction transactionAddRollback = new DDBSTransaction("transactionAddRollback");
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		//Test to add an element with rollback
		Assert.assertTrue(transactionAddRollback.add(filmToAdd));
		
		manager.executeTransaction(transactionAddRollback);
		
		numberOfElement++;
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		manager.rollback(transactionAddRollback);
		
		numberOfElement--;
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
	}
	
	/**
	 * Test executeTransaction() method with update transaction
	 * Test commit and rollback conditions
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testTransactionUpdate() throws Exception {
		
		FilmBase originalFilm = createFilm(1, "Test JUnit original", 10,
				new Timestamp(10000), new Long(1000), new Float(100));
		
		manager.add(originalFilm);
		
		manager.setAutoCommit(false);
		
		//Test to update an element with commit
		DDBSTransaction transactionUpdateCommit = new DDBSTransaction("transactionUpdateCommit");
		
		FilmBase filmToUpdate = manager.readLastElement(createFilm());
		filmToUpdate.setFilmName("Test JUnit update commit");
		filmToUpdate.setDuration(20);
		filmToUpdate.setFloatField(new Float(200));
		filmToUpdate.setLongField(new Long(2000));
		filmToUpdate.setCreationDate(new Timestamp(20000));
		
		Assert.assertTrue(transactionUpdateCommit.update(filmToUpdate));
		
		manager.executeTransaction(transactionUpdateCommit);
		
		manager.commit(transactionUpdateCommit);
		
		testReadLastFilmElement(filmToUpdate);
		
		//Test to update an element with rollback
		DDBSTransaction transactionUpdateRollback = new DDBSTransaction("transactionUpdateRollback");
		
		FilmBase filmTryToUpdate = manager.readLastElement(createFilm());
		filmTryToUpdate.setFilmName("Test JUnit update rollback");
		filmTryToUpdate.setDuration(30);
		filmTryToUpdate.setFloatField(new Float(300));
		filmTryToUpdate.setLongField(new Long(3000));
		filmTryToUpdate.setCreationDate(new Timestamp(30000));
		
		Assert.assertTrue(transactionUpdateRollback.update(filmTryToUpdate));
		
		manager.executeTransaction(transactionUpdateRollback);
		
		manager.rollback(transactionUpdateRollback);
		
		testReadLastFilmElement(filmToUpdate);
	}
	
	/**
	 * Test executeTransaction() method with delete transaction
	 * Test commit and rollback conditions
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Test
	public void testTransactionDelete() throws Exception {
		
		FilmBase originalFilm1 = createFilm(1, "Test JUnit original 1", 10,
				new Timestamp(10000), new Long(1000), new Float(100));
		
		FilmBase originalFilm2 = createFilm(2, "Test JUnit original 2", 20,
				new Timestamp(20000), new Long(2000), new Float(200));
		
		manager.add(originalFilm1);
		manager.add(originalFilm2);
		
		manager.setAutoCommit(false);
		
		//Delete transaction with rollback
		DDBSTransaction transactionDeleteRollback = new DDBSTransaction("transactionDeleteRollback");
		
		FilmBase filmTryToDelete = manager.readLastElement(createFilm());
		
		Assert.assertTrue(transactionDeleteRollback.delete(filmTryToDelete));
		
		manager.executeTransaction(transactionDeleteRollback);
		
		manager.rollback(transactionDeleteRollback);
		
		testReadLastFilmElement(originalFilm2);
		
		//Delete transaction with commit
		DDBSTransaction transactionDeleteCommit = new DDBSTransaction("transactionDeleteCommit");
		
		FilmBase filmToDelete = manager.readLastElement(createFilm());
		
		Assert.assertTrue(transactionDeleteCommit.delete(filmToDelete));
		
		manager.executeTransaction(transactionDeleteCommit);
		
		manager.commit(transactionDeleteCommit);
		
		testReadLastFilmElement(originalFilm1);
	}
}
