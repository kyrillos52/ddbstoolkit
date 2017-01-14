package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.sql.Timestamp;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DDBSTransaction;
import org.ddbstoolkit.toolkit.core.DataModuleTest;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.orderby.OrderByType;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.ActorDatastore;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Book;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Company;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Film;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.FilmDatastore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * JUnit tests to test Jena module
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitJenaModuleTest extends DataModuleTest {
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@After
	public void closeConnection() throws DDBSToolkitException {
		manager.close();
	}
    
	@Override
	public void instantiateManager() {
		manager = new DistributedSPARQLManager(true);
	}
	
	@Before
	@Override
	public void setUp() throws Exception {
		
		instantiateManager();
		
		if(!manager.isOpen()) {
			manager.open();
		}
	}
	
	@After
	public void tearDown() throws Exception {

		if(manager.isOpen()) {
			manager.close();
		}
	}

    /**
     * JUnit tests for adding
     * @throws DDBSToolkitException Toolkit exception
     */
    @Test
    public void testAdd() throws DDBSToolkitException {

        Assert.assertEquals(manager.listAllWithQueryString(new Company(), null, null).size(), 0);

        //Bad parameters
        Company companyToTest = new Company();

        companyToTest.company_uri = "http://cyril-grandjean.co.uk/business/CompanyTest";
        Assert.assertTrue(manager.add(companyToTest));
        
        Assert.assertEquals(manager.listAllWithQueryString(new Company(), null, null).size(), 0);

        manager.delete(companyToTest);
        
        Assert.assertEquals(manager.listAllWithQueryString(new Company(), null, null).size(), 0);

        //Add a company
        Company companyToAdd = new Company();
        companyToAdd.company_uri = "http://cyril-grandjean.co.uk/business/Microsoft";
        companyToAdd.company_ID = 2;
        companyToAdd.company_name = "Microsoft";
        companyToAdd.floatField = 20;
        companyToAdd.longField = 30;
        companyToAdd.surface = 30;

        //Set 2 boss
        String[] boss = new String[2];
        boss[0] = "Bill Gates";
        boss[1] = "Steve Ballmer";
        companyToAdd.boss = boss;

        //Set 2 ints
        int[] mark = new int[2];
        mark[0] = 1;
        mark[1] = 2;
        companyToAdd.mark = mark;

        //Set 2 longs
        long[] number1 = new long[2];
        number1[0] = 3;
        number1[1] = 4;
        companyToAdd.number1 = number1;

        //Set 2 floats
        float[] number2 = new float[2];
        number2[0] = 5;
        number2[1] = 6;
        companyToAdd.number2 = number2;

        manager.add(companyToAdd);

        Assert.assertEquals(manager.listAllWithQueryString(new Company(), null, null).size(), 1);

        Company myCompany = manager.readLastElement(new Company());

        Assert.assertEquals(companyToAdd.company_uri, myCompany.company_uri);
        Assert.assertEquals(companyToAdd.company_ID, myCompany.company_ID);
        Assert.assertEquals(companyToAdd.company_name, myCompany.company_name);
        Assert.assertTrue(companyToAdd.floatField == myCompany.floatField);
        Assert.assertEquals(companyToAdd.longField, myCompany.longField);
        Assert.assertEquals(companyToAdd.surface, myCompany.surface);

        Assert.assertEquals(companyToAdd.boss[0], myCompany.boss[0]);
        Assert.assertEquals(companyToAdd.boss[1], myCompany.boss[1]);

        Assert.assertEquals(companyToAdd.mark[0], myCompany.mark[0]);
        Assert.assertEquals(companyToAdd.mark[1], myCompany.mark[1]);

        Assert.assertEquals(companyToAdd.number1[0], myCompany.number1[0]);
        Assert.assertEquals(companyToAdd.number1[1], myCompany.number1[1]);

        Assert.assertTrue(companyToAdd.number2[0] == myCompany.number2[0]);
        Assert.assertTrue(companyToAdd.number2[1] == myCompany.number2[1]);
    }
    
    /**
     * JUnit tests to test the update function
     * @throws DDBSToolkitException Toolkit exception
     */
    @Test
    public void testUpdate() throws DDBSToolkitException {

        //Add a company
        Company companyToAdd = new Company();
        companyToAdd.company_uri = "http://cyril-grandjean.co.uk/business/Microsoft";
        companyToAdd.company_ID = 2;
        companyToAdd.company_name = "Microsoft";
        companyToAdd.floatField = 20;
        companyToAdd.longField = 30;
        companyToAdd.surface = 30;

        //Set 2 boss
        String[] boss = new String[2];
        boss[0] = "Bill Gates";
        boss[1] = "Steve Ballmer";
        companyToAdd.boss = boss;

        //Set 2 ints
        int[] mark = new int[2];
        mark[0] = 1;
        mark[1] = 2;
        companyToAdd.mark = mark;

        //Set 2 longs
        long[] number1 = new long[2];
        number1[0] = 3;
        number1[1] = 4;
        companyToAdd.number1 = number1;

        //Set 2 floats
        float[] number2 = new float[2];
        number2[0] = 5;
        number2[1] = 6;
        companyToAdd.number2 = number2;

        manager.add(companyToAdd);

        Company companyToUpdate = new Company();
        companyToUpdate.company_uri = "http://cyril-grandjean.co.uk/business/Microsoft";
        companyToUpdate.company_ID = 3;
        companyToUpdate.company_name = "Microsoft Corporation";
        companyToUpdate.floatField = 30;
        companyToUpdate.longField = 40;
        companyToUpdate.surface = 50;

        //Set 2 boss
        boss = new String[2];
        boss[0] = "Bill Gates Updated";
        boss[1] = "Steve Ballmer Updated";
        companyToUpdate.boss = boss;

        //Set 2 ints
        mark = new int[2];
        mark[0] = 3;
        mark[1] = 4;
        companyToUpdate.mark = mark;

        //Set 2 longs
        number1 = new long[2];
        number1[0] = 5;
        number1[1] = 6;
        companyToUpdate.number1 = number1;

        //Set 2 floats
        number2 = new float[2];
        number2[0] = 5;
        number2[1] = 6;
        companyToUpdate.number2 = number2;

        manager.update(companyToUpdate);

        Company myCompany = manager.readLastElement(new Company());

        Assert.assertEquals(companyToUpdate.company_uri, myCompany.company_uri);
        Assert.assertEquals(companyToUpdate.company_ID, myCompany.company_ID);
        Assert.assertEquals(companyToUpdate.company_name, myCompany.company_name);
        Assert.assertTrue(companyToUpdate.floatField == myCompany.floatField);
        Assert.assertEquals(companyToUpdate.longField, myCompany.longField);
        Assert.assertEquals(companyToUpdate.surface, myCompany.surface);

        Assert.assertEquals(companyToUpdate.boss[0], myCompany.boss[0]);
        Assert.assertEquals(companyToUpdate.boss[1], myCompany.boss[1]);

        Assert.assertEquals(companyToUpdate.mark[0], myCompany.mark[0]);
        Assert.assertEquals(companyToUpdate.mark[1], myCompany.mark[1]);

        Assert.assertEquals(companyToUpdate.number1[0], myCompany.number1[0]);
        Assert.assertEquals(companyToUpdate.number1[1], myCompany.number1[1]);

        Assert.assertTrue(companyToUpdate.number2[0] == myCompany.number2[0]);
        Assert.assertTrue(companyToUpdate.number2[1] == myCompany.number2[1]);
    }
    
    @Override
    @Test
    public void testLoadArray() throws DDBSToolkitException {
    	//TODO To fix
    }
    
    /*
    @Test
    public void testLoadArray() throws DDBSToolkitException {
    	
        //Add a company
        Company companyToAdd = new Company();
        companyToAdd.company_uri = "http://cyril-grandjean.co.uk/business/Microsoft";
        companyToAdd.company_ID = 2;
        companyToAdd.company_name = "Microsoft";
        companyToAdd.floatField = 20;
        companyToAdd.longField = 30;
        companyToAdd.surface = 30;

        //Set 2 boss
        String[] boss = new String[2];
        boss[0] = "Bill Gates";
        boss[1] = "Steve Ballmer";
        companyToAdd.boss = boss;

        //Set 2 employees
        Employee employee1 = new Employee();
        employee1.employee_uri = "http://cyril-grandjean.co.uk/business/Cyril_Grandjean";
        employee1.name = "Cyril Grandjean";
        manager.add(employee1);

        Employee employee2 = new Employee();
        employee2.employee_uri = "http://cyril-grandjean.co.uk/business/Steve_Jobs";
        employee2.name = "Steve Jobs";
        manager.add(employee2);

        List<Employee> listEmployees = manager.listAllWithQueryString(new Employee(), null, null);
        Assert.assertEquals(listEmployees.size(), 2);

        Employee[] listEmployee = new Employee[2];
        companyToAdd.employee = listEmployee;
        companyToAdd.employee[0] = employee1;
        companyToAdd.employee[1] = employee2;

        //Add the company
        manager.add(companyToAdd);

        Company companyToLoad = (Company)manager.loadArray(companyToAdd, "employee", null);

        Assert.assertEquals(companyToLoad.employee.length, 2);

        for (int counterEmployee = 0; counterEmployee < companyToLoad.employee.length; counterEmployee++)
        {
            Assert.assertTrue(companyToLoad.employee[counterEmployee].employee_uri.equals(employee1.employee_uri) || companyToLoad.employee[counterEmployee].employee_uri.equals(employee2.employee_uri));
            Assert.assertTrue(companyToLoad.employee[counterEmployee].name.equals(employee1.name) || companyToLoad.employee[counterEmployee].name.equals(employee2.name));

            manager.delete(companyToLoad.employee[counterEmployee]);
        }

        manager.delete(companyToAdd);
        manager.close();
        
        manager = new DistributedSPARQLManager();
        manager.open();
        
        Film filmToRead = new Film();
        filmToRead.film_uri = "http://data.linkedmdb.org/resource/film/1025";

        //Test with remote endpoint
        Film filmExtracted = manager.read(filmToRead);

        Assert.assertEquals(filmExtracted.filmid,1025);
        Assert.assertEquals(filmExtracted.film_uri, "http://data.linkedmdb.org/resource/film/1025");
        Assert.assertEquals(filmExtracted.title, "The Return of the King");
        Assert.assertEquals(filmExtracted.runtime, 98);

        manager.loadArray(filmExtracted, "actor", null);

        for (int counterActor = 0; counterActor < filmExtracted.actor.length; counterActor++)
        {
            System.out.println(filmExtracted.actor[counterActor].actor_uri+" "+filmExtracted.actor[counterActor].actor_name);
        }
    }*/

	@Override
	protected void addReceiverPeerUID(IEntity iEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected FilmBase createFilm() {
		FilmDatastore film = new FilmDatastore();
		return film;
	}

	@Override
	protected ActorBase createActor() {
		ActorDatastore actor = new ActorDatastore();
		return actor;
	}

	@Override
	protected FilmBase createFilm(Integer filmID, String filmName,
			Integer duration, Timestamp creationDate, Long longField,
			Float floatField) {
		return new FilmDatastore(filmID, filmName,
				duration, creationDate, longField,
				floatField);
	}

	@Override
	protected ActorBase createActor(Integer actorId, String actorName,
			Integer filmId) {
		return new ActorDatastore(actorId, actorName,
				filmId);
	}

	@Override
	protected String getLikeExpression() {
		return "2";
	}
	
	/**
	 * Test executeTransaction() method with add transaction
	 * Test commit and rollback conditions
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Override
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
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
		
		manager.rollback(transactionAddRollback);
		
		Assert.assertEquals(
				manager.listAllWithQueryString(createFilm(), null, null).size(),
				numberOfElement);
	}
	
	/**
	 * Test executeTransaction() method with delete transaction
	 * Test commit and rollback conditions
	 * @throws DDBSToolkitException DDBS Toolkit exception
	 */
	@Override
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
	
	/**
	* JUnit tests for the listAll function for MySQL
	* @throws Exception Exception thrown
	*/
	/*
	@Test
	public void testListAllRemoteEndPoint() throws Exception {
		
		//Test with existing remote SPARQL endpoint
		manager = new DistributedSPARQLManager();
		manager.open();
		
		String conditionQueryString = ((DistributedSPARQLManager)manager).getObjectVariable(new Film())+" dc:title 'The Return of the King'";

		List<Film> listEntity = manager.listAllWithQueryString(new Film(), conditionQueryString, null);
		
		//There is only one element
		Assert.assertEquals(listEntity.size(), 1);
		
		Film myFilm = listEntity.get(0);
	
		Assert.assertEquals(myFilm.filmid, 1025);
		Assert.assertEquals(myFilm.film_uri, "http://data.linkedmdb.org/resource/film/1025");
		Assert.assertEquals(myFilm.title, myFilm.title);
		Assert.assertEquals(myFilm.runtime, 98);
		
        conditionQueryString = ((DistributedSPARQLManager)manager).getObjectVariable(new Book())+" fb:type.object.name 'The Fellowship of the Ring'@en.";
        conditionQueryString += "FILTER ( lang(?title) =  'en' ).";
        conditionQueryString += "FILTER ( lang(?summary) = 'en' )";

        List<Book> listBook = manager.listAllWithQueryString(new Book(), conditionQueryString, null);

        Book myBook = listBook.get(0);
        myBook = manager.loadArray(myBook, "author", OrderBy.get("name", OrderByType.ASC));
        myBook = manager.loadArray(myBook, "genre", null);
        myBook = manager.loadArray(myBook, "character", null);
        
        Assert.assertNotNull(myBook.author);
        Assert.assertNotNull(myBook.genre);
        Assert.assertNotNull(myBook.character);
    }*/
	
    /**
     * JUnit test to test the Read function
     * @throws Exception Exception thrown
     */
	/*
    @Test
    public void testReadRemoteEndpoint() throws Exception {

        //Test with a remote SPARQL Endpoint
        DistributedSPARQLManager manager = new DistributedSPARQLManager();
        manager.open();

        Film filmToRead = new Film();
        filmToRead.film_uri = "http://data.linkedmdb.org/resource/film/1025";

        Film filmExtracted = manager.read(filmToRead);

        Assert.assertEquals(filmExtracted.filmid, 1025);
        Assert.assertEquals(filmExtracted.film_uri, "http://data.linkedmdb.org/resource/film/1025");
        Assert.assertEquals(filmExtracted.title, "The Return of the King");
        Assert.assertEquals(filmExtracted.runtime, 98);
    }*/

}
