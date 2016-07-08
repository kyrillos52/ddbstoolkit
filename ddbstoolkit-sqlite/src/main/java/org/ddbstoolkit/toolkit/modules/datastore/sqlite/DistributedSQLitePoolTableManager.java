package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import java.sql.SQLException;
import java.util.ListIterator;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;
import org.ddbstoolkit.toolkit.jdbc.JDBCPoolManager;

/**
 * Class representing a distributed SQLite Database
 * 
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as
 *          "1characters" table
 */
public class DistributedSQLitePoolTableManager extends JDBCPoolManager {
	
	public DistributedSQLitePoolTableManager(SQLiteConnectionPool pool) {
		super(pool);
	}

	@Override
	public boolean createEntity(IEntity objectToCreate)
			throws DDBSToolkitException {
		
		DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToCreate);
	
		StringBuilder createTableString = new StringBuilder();
		createTableString.append("CREATE TABLE ").append(ddbsEntity.getDatastoreEntityName()).append(" (");
		
		ListIterator<DDBSEntityProperty> ddbsEntityPropertyIterator = ddbsEntity.getEntityProperties().listIterator();
		
		while(ddbsEntityPropertyIterator.hasNext()) {
			
			DDBSEntityProperty property = ddbsEntityPropertyIterator.next();
			
			if(!DDBSToolkitSupportedEntity.IENTITY_ARRAY.equals(property.getDdbsToolkitSupportedEntity())) {
				
				createTableString.append(property.getPropertyName());
				createTableString.append(" ");
				
				if(property.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.INTEGER) || property.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.LONG) || property.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.TIMESTAMP)) {
					createTableString.append("INTEGER");
				} else if(property.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.FLOAT) || property.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.DOUBLE)) {
					createTableString.append("REAL");
				} else if(property.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.STRING)) {
					createTableString.append("TEXT");
				}
				
				if(property.isIDEntity()) {
					createTableString.append(" PRIMARY KEY ");
					
					if(property.getDdbsEntityIDProperty().isAutoIncrement()) {
						createTableString.append("AUTOINCREMENT");
					}
				}
				
				if(ddbsEntityPropertyIterator.hasNext()) {
					
					property = ddbsEntityPropertyIterator.next();
					if(!DDBSToolkitSupportedEntity.IENTITY_ARRAY.equals(property.getDdbsToolkitSupportedEntity())) {
						createTableString.append(",");
					}
					ddbsEntityPropertyIterator.previous();
					
				}
			}
		}
		
		createTableString.append(");");
		
		try {
			return jdbcConnector.executeQuery(createTableString.toString()) == 1;
		} catch (SQLException sqlException) {
			throw new DDBSToolkitException("Error while creating the table", sqlException);
		}
		
	}
}
