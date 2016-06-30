package org.ddbstoolkit.toolkit.jdbc;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC Prepared Statements
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class JDBCPreparedStatements {
	
	/**
	 * Prepared statements
	 */
	private final Map<PreparedStatementType, PreparedStatement> preparedStatements = new HashMap<PreparedStatementType, PreparedStatement>(PreparedStatementType.values().length);
	
	/**
	 * Get Prepared statement
	 * Null if not created
	 * @param type prepared statement type
	 * @return the prepared statement or null
	 */
	public PreparedStatement getPreparedStatement(PreparedStatementType type) {
		return preparedStatements.get(type);
	}
	
	/**
	 * Set prepared statement
	 * @param type prepared statement type
	 * @param preparedStatement Prepared statement
	 */
	public void setPreparedStatement(PreparedStatementType type, PreparedStatement preparedStatement) {
		preparedStatements.put(type, preparedStatement);
	}
}
