package org.ddbstoolkit.toolkit.jdbc;

import java.sql.Timestamp;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.orderby.OrderByType;
import org.ddbstoolkit.toolkit.jdbc.model.Actor;
import org.ddbstoolkit.toolkit.jdbc.model.Film;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit tests for all JDBC Modules
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCModuleTest {
	
	/**
	 * Distributed entity manager
	 */
	protected DistributableEntityManager manager;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	/**
	 * Add receiver peer uid
	 * @param distributedEntity Distributed Entity
	 */
	protected abstract void addReceiverPeerUID(DistributedEntity distributedEntity);
	
	/**
	 * Clean data inside the data source
	 * @throws ClassNotFoundException
	 * @throws DDBSToolkitException
	 */
	public void cleanData() throws ClassNotFoundException, DDBSToolkitException {
		
		for(Actor actor : manager.listAllWithQueryString(new Actor(), null, null))
		{
			manager.delete(actor);
		}
		
		for(Film actor : manager.listAllWithQueryString(new Film(), null, null))
		{
			manager.delete(actor);
		}
	}
	
	public abstract void instantiateManager() throws Exception;
	
    /**
     * Test data source connection
     * @throws Exception
     */
    @Test
    public void testIsOpen() throws Exception {
    	
    	instantiateManager();
    	
    	Assert.assertEquals(manager.isOpen(), false);
    	
        manager.open();

        Assert.assertEquals(manager.isOpen(), true);

        manager.close();

        Assert.assertEquals(manager.isOpen(), false);
    }
    
    /**
     * Test to add a null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testAddNullValue() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	//No object : must return null
        manager.add(null);
    }
    
    /**
     * Test adding elements
     * @throws Exception
     */
    @Test
    public void testAdd() throws Exception {

        int numberOfElement = 0;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);

        //All parameters with no values
        Film filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        Assert.assertTrue(manager.add(filmToAdd));

        testReadLastElement(filmToAdd);

        numberOfElement++;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);

        //Add the string
        filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit 1";
        Assert.assertTrue(manager.add(filmToAdd));

        testReadLastElement(filmToAdd);

        numberOfElement++;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);

        //Add the duration
        filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit 2";
        filmToAdd.duration = 10;

        Assert.assertTrue(manager.add(filmToAdd));

        testReadLastElement(filmToAdd);

        numberOfElement++;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);

        //Add the float
        filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit 3";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);

        Assert.assertTrue(manager.add(filmToAdd));

        testReadLastElement(filmToAdd);

        numberOfElement++;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);

        //Add the long
        filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit 4";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);
        filmToAdd.longField = new Long(100);

        Assert.assertTrue(manager.add(filmToAdd));

        testReadLastElement(filmToAdd);

        numberOfElement++;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);

        //Add the timestamp
        filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit 5";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);
        filmToAdd.longField = new Long(100);
        filmToAdd.creationDate = new Timestamp(100000);

        Assert.assertTrue(manager.add(filmToAdd));

        testReadLastElement(filmToAdd);

        numberOfElement++;
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), numberOfElement);
    }
    
    /**
     * JUnit tests to test that the last element correspond to the last element added
     * @param objectToCheck
     * @throws Exception
     */
    public void testReadLastElement(Film objectToCheck) throws Exception{
    	
    	Film lastFilm = new Film();
    	addReceiverPeerUID(lastFilm);
        lastFilm = manager.readLastElement(lastFilm);
        
        Assert.assertTrue(objectToCheck.film_name == null || objectToCheck.film_name.equals(lastFilm.film_name));
        Assert.assertEquals(objectToCheck.duration, lastFilm.duration);
        Assert.assertTrue(objectToCheck.floatField == lastFilm.floatField);
        Assert.assertEquals(objectToCheck.longField, lastFilm.longField);
        Assert.assertTrue(objectToCheck.creationDate == null || objectToCheck.creationDate.equals(lastFilm.creationDate));
    }
    
    /**
     * Test list all method with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testListAllWithNullValue() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	manager.listAllWithQueryString(null, null, null);
    }

    /**
     * Test list all method
     * @throws Exception
     */
    @Test
    public void testListAll() throws Exception {

        //Select a movie
        Assert.assertNotNull(manager.listAllWithQueryString(new Film(), null, null));

        //Select a movie order by filmID
        Assert.assertNotNull(manager.listAllWithQueryString(new Film(), null, OrderBy.get("film_ID", OrderByType.ASC)));
    }
    
    @Test
    public void testReadNullValue() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	//No object : must return null
        manager.read(null);
    }

    @Test
    public void testRead() throws Exception {

    	Film filmToRead = new Film();
    	
    	addReceiverPeerUID(filmToRead);
        filmToRead.film_ID = -1;

        Assert.assertNull(manager.read(filmToRead));
    	
        Film filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit 5";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);
        filmToAdd.longField = new Long(100);
        filmToAdd.creationDate = new Timestamp(100000);
        
        manager.add(filmToAdd);
        
        //Get last film
        Film lastFilm = new Film();
        addReceiverPeerUID(lastFilm);
        filmToRead = manager.read(manager.readLastElement(lastFilm));

        //Check the fields
        Assert.assertEquals(filmToAdd.film_name, filmToRead.film_name);
        Assert.assertEquals(filmToAdd.duration, filmToRead.duration);
        Assert.assertTrue(filmToAdd.floatField == filmToRead.floatField);
        Assert.assertEquals(filmToAdd.longField, filmToRead.longField);
        Assert.assertEquals(filmToAdd.creationDate, filmToRead.creationDate);
    }
    
    /**
     * Test read element with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testReadLastElementNullValue() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	//No object : must return null
    	manager.readLastElement(null);
    }
    
    /**
     * Test update method with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testUpdateNullValue() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	manager.update(null);
    }

    /**
     * JUnit tests to test the update function
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {

        //If there is no primary key, must return false
        Assert.assertEquals(manager.update(new Film()), false);

        //Add a movie
        Film filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);
        filmToAdd.longField = new Long(30);
        filmToAdd.creationDate = new Timestamp(10000);

        manager.add(filmToAdd);

        //Check the last element
        testReadLastElement(filmToAdd);

        //Modify the last element
        Film lastFilmAdded = new Film();
        addReceiverPeerUID(lastFilmAdded);
        lastFilmAdded = manager.readLastElement(lastFilmAdded);
        lastFilmAdded.film_name = "Test JUnit Updated";
        lastFilmAdded.duration = 20;
        lastFilmAdded.floatField = new Float(30);
        lastFilmAdded.longField = new Long(40);
        lastFilmAdded.creationDate = new Timestamp(20000);

        manager.update(lastFilmAdded);

        //Check the last element
        testReadLastElement(lastFilmAdded);
    }
    
    /**
     * Test delete method with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testDeleteNullValue() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	//No object : must return null
    	manager.delete(null);
    }

    /**
     * Test delete method with null value
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {

        //If there is no primary key, must return false
        Assert.assertEquals(manager.delete(new Film()), false);
        
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), 0);
        
        //Add a movie
        Film filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);
        filmToAdd.longField = new Long(30);
        filmToAdd.creationDate = new Timestamp(10000);

        manager.add(filmToAdd);

        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), 1);
        
        Film lastFilm = new Film();
        addReceiverPeerUID(lastFilm);
        Film filmToDelete = manager.readLastElement(lastFilm);
        
        manager.delete(filmToDelete);

        //Check if all entities has been deleted
        Assert.assertEquals(manager.listAllWithQueryString(new Film(), null, null).size(), 0);
    }
    
    @Test
    public void testLoadArrayNullValue1stParameter() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
    	
    	//No object : must return null
        manager.loadArray(null, null, null);
    }
    
    @Test
    public void testLoadArrayNullValue2ndParameter() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
   
        manager.loadArray(new Film(), null, null);
    }
    
    @Test
    public void testLoadArrayEmptyParameter() throws DDBSToolkitException
    {
    	thrown.expect(IllegalArgumentException.class);
   
        manager.loadArray(new Film(), "", null);
    }
    
    @Test
    public void testLoadArray() throws Exception {
    	
        //Add a movie
        Film filmToAdd = new Film();
        addReceiverPeerUID(filmToAdd);
        filmToAdd.film_name = "Test JUnit";
        filmToAdd.duration = 10;
        filmToAdd.floatField = new Float(20);
        filmToAdd.longField = new Long(30);
        filmToAdd.creationDate = new Timestamp(10000);

        manager.add(filmToAdd);

        //Check the last element
        testReadLastElement(filmToAdd);

        Film lastFilmAdded = new Film();
        addReceiverPeerUID(lastFilmAdded);
        lastFilmAdded = manager.readLastElement(lastFilmAdded);

        //Add 3 actors
        for(int i = 0; i < 3; i++)
        {
            Actor actorToAdd = new Actor();
            addReceiverPeerUID(actorToAdd);
            actorToAdd.actor_name = "actor "+i;
            actorToAdd.film_ID = lastFilmAdded.film_ID;

            manager.add(actorToAdd);
        }

        //Check if the 3 elements have been added
        List<Actor> listActors = manager.listAllWithQueryString(new Actor(), "film_id = "+lastFilmAdded.film_ID, null);
        Assert.assertEquals(listActors.size(), 3);

        //Loader load the array of actors
        lastFilmAdded = manager.loadArray(lastFilmAdded, "actors", OrderBy.get("actor_id", OrderByType.ASC));

        Assert.assertEquals(lastFilmAdded.actors.length, 3);

        //Check that the elements are corrects
        for(int i = 0; i < lastFilmAdded.actors.length; i++)
        {
            Actor anActor = lastFilmAdded.actors[i];
            Assert.assertEquals(anActor.actor_name, "actor "+i);
            Assert.assertEquals(anActor.film_ID, lastFilmAdded.film_ID);
        }
    }

}
