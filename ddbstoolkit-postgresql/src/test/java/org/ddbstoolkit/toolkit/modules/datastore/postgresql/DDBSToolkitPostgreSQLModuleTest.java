package org.ddbstoolkit.toolkit.modules.datastore.postgresql;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.jdbc.JDBCModuleTest;
import org.junit.After;
import org.junit.Before;

/**
 * JUnit tests to test DDBSToolkitPostgreSQLModuleTest class
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitPostgreSQLModuleTest extends JDBCModuleTest {

	/**
	 * JDBC String
	 */
	private static final String JDBC_STRING = "jdbc:postgresql://localhost/ddbstoolkit";
	
	/**
	 * JDBC User
	 */
	private static final String JDBC_USER = "postgres";
	
	/**
	 * JDBC Password
	 */
	private static final String JDBC_PASSWORD = "";
	
	@Before 
	public void initialiseDatabase() throws ClassNotFoundException, DDBSToolkitException {
		
		manager = new DistributedPostgreSQLTableManager(new PostgreSQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD)) ;
		
		manager.open();
	}
	
	@After
	public void closeConnection() throws DDBSToolkitException {
		if(manager.isOpen()) {
			manager.close();
		}
	}
    
	@Override
	public void instantiateManager() throws ClassNotFoundException {
		manager = new DistributedPostgreSQLTableManager(new PostgreSQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD));
	}

	@Override
	protected void addReceiverPeerUID(IEntity iEntity) {
		//Nothing to add
	}
}
