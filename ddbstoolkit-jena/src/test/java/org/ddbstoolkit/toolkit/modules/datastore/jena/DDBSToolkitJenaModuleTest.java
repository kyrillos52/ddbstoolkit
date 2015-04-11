package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Actor;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Book;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Company;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Employee;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Film;
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
public class DDBSToolkitJenaModuleTest {
	
	/**
	 * Distributed entity manager
	 */
	protected DistributableEntityManager manager;
	
	/**
	 * Jena Directory path
	 */
	private final static String DATASTORE_DIRECTORY = "/Users/Cyril/Desktop/datastore";
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Before 
	public void initialiseDatabase() throws ClassNotFoundException, DDBSToolkitException {
		
		manager = new DistributedSPARQLManager(new Peer(), DATASTORE_DIRECTORY);
		
		manager.open();
		
		cleanData();
	}
	
	@After
	public void closeConnection() throws DDBSToolkitException
	{
		manager.close();
	}
    
	public void instantiateManager() {
		manager = new DistributedSPARQLManager(new Peer(), DATASTORE_DIRECTORY);
	}
	
	/**
	 * Clean data inside the data source
	 * @throws ClassNotFoundException
	 * @throws DDBSToolkitException
	 */
	public void cleanData() throws ClassNotFoundException, DDBSToolkitException {
		
		for(Actor actor : manager.listAll(new Actor(), null, null))
		{
			manager.delete(actor);
		}
		
		for(Company company : manager.listAll(new Company(), null, null))
		{
			manager.delete(company);
		}
		
		for(Employee employee : manager.listAll(new Employee(), null, null))
		{
			manager.delete(employee);
		}
	}
	
	/**
     * Test Connection
     * @throws Exception
     */
    @Test
    public void testIsOpen() throws Exception {
    	
    	instantiateManager();
    	
        manager.open();

        Assert.assertEquals(manager.isOpen(),true);

        manager.close();

        Assert.assertEquals(manager.isOpen(),false);
        
        manager.open();
    }
    
    /**
     * Test list all method with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testListAllWithNullValue() throws DDBSToolkitException
    {
    	thrown.expect(DDBSToolkitException.class);
    	
    	manager.listAll(null, null, null);
    }

    /**
     * JUnit tests for the listAll function for MySQL
     * @throws Exception
     */
    @Test
    public void testListAllRemoteEndPoint() throws Exception {

        //Test with existing remote SPARQL endpoint
        manager = new DistributedSPARQLManager();

        List<String> listCondition = new ArrayList<String>();
        listCondition.add(DistributedSPARQLManager.getObjectVariable(new Film())+" dc:title 'The Return of the King'");

        List<Film> listEntity = manager.listAll(new Film(), listCondition, null);

        //There is only one element
        Assert.assertEquals(listEntity.size(), 1);

        Film myFilm = listEntity.get(0);

        Assert.assertEquals(myFilm.filmid, 1025);
        Assert.assertEquals(myFilm.film_uri, "http://data.linkedmdb.org/resource/film/1025");
        Assert.assertEquals(myFilm.title, myFilm.title);
        Assert.assertEquals(myFilm.runtime, 98);

        listCondition = new ArrayList<String>();
        listCondition.add(DistributedSPARQLManager.getObjectVariable(new Book())+" fb:type.object.name 'The Fellowship of the Ring'@en");
        listCondition.add("FILTER ( lang(?title) =  'en' )");
        listCondition.add("FILTER ( lang(?summary) = 'en' )");

        List<Book> listBook = manager.listAll(new Book(), listCondition, null);

        Book myBook = listBook.get(0);
        myBook = manager.loadArray(myBook, "author", "name ASC");
        myBook = manager.loadArray(myBook, "genre", null);
        myBook = manager.loadArray(myBook, "character", null);
        
        Assert.assertNotNull(myBook.author);
        Assert.assertNotNull(myBook.genre);
        Assert.assertNotNull(myBook.character);
    }
    
