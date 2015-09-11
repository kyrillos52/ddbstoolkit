package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import org.ddbstoolkit.toolkit.jdbc.JDBCModuleTest;

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
    
	@Override
	public void instantiateManager() throws ClassNotFoundException {
		manager = new DistributedSQLiteTableManager(new SQLiteConnector(SQLITE_DIRECTORY, SQLITE_DATABASE));
	}
}
