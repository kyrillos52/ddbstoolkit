package org.ddbstoolkit.toolkit.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.ddbstoolkit.toolkit.core.DDBSTransaction;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * JDBC Connection Pool
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCConnectionPool {

	/**
	 * Connection pool
	 */
	protected BoneCP connectionPool;
	
	/**
	 * Number of connection
	 */
	protected int numberOfConnection;
	
	/**
	 * Connection session
	 */
	protected Map<String, Connection> connectionSession;
	
	/**
	 * JDBC String
	 */
	protected String jdbcString;

	public JDBCConnectionPool(final String jdbcString, final int numberOfConnection) throws SQLException {
		super();
		this.numberOfConnection = numberOfConnection;
		this.jdbcString = jdbcString;
		BoneCPConfig config = new BoneCPConfig();
	 	config.setJdbcUrl(jdbcString);
		this.connectionPool = new BoneCP(config);
		this.connectionSession = new HashMap<String, Connection>();
	}
	
	/**
	 * Create a new connection session
	 * @return session id
	 * @throws SQLException SQL Exception
	 */
	public DDBSTransaction createSession() throws SQLException {
		
		Random random = new Random();
		long mapKey;
		do {
			mapKey = random.nextLong();
		} while (connectionSession.get(String.valueOf(mapKey)) != null);
		
		Connection connection = connectionPool.getConnection();
		connectionSession.put(String.valueOf(mapKey), connection);
		return new DDBSTransaction(String.valueOf(mapKey));
	}
	
	/**
	 * Get JDBC connection
	 * @return Connection
	 * @throws SQLException SQL Exception
	 */
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	
	/**
	 * Get JDBC connection
	 * @param transaction Transaction
	 * @return Connection
	 */
	public Connection getConnection(DDBSTransaction transaction) {
		return connectionSession.get(transaction.getTransactionId());
	}
	
	/**
	 * End JDBC session
	 * @param transaction Transaction
	 * @throws SQLException SQL Exception
	 */
	public void endSession(DDBSTransaction transaction) throws SQLException {
		connectionSession.get(transaction.getTransactionId()).close();
		connectionSession.remove(transaction.getTransactionId());
	}
	
	/**
	 * Shutdown connection pool
	 */
	public void shutdownConnectionPool() {
		connectionPool.shutdown();
	}
	
	/**
	 * Get JDBC Connector
	 * @param connection JDBC connection
	 * @return JDBC Connector
	 * @throws SQLException SQL Exception
	 */
	public abstract JDBCConnector getJDBCConnector(Connection connection) throws SQLException;

	@Override
	public String toString() {
		return "JDBCConnectionPool [numberOfConnection=" + numberOfConnection
				+ "]";
	}
}
