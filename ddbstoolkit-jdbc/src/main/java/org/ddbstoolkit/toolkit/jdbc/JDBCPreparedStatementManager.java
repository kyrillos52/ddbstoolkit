package org.ddbstoolkit.toolkit.jdbc;

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
	 * JDBC Connector
	 */
	private final JDBCConnector myConnector;
	
	/**
	 * Map of Prepared Statements
	 */
	private final Map<String, JDBCPreparedStatements> mapStatements;
	
	/**
	 * JDBC Prepared Statement Manager Constructor
	 * @param myConnector JDBC Connector
	 */
	public JDBCPreparedStatementManager(JDBCConnector myConnector) {
		super();
		this.myConnector = myConnector;
		this.mapStatements = new HashMap<String, JDBCPreparedStatements>();
	}

	/**
	 * Get JDBCPreparedStatements of the object
	 * @param ddbsEntity Entity
	 * @param type Prepared statement type
	 * @return JDBCPreparedStatements object
	 */
	public PreparedStatement getJDBCPreparedStatements(DDBSEntity<DDBSEntityProperty> ddbsEntity, PreparedStatementType type) {
		if(mapStatements.get(ddbsEntity.getDatastoreEntityName()) == null) {
			mapStatements.put(ddbsEntity.getDatastoreEntityName(), new JDBCPreparedStatements());
		}
		return mapStatements.get(ddbsEntity.getDatastoreEntityName()).getPreparedStatement(type);
	}
	
	/**
	 * Get JDBCPreparedStatements of the object
	 * @param ddbsEntity Entity
	 * @param type Prepared statement type
	 * @param query Query to prepare
	 * @return JDBCPreparedStatements object
	 * @throws SQLException SQLException
	 */
	public PreparedStatement setJDBCPreparedStatements(DDBSEntity<DDBSEntityProperty> ddbsEntity, PreparedStatementType type, String query) throws SQLException {
		if(mapStatements.get(ddbsEntity.getDatastoreEntityName()) == null && !hasUnknownDDBSEntityProperties(ddbsEntity)) {
			mapStatements.put(ddbsEntity.getDatastoreEntityName(), new JDBCPreparedStatements());
		}
		PreparedStatement preparedStatement = myConnector.prepareStatement(query);
		mapStatements.get(ddbsEntity.getDatastoreEntityName()).setPreparedStatement(type, preparedStatement);
		return preparedStatement;
	}
	
	/**
	 * Some properties are null and therefore impossible to detect the type
	 * @param ddbsEntity DDBS Entity
	 * @return
	 */
	private boolean hasUnknownDDBSEntityProperties(DDBSEntity<DDBSEntityProperty> ddbsEntity) {
		
		for(DDBSEntityProperty ddbsEntityProperty : ddbsEntity.getEntityProperties()) {
			if(ddbsEntityProperty.getDdbsToolkitSupportedEntity() == null) {
				return true;
			}
		}
		return false;
	}
}