    /**
     * Test read with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testReadNullValue() throws DDBSToolkitException
    {
    	thrown.expect(DDBSToolkitException.class);
    	
    	//No object : must return null
        manager.read(null);
    }

    /**
     * JUnit test to test the Read function
     * @throws Exception
     */
    @Test
    public void testReadRemoteEndpoint() throws Exception {

        //Test with a remote SPARQL Endpoint
        DistributedSPARQLManager manager = new DistributedSPARQLManager();

        Film filmToRead = new Film();
        filmToRead.film_uri = "http://data.linkedmdb.org/resource/film/1025";

        Film filmExtracted = manager.read(filmToRead);

        Assert.assertEquals(filmExtracted.filmid, 1025);
        Assert.assertEquals(filmExtracted.film_uri, "http://data.linkedmdb.org/resource/film/1025");
        Assert.assertEquals(filmExtracted.title, "The Return of the King");
        Assert.assertEquals(filmExtracted.runtime, 98);
    }
    
    /**
     * Test to add a null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testAddNullValue() throws DDBSToolkitException
    {
    	thrown.expect(DDBSToolkitException.class);
 
        manager.add(null);
    }

    /**
     * JUnit tests for adding
     * @throws Exception
     */
    @Test
    public void testAdd() throws Exception {

        Assert.assertEquals(manager.listAll(new Company(), null, null).size(), 0);

        //Bad parameters
        Company companyToTest = new Company();

        companyToTest.company_uri = "http://cyril-grandjean.co.uk/business/CompanyTest";
        manager.add(companyToTest);
        
        Assert.assertEquals(manager.listAll(new Company(), null, null).size(), 1);

        manager.delete(companyToTest);
        
        Assert.assertEquals(manager.listAll(new Company(), null, null).size(), 0);

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

        Assert.assertEquals(manager.listAll(new Company(), null, null).size(), 1);

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
     * Test update method with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testUpdateNullValue() throws DDBSToolkitException
    {
    	thrown.expect(DDBSToolkitException.class);
    	
    	manager.update(null);
    }

    /**
     * JUnit tests to test the update function
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {

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
    
    /**
     * Test delete method with null value
     * @throws DDBSToolkitException
     */
    @Test
    public void testDeleteNullValue() throws DDBSToolkitException
    {
    	thrown.expect(DDBSToolkitException.class);
    	
    	//No object : must return null
    	manager.delete(null);
    }

    /**
     * JUnit tests to test the LoadArray function
     * @throws Exception
     */
    @Test
    public void testLoadArray() throws Exception {

    	//TODO Fix load array
    	
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

        List<Employee> listEmployees = manager.listAll(new Employee(), null, null);
        assert listEmployees.size() == 2;

        Employee[] listEmployee = new Employee[2];
        companyToAdd.employee = listEmployee;
        companyToAdd.employee[0] = employee1;
        companyToAdd.employee[1] = employee2;

        //Add the company
        manager.add(companyToAdd);

        Company companyToLoad = (Company)manager.loadArray(companyToAdd, "employee", null);

        assert companyToLoad.employee.length == 2;

        for (int i = 0; i < companyToLoad.employee.length; i++)
        {
            assert companyToLoad.employee[i].employee_uri.equals(employee1.employee_uri) || companyToLoad.employee[i].employee_uri.equals(employee2.employee_uri);
            assert companyToLoad.employee[i].name.equals(employee1.name) || companyToLoad.employee[i].name.equals(employee2.name);

            manager.delete(companyToLoad.employee[i]);
        }

        manager.delete(companyToAdd);

        Film filmToRead = new Film();
        filmToRead.film_uri = "http://data.linkedmdb.org/resource/film/1025";

        //Test with remote endpoint
        Film filmExtracted = manager.read(filmToRead);

        assert  filmExtracted.filmid == 1025;
        assert  filmExtracted.film_uri.equals("http://data.linkedmdb.org/resource/film/1025");
        assert  filmExtracted.title.equals("The Return of the King");
        assert  filmExtracted.runtime == 98;

        manager.loadArray(filmExtracted, "actor", null);

        for (int i = 0; i < filmExtracted.actor.length; i++)
        {
            System.out.println(filmExtracted.actor[i].actor_uri+" "+filmExtracted.actor[i].actor_name);
        }
    }

}
