package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.jdbc.JDBCModuleTest;

/**
 * JUnit tests to test DistributedMySQLTableManager class
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitSQLiteModuleTest extends JDBCModuleTest {

	/**
	 * SQLLite Database file
	 */
	private static final String SQLITE_DATABASE = ":memory:";
    
	@Override
	public void instantiateManager() throws ClassNotFoundException, DDBSToolkitException {
		manager = new DistributedSQLiteTableManager(new SQLiteConnector(SQLITE_DATABASE));
		manager.open();
		manager.createEntity(createActor());
		manager.createEntity(createFilm());
	}
}
