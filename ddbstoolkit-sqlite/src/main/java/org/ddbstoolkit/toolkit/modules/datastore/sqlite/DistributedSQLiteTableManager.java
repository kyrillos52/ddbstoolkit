package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import org.ddbstoolkit.toolkit.core.jdbc.JDBCEntityManager;

/**
 * Class representing a distributed SQLite Database
 * 
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as
 *          "1characters" table
 */
public class DistributedSQLiteTableManager extends JDBCEntityManager {

	public DistributedSQLiteTableManager(SQLiteConnector myConnector) {
		super(myConnector);
	}
}
