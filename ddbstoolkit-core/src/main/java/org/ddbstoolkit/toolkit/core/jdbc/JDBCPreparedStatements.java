package org.ddbstoolkit.toolkit.core.jdbc;

import java.sql.PreparedStatement;

/**
 * JDBC Prepared Statements
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class JDBCPreparedStatements {
	
	private PreparedStatement readPreparedStatement;
	
	private PreparedStatement readLastElementPreparedStatement;
	
	private PreparedStatement addPreparedStatement;
	
	private PreparedStatement updatePreparedStatement;
	
	private PreparedStatement deletePreparedStatement;

	public PreparedStatement getReadPreparedStatement() {
		return readPreparedStatement;
	}

	public void setReadPreparedStatement(PreparedStatement readPreparedStatement) {
		this.readPreparedStatement = readPreparedStatement;
	}

	public PreparedStatement getReadLastElementPreparedStatement() {
		return readLastElementPreparedStatement;
	}

	public void setReadLastElementPreparedStatement(
			PreparedStatement readLastElementPreparedStatement) {
		this.readLastElementPreparedStatement = readLastElementPreparedStatement;
	}

	public PreparedStatement getAddPreparedStatement() {
		return addPreparedStatement;
	}

	public void setAddPreparedStatement(PreparedStatement addPreparedStatement) {
		this.addPreparedStatement = addPreparedStatement;
	}

	public PreparedStatement getUpdatePreparedStatement() {
		return updatePreparedStatement;
	}

	public void setUpdatePreparedStatement(PreparedStatement updatePreparedStatement) {
		this.updatePreparedStatement = updatePreparedStatement;
	}

	public PreparedStatement getDeletePreparedStatement() {
		return deletePreparedStatement;
	}

	public void setDeletePreparedStatement(PreparedStatement deletePreparedStatement) {
		this.deletePreparedStatement = deletePreparedStatement;
	}
}
