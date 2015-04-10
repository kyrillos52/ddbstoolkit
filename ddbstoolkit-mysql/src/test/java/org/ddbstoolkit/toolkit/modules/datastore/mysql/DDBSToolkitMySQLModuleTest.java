package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.jdbc.JDBCModuleTest;
import org.junit.After;
import org.junit.Before;

/**
 * JUnit tests to test DistributedMySQLTableManager class
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitMySQLModuleTest extends JDBCModuleTest {

	/**
	 * JDBC String
	 */
	private final static String JDBC_STRING = "jdbc:mysql://localhost:8889/ddbstoolkit";
	
	/**
	 * JDBC User
	 */
	private final static String JDBC_USER = "root";
	
	/**
	 * JDBC Password
	 */
	private final static String JDBC_PASSWORD = "root";
	
	@Before 
	public void initialiseDatabase() throws ClassNotFoundException, DDBSToolkitException {
		
		manager = new DistributedMySQLTableManager(new MySQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD)) ;
		
		manager.open();
		
		cleanData();
	}
	
	@After
	public void closeConnection() throws DDBSToolkitException
	{
		manager.close();
	}
    
	@Override
	public void instantiateManager() throws ClassNotFoundException {
		manager = new DistributedMySQLTableManager(new MySQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD)) ;
	}
}
