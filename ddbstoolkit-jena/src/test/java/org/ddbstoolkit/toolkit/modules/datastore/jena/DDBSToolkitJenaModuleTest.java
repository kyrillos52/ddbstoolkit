package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddbstoolkit.toolkit.core.DataModuleTest;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.orderby.OrderByType;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.ActorDatastore;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Book;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Company;
import org.ddbstoolkit.toolkit.modules.datastore.jena.model.Employee;
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
	public void closeConnection() throws DDBSToolkitException
	{
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
	 * Create sample data
	 * 
	 * @throws DDBSToolkitException
	 */
	@Override
	protected Map<String, FilmBase> createSampleData()
			throws DDBSToolkitException {

		Map<String, FilmBase> mapFilms = new HashMap<String, FilmBase>();

		FilmDatastore film1 = new FilmDatastore();
		addReceiverPeerUID(film1);
		film1.setFilm_uri("http://www.cyril-grandjean.co.uk/film/1");
		film1.setFilmName("Film 1");
		film1.setDuration(10);
		film1.setFloatField(new Float(20));
		film1.setLongField(new Long(30));
		film1.setCreationDate(new Timestamp(10000));

		mapFilms.put("film1", film1);
		manager.add(film1);

		FilmDatastore film2 = new FilmDatastore();
		addReceiverPeerUID(film2);
		film2.setFilm_uri("http://www.cyril-grandjean.co.uk/film/2");
		film2.setFilmName("Film 2");
		film2.setDuration(20);
		film2.setFloatField(new Float(30));
		film2.setLongField(new Long(40));
		film2.setCreationDate(new Timestamp(20000));

		mapFilms.put("film2", film2);
		manager.add(film2);

		FilmDatastore film3 = new FilmDatastore();
		addReceiverPeerUID(film3);
		film3.setFilm_uri("http://www.cyril-grandjean.co.uk/film/3");
		film3.setFilmName("Film 3");
		film3.setDuration(30);
		film3.setFloatField(new Float(40));
		film3.setLongField(new Long(50));
		film3.setCreationDate(new Timestamp(60000));

		mapFilms.put("film3", film3);
		manager.add(film3);

		FilmDatastore filmNull = new FilmDatastore();
		addReceiverPeerUID(filmNull);
		filmNull.setFilm_uri("http://www.cyril-grandjean.co.uk/film/4");

		mapFilms.put("filmNull", filmNull);
		manager.add(filmNull);

		return mapFilms;
	}

    /**
     * JUnit tests for adding
     * @throws Exception
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
     * @throws Exception
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

    /**
     * JUnit tests to test the LoadArray function
     * @throws Exception
     */
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
        manager.close();
        
        manager = new DistributedSPARQLManager();
        manager.open();
        
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

	@Override
	protected void addReceiverPeerUID(IEntity iEntity) {
		// TODO Auto-generated method stub
		
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

}
