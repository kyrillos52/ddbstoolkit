package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import org.ddbstoolkit.toolkit.core.IEntity;
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
	private static final String JDBC_STRING = "jdbc:mysql://localhost:3306/ddbstoolkit";
	
	/**
	 * JDBC User
	 */
	private static final String JDBC_USER = "root";
	
	/**
	 * JDBC Password
	 */
	private static final String JDBC_PASSWORD = "";
	
	@Before 
	public void initialiseDatabase() throws ClassNotFoundException, DDBSToolkitException {
		
		manager = new DistributedMySQLTableManager(new MySQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD));
		
		manager.open();
	}
	
	@After
	public void closeConnection() throws DDBSToolkitException
	{
		if(manager.isOpen()) {
			manager.close();
		}
	}
    
	@Override
	public void instantiateManager() throws ClassNotFoundException {
		manager = new DistributedMySQLTableManager(new MySQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD)) ;
	}

	@Override
	protected void addReceiverPeerUID(IEntity iEntity) {
		//Nothing to add
	}
}
