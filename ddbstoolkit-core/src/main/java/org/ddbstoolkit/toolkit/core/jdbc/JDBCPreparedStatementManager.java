package org.ddbstoolkit.toolkit.core.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;

/**
 * JDBC Prepared Statement Manager
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class JDBCPreparedStatementManager {
	
	/**
	 * Connector SQLite
	 */
	private JDBCConnector myConnector;
	
	/**
	 * Map of Prepared Statements
	 */
	private Map<String, JDBCPreparedStatements> mapStatements;
	
	/**
	 * Get JDBCPreparedStatements of the object
	 * @param iEntity Entity
	 * @return JDBCPreparedStatements object
	 */
	private JDBCPreparedStatements getJDBCPreparedStatements(DDBSEntity<DDBSEntityProperty> ddbsEntity)
	{
		
		if(mapStatements == null)
		{
			mapStatements = new HashMap<String, JDBCPreparedStatements>();
		}
		
		if(mapStatements.get(ddbsEntity.getEntityName()) == null)
		{
			mapStatements.put(ddbsEntity.getEntityName(), new JDBCPreparedStatements());
		}
		return mapStatements.get(ddbsEntity.getEntityName());
	}
	
	/**
	 * JDBC Prepared Statement Manager Constructor
	 * @param myConnector JDBC Connector
	 */
	public JDBCPreparedStatementManager(JDBCConnector myConnector) {
		super();
		this.myConnector = myConnector;
	}
	
	/**
	 * Get the read prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Query
	 * @return
	 */
	public PreparedStatement getReadPreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity)
	{
		return getJDBCPreparedStatements(ddbsEntity).getReadPreparedStatement();
	}
	
	/**
	 * Get the read last element prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Last Element Query
	 * @return
	 */
	public PreparedStatement getReadLastElementPreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity)
	{
		return getJDBCPreparedStatements(ddbsEntity).getReadLastElementPreparedStatement();
	}
	
	/**
	 * Get the add prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Query
	 * @return
	 */
	public PreparedStatement getAddPreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity)
	{
		return getJDBCPreparedStatements(ddbsEntity).getAddPreparedStatement();
	}
	
	/**
	 * Get the update prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Query
	 * @return
	 */
	public PreparedStatement getUpdatePreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity)
	{
		return getJDBCPreparedStatements(ddbsEntity).getUpdatePreparedStatement();
	}
	
	/**
	 * Get the delete prepared statement
	 * @param iEntity Entity
	 * @param readQuery Delete Query
	 * @return
	 */
	public PreparedStatement getDeletePreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity)
	{
		return getJDBCPreparedStatements(ddbsEntity).getDeletePreparedStatement();
	}
	
	/**
	 * Set the read prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Query
	 * @return
	 * @throws SQLException 
	 */
	public PreparedStatement setReadPreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity, String readQuery) throws SQLException
	{
		JDBCPreparedStatements preparedStatements = getJDBCPreparedStatements(ddbsEntity);
		
		if(preparedStatements.getReadPreparedStatement() == null)
		{
			preparedStatements.setReadPreparedStatement(myConnector.prepareStatement(readQuery));
		}
		return preparedStatements.getReadPreparedStatement();
	}
	
	/**
	 * Set the read last element prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Last Element Query
	 * @return
	 * @throws SQLException 
	 */
	public PreparedStatement setReadLastElementPreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity, String readLastElementQuery) throws SQLException
	{
		JDBCPreparedStatements preparedStatements = getJDBCPreparedStatements(ddbsEntity);
		
		if(preparedStatements.getReadLastElementPreparedStatement() == null)
		{
			preparedStatements.setReadLastElementPreparedStatement(myConnector.prepareStatement(readLastElementQuery));
		}
		return preparedStatements.getReadLastElementPreparedStatement();
	}
	
	/**
	 * Set the add prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Query
	 * @return
	 * @throws SQLException 
	 */
	public PreparedStatement setAddPreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity, String addQuery) throws SQLException
	{
		JDBCPreparedStatements preparedStatements = getJDBCPreparedStatements(ddbsEntity);
		
		if(preparedStatements.getAddPreparedStatement() == null)
		{
			preparedStatements.setAddPreparedStatement(myConnector.prepareStatement(addQuery));
		}
		return preparedStatements.getAddPreparedStatement();
	}
	
	/**
	 * Set the update prepared statement
	 * @param iEntity Entity
	 * @param readQuery Read Query
	 * @return
	 * @throws SQLException 
	 */
	public PreparedStatement setUpdatePreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity, String updateQuery) throws SQLException
	{
		JDBCPreparedStatements preparedStatements = getJDBCPreparedStatements(ddbsEntity);
		
		if(preparedStatements.getUpdatePreparedStatement() == null)
		{
			preparedStatements.setUpdatePreparedStatement(myConnector.prepareStatement(updateQuery));
		}
		return preparedStatements.getUpdatePreparedStatement();
	}
	
	/**
	 * Set the delete prepared statement
	 * @param iEntity Entity
	 * @param readQuery Delete Query
	 * @return
	 * @throws SQLException 
	 */
	public PreparedStatement setDeletePreparedStatement(DDBSEntity<DDBSEntityProperty> ddbsEntity, String deleteQuery) throws SQLException
	{
		JDBCPreparedStatements preparedStatements = getJDBCPreparedStatements(ddbsEntity);
		
		if(preparedStatements.getDeletePreparedStatement() == null)
		{
			preparedStatements.setDeletePreparedStatement(myConnector.prepareStatement(deleteQuery));
		}
		return preparedStatements.getDeletePreparedStatement();
	}
}
