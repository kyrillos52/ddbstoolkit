package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import java.sql.Connection;
import java.sql.SQLException;

import org.ddbstoolkit.toolkit.jdbc.JDBCConnectionPool;
import org.ddbstoolkit.toolkit.jdbc.JDBCConnector;

/**
 * SQLLite connection pool
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SQLiteConnectionPool extends JDBCConnectionPool {

	public SQLiteConnectionPool(String jdbcString, int numberOfConnection)
			throws SQLException {
		super(jdbcString, numberOfConnection);
	}

	@Override
	public JDBCConnector getJDBCConnector(Connection connection)
			throws SQLException {
		return new SQLiteConnector(connection);	
	}

}
