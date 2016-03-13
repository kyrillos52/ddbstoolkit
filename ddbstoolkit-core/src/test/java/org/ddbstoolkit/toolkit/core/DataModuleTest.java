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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
	 * JUnit tests to test that the last film element correspond to the last
	 * element added
	 * 
	 * @param objectToCheck
	 * @throws Exception
	 */
	private void testReadLastFilmElement(FilmBase objectToCheck)
			throws Exception {

		FilmBase lastFilm = createFilm();
		addReceiverPeerUID(lastFilm);
		lastFilm = manager.readLastElement(lastFilm);

		compareFilmElement(objectToCheck, lastFilm);
	}

	/**
	 * Compare film element
	 * 
	 * @param referenceObject
	 * @param objectToCheck
	 */
	private void compareFilmElement(FilmBase referenceObject,
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
	 * Test data source connection
	 * 
	 * @throws Exception
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
	 * Test to add a null value
	 * 
	 * @throws DDBSToolkitException
	 */
	@Test
	public void testAddNullValue() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		// No object : must return null
		manager.add(null);
	}

	/**
	 * Test adding an entity
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAdd() throws Exception {

		int numberOfElement = 0;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		// All parameters with no values
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		// Add the string
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 1");
		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		// Add the duration
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 2");
		filmToAdd.setDuration(10);

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		// Add the float
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 3");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		// Add the long
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 4");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(100));

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);

		// Add the timestamp
		filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 5");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(100));
		filmToAdd.setCreationDate(new Timestamp(100000));

		Assert.assertTrue(manager.add(filmToAdd));

		testReadLastFilmElement(filmToAdd);

		numberOfElement++;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
	}

	/**
	 * Test list all method with null value
	 * 
	 * @throws DDBSToolkitException
	 */
	@Test
	public void testListAllWithNullValue() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		manager.listAllWithQueryString(null, null, null);
	}

	/**
	 * Test list all method
	 * 
	 * @throws Exception
	 */
	@Test
	public void testListAll() throws Exception {

		// Select a movie
		Assert.assertNotNull(manager.listAllWithQueryString(createFilm(), null,
				null));

		// Select a movie order by filmID
		Assert.assertNotNull(manager.listAllWithQueryString(createFilm(), null,
				OrderBy.get("filmID", OrderByType.ASC)));
	}

	@Test
	public void testReadNullValue() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		// No object : must return null
		manager.read(null);
	}

	@Test
	public void testRead() throws Exception {

		FilmBase filmToRead = createFilm();

		addReceiverPeerUID(filmToRead);
		filmToRead.setFilmID(-1);

		Assert.assertNull(manager.read(filmToRead));

		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 5");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(100));
		filmToAdd.setCreationDate(new Timestamp(100000));

		manager.add(filmToAdd);

		// Get last film
		FilmBase lastFilm = createFilm();
		addReceiverPeerUID(lastFilm);
		filmToRead = manager.read(manager.readLastElement(lastFilm));

		// Check the fields
		Assert.assertEquals(filmToAdd.getFilmName(), filmToRead.getFilmName());
		Assert.assertEquals(filmToAdd.getDuration(), filmToRead.getDuration());
		Assert.assertEquals(filmToAdd.getFloatField(),filmToRead
				.getFloatField());
		Assert.assertEquals(filmToAdd.getLongField(), filmToRead.getLongField());
		Assert.assertEquals(filmToAdd.getCreationDate(),
				filmToRead.getCreationDate());
	}

	/**
	 * Test read element with null value
	 * 
	 * @throws DDBSToolkitException
	 */
	@Test
	public void testReadLastElementNullValue() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		// No object : must return null
		manager.readLastElement(null);
	}

	/**
	 * Test update method with null value
	 * 
	 * @throws DDBSToolkitException
	 */
	@Test
	public void testUpdateNullValue() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		manager.update(null);
	}

	/**
	 * JUnit tests to test the update function
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {

		// If there is no primary key, must return false
		Assert.assertEquals(manager.update(createFilm()), false);

		// Add a movie
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(30));
		filmToAdd.setCreationDate(new Timestamp(10000));

		manager.add(filmToAdd);

		// Check the last element
		testReadLastFilmElement(filmToAdd);

		// Modify the last element
		FilmBase lastFilmAdded = createFilm();
		addReceiverPeerUID(lastFilmAdded);
		lastFilmAdded = manager.readLastElement(lastFilmAdded);
		lastFilmAdded.setFilmName("Test JUnit Updated");
		lastFilmAdded.setDuration(20);
		lastFilmAdded.setFloatField(new Float(30));
		lastFilmAdded.setLongField(new Long(40));
		lastFilmAdded.setCreationDate(new Timestamp(20000));

		manager.update(lastFilmAdded);

		// Check the last element
		testReadLastFilmElement(lastFilmAdded);
	}

	/**
	 * Test delete method with null value
	 * 
	 * @throws DDBSToolkitException
	 */
	@Test
	public void testDeleteNullValue() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		// No object : must return null
		manager.delete(null);
	}

	/**
	 * Test delete method with null value
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDelete() throws Exception {

		// If there is no primary key, must return false
		Assert.assertEquals(manager.delete(createFilm()), false);

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				0);

		// Add a movie
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(30));
		filmToAdd.setCreationDate(new Timestamp(10000));

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

	@Test
	public void testLoadArrayNullValue1stParameter()
			throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		// No object : must return null
		manager.loadArray(null, null, null);
	}

	@Test
	public void testLoadArrayNullValue2ndParameter()
			throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		manager.loadArray(createFilm(), null, null);
	}

	@Test
	public void testLoadArrayEmptyParameter() throws DDBSToolkitException {
		thrown.expect(IllegalArgumentException.class);

		manager.loadArray(createFilm(), "", null);
	}

	@Test
	public void testLoadArray() throws Exception {

		// Add a movie
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(30));
		filmToAdd.setCreationDate(new Timestamp(10000));

		manager.add(filmToAdd);

		// Check the last element
		testReadLastFilmElement(filmToAdd);

		FilmBase lastFilmAdded = createFilm();
		addReceiverPeerUID(lastFilmAdded);
		lastFilmAdded = manager.readLastElement(lastFilmAdded);

		// Add 3 actors
		for (int i = 0; i < 3; i++) {
			ActorBase actorToAdd = createActor();
			addReceiverPeerUID(actorToAdd);
			actorToAdd.setActorName("actor " + i);
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
		for (int i = 0; i < lastFilmAdded.getActors().length; i++) {
			ActorBase anActor = lastFilmAdded.getActors()[i];
			Assert.assertEquals(anActor.getActorName(), "actor " + i);
			Assert.assertEquals(anActor.getFilmId(), lastFilmAdded.getFilmID());
		}
	}

	/**
	 * Create sample data
	 * 
	 * @throws DDBSToolkitException
	 */
	private Map<String, FilmBase> createSampleData()
			throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = new HashMap<String, FilmBase>();

		FilmBase film1 = createFilm();
		addReceiverPeerUID(film1);
		film1.setFilmName("Film 1");
		film1.setDuration(10);
		film1.setFloatField(new Float(20));
		film1.setLongField(new Long(30));
		film1.setCreationDate(new Timestamp(10000));

		mapFilms.put("film1", film1);
		manager.add(film1);

		FilmBase film2 = createFilm();
		addReceiverPeerUID(film2);
		film2.setFilmName("Film 2");
		film2.setDuration(20);
		film2.setFloatField(new Float(30));
		film2.setLongField(new Long(40));
		film2.setCreationDate(new Timestamp(20000));

		mapFilms.put("film2", film2);
		manager.add(film2);

		FilmBase film3 = createFilm();
		addReceiverPeerUID(film3);
		film3.setFilmName("Film 3");
		film3.setDuration(30);
		film3.setFloatField(new Float(40));
		film3.setLongField(new Long(50));
		film3.setCreationDate(new Timestamp(60000));

		mapFilms.put("film3", film3);
		manager.add(film3);

		FilmBase filmNull = createFilm();
		addReceiverPeerUID(filmNull);

		mapFilms.put("filmNull", filmNull);
		manager.add(filmNull);

		return mapFilms;
	}

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

	@Test
	public void testConditionGreaterBetween() throws DDBSToolkitException {

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

	@Test
	public void testConditionGreaterNotBetween() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.notBetween("duration", 15, 25));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsFloat = Conditions.createConditions().add(
				Conditions.notBetween("floatField", new Float(25),
						new Float(35)));
		results = manager.listAll(createFilm(), conditionEqualsFloat, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsLong = Conditions.createConditions().add(
				Conditions.notBetween("longField", new Long(35), new Long(45)));
		results = manager.listAll(createFilm(), conditionEqualsLong, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));

		Conditions conditionEqualsTimestamp = Conditions.createConditions()
				.add(Conditions.notBetween("creationDate",
						new Timestamp(15000), new Timestamp(25000)));
		results = manager.listAll(createFilm(), conditionEqualsTimestamp, null);
		Assert.assertEquals(results.size(), 2);
		compareFilmElement(mapFilms.get("film1"), results.get(0));
		compareFilmElement(mapFilms.get("film3"), results.get(1));
	}

	@Test
	public void testConditionLike() throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = createSampleData();

		Conditions conditionEqualsInteger = Conditions.createConditions().add(
				Conditions.like("filmName", "%2%"));
		List<FilmBase> results = manager.listAll(createFilm(),
				conditionEqualsInteger, null);
		Assert.assertEquals(results.size(), 1);
		compareFilmElement(mapFilms.get("film2"), results.get(0));
	}

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
	
	@Test
	public void testTransactions() throws DDBSToolkitException {
		
		manager.setAutoCommit(false);
		
		//Test to add an element with commit
		DDBSTransaction transaction1 = new DDBSTransaction("transaction1");
		
		int numberOfElement = 0;

		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		// Add the timestamp
		FilmBase filmToAdd = createFilm();
		addReceiverPeerUID(filmToAdd);
		filmToAdd.setFilmName("Test JUnit 5");
		filmToAdd.setDuration(10);
		filmToAdd.setFloatField(new Float(20));
		filmToAdd.setLongField(new Long(100));
		filmToAdd.setCreationDate(new Timestamp(100000));

		Assert.assertTrue(transaction1.add(filmToAdd));
		
		manager.executeTransaction(transaction1);
		
		numberOfElement++;
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		manager.commit(transaction1);
		
		DDBSTransaction transaction2 = new DDBSTransaction("transaction2");
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		//Test to add an element with rollback
		Assert.assertTrue(transaction2.add(filmToAdd));
		
		manager.executeTransaction(transaction2);
		
		numberOfElement++;
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		manager.rollback(transaction2);
		
		numberOfElement--;
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
	}
}
