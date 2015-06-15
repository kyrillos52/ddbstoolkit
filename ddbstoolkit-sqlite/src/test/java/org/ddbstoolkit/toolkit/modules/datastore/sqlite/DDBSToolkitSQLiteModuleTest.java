package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.jdbc.JDBCModuleTest;
import org.junit.After;
import org.junit.Before;

/**
 * JUnit tests to test DistributedMySQLTableManager class
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitSQLiteModuleTest extends JDBCModuleTest {

	/**
	 * SQLite Directory path String
	 */
	private final static String SQLITE_DIRECTORY = "/Users/Cyril/Desktop/";
	
	/**
	 * SQLLite Database file
	 */
	private final static String SQLITE_DATABASE = "ddbstoolkit.db";
	
	@Before 
	public void initialiseDatabase() throws ClassNotFoundException, DDBSToolkitException {
		
		manager = new DistributedSQLiteTableManager(new SQLiteConnector(SQLITE_DIRECTORY, SQLITE_DATABASE)) ;
		
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
		manager = new DistributedSQLiteTableManager(new SQLiteConnector(SQLITE_DIRECTORY, SQLITE_DATABASE));
	}

	@Override
	protected void addReceiverPeerUID(DistributedEntity distributedEntity) {
		
		//Nothing to add
	}
}
