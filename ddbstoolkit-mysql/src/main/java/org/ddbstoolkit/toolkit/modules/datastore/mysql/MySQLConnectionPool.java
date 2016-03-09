package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.ddbstoolkit.toolkit.jdbc.JDBCConnectionPool;
import org.ddbstoolkit.toolkit.jdbc.JDBCConnector;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * MySQL Connection pool
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class MySQLConnectionPool extends JDBCConnectionPool {

	public MySQLConnectionPool(final String jdbcString, final int numberOfConnection, final String login, final String password)
			throws SQLException {
		super(jdbcString, numberOfConnection);
		BoneCPConfig config = new BoneCPConfig();
	 	config.setJdbcUrl(jdbcString);
	 	config.setUsername(login);
	 	config.setPassword(password);
		this.connectionPool = new BoneCP(config);
		this.connectionSession = new HashMap<String, Connection>();
	}

	@Override
	public JDBCConnector getJDBCConnector(Connection connection) throws SQLException {
		return new MySQLConnector(connection);	
	}
}
