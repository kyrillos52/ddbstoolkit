package org.ddbstoolkit.toolkit.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC Connection object
 * 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCConnector {

	/**
	 * Connection object
	 */
	protected Connection connector;

	/**
	 * JDBC Connection String
	 */
	protected String jdbcString;
	
	/**
	 * Connection ID
	 */
	protected long connectionId;

	/**
	 * JDBC Connection constructor
	 * 
	 * @param jdbcString JDBC String
	 */
	protected JDBCConnector(String jdbcString) {
		super();
		this.jdbcString = jdbcString;
	}
	
	/**
	 * JDBC Constructor
	 * @param connector Connector
	 */
	public JDBCConnector(Connection connector) {
		super();
		this.connector = connector;
	}

	/**
	 * Function which check if the connection is opened
	 * 
	 * @return boolean
	 * @throws SQLException SQL Exception
	 */
	public boolean isOpen() throws SQLException {
		return connector != null && !connector.isClosed();
	}

	/**
	 * Open connection to the database
	 * 
	 * @throws SQLException SQL Exception
	 */
	public void open() throws SQLException {
		connector = DriverManager.getConnection(jdbcString);
	}

	/**
	 * Close connection to the database
	 * 
	 * @throws SQLException SQL Exception
	 */
	public void close() throws SQLException {
		if(connector != null) {
			connector.close();
		}
	}

	/**
	 * Function to launch SQL request
	 * 
	 * @param sql
	 *            Request to execute (without protection)
	 * @return Result of the request
	 * @throws SQLException SQL Exception
	 */
	public int executeQuery(String sql) throws SQLException {

		Statement stmt = connector.createStatement();
		return stmt.executeUpdate(sql);
	}

	/**
	 * Function to execute prepared query
	 * 
	 * @param preparedRequest
	 *            SQL request to execute
	 * @return Result of the request
	 * @throws SQLException SQL Exception
	 */
	public int executePreparedQuery(PreparedStatement preparedRequest)
			throws SQLException {
		return preparedRequest.executeUpdate();
	}

	/**
	 * Function to query SQL request
	 * 
	 * @param sql
	 *            SQL request to execute (without protection)
	 * @return Result of the request
	 * @throws SQLException SQL Exception
	 */
	public ResultSet query(String sql) throws SQLException {
		Statement stmt = connector.createStatement();
		return stmt.executeQuery(sql);
	}

	/**
	 * Query a prepared request
	 * 
	 * @param preparedRequest
	 *            Prepared Request
	 * @return Result of the request
	 * @throws SQLException SQL Exception
	 */
	public ResultSet queryPreparedStatement(PreparedStatement preparedRequest) throws SQLException {
		return preparedRequest.executeQuery();
	}

	/**
	 * Prepare a request
	 * 
	 * @param sql
	 *            SQL request to prepare
	 * @return Prepared request
	 * @throws SQLException SQL Exception
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connector.prepareStatement(sql);
	}
	
	/**
	 * Set auto commit value
	 * @param isAutoCommit Boolean indicating expected auto commit value
	 * @throws SQLException SQL Exception
	 */
	public void setAutoCommit(boolean isAutoCommit) throws SQLException {
		connector.setAutoCommit(isAutoCommit);
	}

	/**
	 * Get auto commit value
	 * @return Boolean indicating the auto-commit state
	 * @throws SQLException SQL Exception
	 */
	public boolean isAutoCommit() throws SQLException {
		return connector.getAutoCommit();
	}

	/**
	 * Commit the transaction
	 * @throws SQLException SQL Exception
	 */
	public void commit() throws SQLException {
		connector.commit();
	}

	/**
	 * Rollback the transaction
	 * @throws SQLException SQL Exception 
	 */
	public void rollback() throws SQLException  {
		connector.rollback();
	}

}
