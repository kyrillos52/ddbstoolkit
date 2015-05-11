package org.ddbstoolkit.toolkit.core.jdbc;

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
 * @1.0 Class creation
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
	 * JDBC Connection constructor
	 * 
	 * @param jdbcString
	 */
	public JDBCConnector(String jdbcString) {
		super();
		this.jdbcString = jdbcString;
	}

	/**
	 * Function which check if the connection is opened
	 * 
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean isOpen() throws SQLException {
		if (connector == null || connector.isClosed()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Open connection to the database
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		connector = DriverManager.getConnection(jdbcString);
	}

	/**
	 * Close connection to the database
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		connector.close();
	}

	/**
	 * Function to launch SQL request
	 * 
	 * @param sql
	 *            Request to execute (without protection)
	 * @return Result of the request
	 * @throws SQLException
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
	 * @throws SQLException
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
	 * @throws SQLException 
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
	 * @throws SQLException 
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
	 * @throws SQLException 
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connector.prepareStatement(sql);
	}

}
