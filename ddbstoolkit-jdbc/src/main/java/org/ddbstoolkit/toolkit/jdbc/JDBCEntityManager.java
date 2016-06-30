package org.ddbstoolkit.toolkit.jdbc;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DDBSTransaction;
import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.TransactionCommand;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.generation.ImplementableEntity;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * JDBC Entity manager
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCEntityManager implements DistributableEntityManager {
	
	/**
	 * JDBC connector
	 */
	protected JDBCConnector jdbcConnector;

	/**
	 * JDBC Prepared Statement Manager
	 */
	protected JDBCPreparedStatementManager jdbcPreparedStatementManager;
	
	/**
	 * DDBS Entity manager
	 */
	protected DDBSEntityManager<DDBSEntity<DDBSEntityProperty>> ddbsEntityManager;
	
	/**
	 * JDBC Condition converter
	 */
	protected JDBCConditionConverter jdbcConditionConverter;
	
	/**
	 * JDBC Entity manager with a single connection
	 * @param jdbcConnector JDBC Connector
	 */
	public JDBCEntityManager(JDBCConnector jdbcConnector) {
		super();
		this.jdbcConnector = jdbcConnector;
		this.ddbsEntityManager = new DDBSEntityManager<DDBSEntity<DDBSEntityProperty>>(new ClassInspector());
		this.jdbcConditionConverter = new JDBCConditionConverter(ddbsEntityManager);
	}


	@Override
	public boolean isOpen() throws DDBSToolkitException {
		try {
			return jdbcConnector.isOpen();
			
		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during checking SQL connection", sqle);
		}
	}

	@Override
	public void open() throws DDBSToolkitException {
		try {
			jdbcConnector.open();
			
			jdbcPreparedStatementManager = new JDBCPreparedStatementManager(
						jdbcConnector);

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during opening SQL connection", sqle);
		}
	}

	@Override
	public void close() throws DDBSToolkitException {
		try {
			jdbcConnector.close();
		} catch (SQLException e) {
			throw new DDBSToolkitException("Error during opening SQL connection",e);
		}
		jdbcPreparedStatementManager = null;
	}

	@Override
	public void setAutoCommit(boolean isAutoCommit) throws DDBSToolkitException {
		try {
			jdbcConnector.setAutoCommit(isAutoCommit);
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while committing the transaction", sqle);
		}
	}

	@Override
	public void commit(DDBSTransaction transaction) throws DDBSToolkitException {
		try {
			jdbcConnector.commit();
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while committing the transaction", sqle);
		}
	}

	@Override
	public void rollback(DDBSTransaction transaction) throws DDBSToolkitException {
		try {
			jdbcConnector.rollback();
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while rollbacking the transaction", sqle);
		}
	}
	

	@Override
	public DDBSTransaction executeTransaction(DDBSTransaction transaction)throws DDBSToolkitException {
		
		for(TransactionCommand transactionCommand : transaction.getTransactionCommands()) {
			switch (transactionCommand.getDataAction()) {
			case ADD:
				add(transactionCommand.getEntity());
				break;
			case UPDATE:
				update(transactionCommand.getEntity());
				break;
			case DELETE:
				delete(transactionCommand.getEntity());
				break;
			case CREATE_ENTITY:
				createEntity(transactionCommand.getEntity());
				break;
			default:
				break;
			}
		}
		
		return transaction;
	}

	/**
	 * Test database connection
	 * 
	 * @throws DDBSToolkitException
	 */
	private <T extends IEntity> void testConnection(T object)
			throws DDBSToolkitException {
		try {
			if (!jdbcConnector.isOpen()) {
				throw new DDBSToolkitException(
						"The database connection is not opened");
			}
			if (object == null) {
				throw new IllegalArgumentException(
						"The object passed in parameter is null");
			}

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error has occured while checking the connection", sqle);
		}

	}
	
	/**
	 * Create a SQL Select query
	 * @param object Object
	 * @param conditionQueryString Condition query string
	 * @param orderBy Order By element
	 * @return SQL Select query
	 */
	private <T extends IEntity> String getSelectQueryString(T object,
			String conditionQueryString, OrderBy orderBy) {
		
		DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);
		
		StringBuilder listAllQuery = new StringBuilder();
		
		listAllQuery.append("SELECT ");

		Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity
				.getSupportedPrimaryTypeEntityProperties().iterator();
		while (iteratorProperties.hasNext()) {
			listAllQuery.append(iteratorProperties.next().getPropertyName());

			if (iteratorProperties.hasNext()) {
				listAllQuery.append(",");
			}
		}

		listAllQuery.append(" FROM ");
		listAllQuery.append(ddbsEntity.getDatastoreEntityName());

		if (conditionQueryString != null && !conditionQueryString.isEmpty()) {
			listAllQuery.append(" WHERE ");

			listAllQuery.append(conditionQueryString);
		}

		if (orderBy != null) {
			DDBSEntityProperty ddbsEntityProperty = ddbsEntity.getDDBSEntityProperty(orderBy.getName());
			
			listAllQuery.append(" ORDER BY ");
			listAllQuery.append(ddbsEntityProperty.getPropertyName());
			switch (orderBy.getType()) {
			case ASC:
				listAllQuery.append(" ASC");
				break;
			case DESC:
				listAllQuery.append(" DESC");
				break;

			default:
				break;
			}
		}

		listAllQuery.append(";");
		return listAllQuery.toString();
	}
	
	@Override
	public <T extends IEntity> List<T> listAll(T object, Conditions conditions,
			OrderBy orderBy) throws DDBSToolkitException {
		
		testConnection(object);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);

			String listAllQuery = getSelectQueryString(object, jdbcConditionConverter.getConditionsString(conditions, object), orderBy);
			
			PreparedStatement preparedRequest = jdbcConnector.prepareStatement(listAllQuery);
			
			jdbcConditionConverter.prepareStatement(preparedRequest, conditions, ddbsEntity);
			
			ResultSet results = jdbcConnector.queryPreparedStatement(preparedRequest);

			if (object instanceof ImplementableEntity) {
				return ((ImplementableEntity) object).conversionResultSet(
						results, object);
			} else {
				return conversionResultSet(results, object);
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}

	@Override
	public <T extends IEntity> List<T> listAllWithQueryString(T object,
			String conditionQueryString, OrderBy orderBy)
			throws DDBSToolkitException {

		testConnection(object);

		try {
			
			String listAllQuery = getSelectQueryString(object, conditionQueryString, orderBy);

			ResultSet results = jdbcConnector.query(listAllQuery);

			if (object instanceof ImplementableEntity) {
				return ((ImplementableEntity) object).conversionResultSet(results, object);
			} else {
				return conversionResultSet(results, object);
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}

	@Override
	public <T extends IEntity> T read(T object) throws DDBSToolkitException {

		testConnection(object);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.READ);

			if (preparedRequest == null) {

				StringBuilder sqlReadWhereString = new StringBuilder();

				Iterator<DDBSEntityProperty> iteratorIDProperties = ddbsEntity.getEntityIDProperties().iterator();

				while (iteratorIDProperties.hasNext()) {

					sqlReadWhereString.append(iteratorIDProperties.next().getPropertyName());

					sqlReadWhereString.append(" = ?");

					if (iteratorIDProperties.hasNext()) {
						sqlReadWhereString.append(" AND ");
					}
				}
				
				String sqlReadString = getSelectQueryString(object, sqlReadWhereString.toString(), null);

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity,
								PreparedStatementType.READ, sqlReadString);
			}

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, ddbsEntity.getEntityIDProperties(), object);

			ResultSet results = jdbcConnector
					.queryPreparedStatement(preparedRequest);

			List<T> resultList = conversionResultSet(results, object);
			if (resultList.size() > 0) {
				return resultList.get(0);
			}

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}

		return null;
	}

	@Override
	public <T extends IEntity> T readLastElement(T object)
			throws DDBSToolkitException {

		testConnection(object);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.READ_LAST_ELEMENT);

			if (preparedRequest == null) {

				List<DDBSEntityProperty> ddbsIdProperties = ddbsEntity.getEntityIDProperties();

				if (ddbsIdProperties.isEmpty() || ddbsIdProperties.size() > 1) {
					throw new DDBSToolkitException(
							"There is more than one ID property");
				} else {
					StringBuilder sqlReadWhereString = new StringBuilder();

					sqlReadWhereString.append(ddbsIdProperties.get(0)
							.getPropertyName());
					sqlReadWhereString.append(" = (SELECT MAX(");
					sqlReadWhereString.append(ddbsIdProperties.get(0)
							.getPropertyName());
					sqlReadWhereString.append(") FROM ");
					sqlReadWhereString.append(ddbsEntity.getDatastoreEntityName());
					sqlReadWhereString.append(')');
					
					String sqlReadLastElementString = getSelectQueryString(object, sqlReadWhereString.toString(), null);

					preparedRequest = jdbcPreparedStatementManager
							.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.READ_LAST_ELEMENT,
									sqlReadLastElementString);
				}
			}

			ResultSet results = jdbcConnector
					.queryPreparedStatement(preparedRequest);

			List<T> resultList = conversionResultSet(results, object);
			if (resultList.size() > 0) {
				return resultList.get(0);
			}

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}

		return null;
	}
	
	/**
	 * Create Insert SQL Query
	 * @param ddbsEntity DDBSEntity
	 * @return INSERT SQL Query
	 */
	protected String getInsertSQLString(DDBSEntity<DDBSEntityProperty> ddbsEntity) {
		
		StringBuilder sqlAddString = new StringBuilder();
		StringBuilder sqlAddPart2String = new StringBuilder();

		sqlAddString.append("INSERT INTO ");
		sqlAddString.append(ddbsEntity.getDatastoreEntityName());
		sqlAddString.append(" (");

		Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity.getNotIncrementingEntityProperties().iterator();

		while (iteratorProperties.hasNext()) {

			sqlAddString.append(iteratorProperties.next().getPropertyName());
			sqlAddPart2String.append('?');

			if (iteratorProperties.hasNext()) {
				sqlAddString.append(',');
				sqlAddPart2String.append(',');
			}
		}

		sqlAddString.append(") VALUES (");
		sqlAddString.append(sqlAddPart2String);
		sqlAddString.append(");");
		return sqlAddString.toString();
	}

	@Override
	public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

		testConnection(objectToAdd);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToAdd);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.ADD);

			if (preparedRequest == null) {

				String sqlAddString = getInsertSQLString(ddbsEntity);

				preparedRequest = jdbcPreparedStatementManager.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.ADD,
								sqlAddString);
			}

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, ddbsEntity.getEntityNonIDProperties(), objectToAdd);

			return jdbcConnector.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}
	
	/**
	 * Create Update SQL Query
	 * @param ddbsEntity DDBSEntity
	 * @return UPDATE SQL Query
	 */
	protected String getUpdateSQLString(DDBSEntity<DDBSEntityProperty> ddbsEntity) {
		
		StringBuilder sqlUpdateString = new StringBuilder();

		sqlUpdateString.append("UPDATE ");
		sqlUpdateString.append(ddbsEntity.getDatastoreEntityName());
		sqlUpdateString.append(" SET ");

		Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity.getEntityNonIDProperties().iterator();

		while (iteratorProperties.hasNext()) {

			sqlUpdateString.append(iteratorProperties.next().getPropertyName());
			sqlUpdateString.append(" = ?");

			if (iteratorProperties.hasNext()) {
				sqlUpdateString.append(',');
			}
		}

		sqlUpdateString.append(" WHERE ");

		Iterator<DDBSEntityProperty> iteratorIDProperties = ddbsEntity.getEntityIDProperties().iterator();

		while (iteratorIDProperties.hasNext()) {

			sqlUpdateString.append(iteratorIDProperties.next().getPropertyName());
			sqlUpdateString.append(" = ?");

			if (iteratorIDProperties.hasNext()) {
				sqlUpdateString.append(" AND ");
			}
		}
		return sqlUpdateString.toString();
	}

	@Override
	public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {

		testConnection(objectToUpdate);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToUpdate);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.UPDATE);

			if (preparedRequest == null) {

				String sqlUpdateString = getUpdateSQLString(ddbsEntity);

				preparedRequest = jdbcPreparedStatementManager.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.UPDATE,sqlUpdateString);
			}

			List<DDBSEntityProperty> listPreparedEntities = new ArrayList<>();
			listPreparedEntities.addAll(ddbsEntity.getEntityNonIDProperties());
			listPreparedEntities.addAll(ddbsEntity.getEntityIDProperties());

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, listPreparedEntities, objectToUpdate);

			return jdbcConnector.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}
	
	/**
	 * Create Delete SQL Query
	 * @param ddbsEntity DDBSEntity
	 * @return DELETE SQL Query
	 */
	protected String getDeleteSQLString(DDBSEntity<DDBSEntityProperty> ddbsEntity) {
		
		StringBuilder sqlDeleteString = new StringBuilder();

		sqlDeleteString.append("DELETE FROM ");
		sqlDeleteString.append(ddbsEntity.getDatastoreEntityName());
		sqlDeleteString.append(" WHERE ");

		Iterator<DDBSEntityProperty> iteratorIDProperties = ddbsEntity
				.getEntityIDProperties().iterator();

		while (iteratorIDProperties.hasNext()) {

			sqlDeleteString.append(iteratorIDProperties.next().getPropertyName());
			sqlDeleteString.append(" = ?");

			if (iteratorIDProperties.hasNext()) {
				sqlDeleteString.append(" AND ");
			}
		}
		return sqlDeleteString.toString();
	}

	@Override
	public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

		testConnection(objectToDelete);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToDelete);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.DELETE);

			if (preparedRequest == null) {

				String sqlDeleteString = getDeleteSQLString(ddbsEntity);

				preparedRequest = jdbcPreparedStatementManager.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.DELETE,sqlDeleteString);
			}

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, ddbsEntity.getEntityIDProperties(), objectToDelete);

			return jdbcConnector.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}

	@Override
	public <T extends IEntity> T loadArray(T objectToLoad, String field,
			OrderBy orderBy) throws DDBSToolkitException {

		testConnection(objectToLoad);

		if (objectToLoad != null && field != null && !field.isEmpty()) {
			
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToLoad);

			List<DDBSEntityProperty> idProperties = ddbsEntity.getEntityIDProperties();

			if (idProperties.size() > 0) {
				
				StringBuilder conditionQueryString = new StringBuilder();
				
				Iterator<DDBSEntityProperty> conditionIterator = idProperties.iterator();
				while (conditionIterator.hasNext()) {
					
					DDBSEntityProperty idProperty = conditionIterator.next();
					
					conditionQueryString.append(idProperty.getPropertyName());
					conditionQueryString.append(" = ");
					conditionQueryString.append(idProperty.getValue(objectToLoad));

					if (conditionIterator.hasNext()) {
						conditionQueryString.append(" AND ");
					}
				}

				DDBSEntityProperty propertyName = ddbsEntity
						.getDDBSEntityProperty(field);

				if (propertyName != null) {
					try {
						IEntity objectLinked = (IEntity) Class.forName(propertyName.getObjectTypeName()).newInstance();

						List<IEntity> listObject = listAllWithQueryString(objectLinked,
								conditionQueryString.toString(), orderBy);

						Object array = Array.newInstance(Class.forName(propertyName.getObjectTypeName()), listObject.size());
						
						propertyName.setValue(objectToLoad, array);

						int counterArray = 0;
						for (IEntity entity : listObject) {
							Array.set(array, counterArray, entity);
							counterArray++;
						}

						return objectToLoad;
					} catch (InstantiationException | IllegalAccessException
							| ClassNotFoundException e) {
						throw new DDBSToolkitException("Class "
								+ propertyName.getObjectTypeName()
								+ " not found", e);
					} catch (SecurityException e) {
						throw new DDBSToolkitException("No such field "
								+ field, e);
					}

				} else {
					throw new DDBSToolkitException("No property " + field
							+ " found for object " + ddbsEntity.getDatastoreEntityName());
				}

			} else {
				throw new DDBSToolkitException(
						"There is no primary key defined for object "
								+ ddbsEntity.getDatastoreEntityName());
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean createEntity(IEntity objectToCreate)
			throws DDBSToolkitException {
		throw new UnsupportedOperationException();
	}

	protected <T extends IEntity> List<T> conversionResultSet(
			ResultSet results, T myObject) throws DDBSToolkitException {

		List<T> resultList = new ArrayList<T>();

		DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(myObject);
		
		// List properties
		List<DDBSEntityProperty> listProperties = ddbsEntity.getEntityProperties();

		// For each object
		try {
			while (results.next()) {

				// Instantiate the object
				@SuppressWarnings("unchecked")
				T myData = (T) ddbsEntity.newInstance();

				// Set object properties
				for (DDBSEntityProperty myProperty : listProperties) {

					// If it's not an array
					if (!myProperty.isArray()) {
						if (myProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.INTEGER)) {
							myProperty.setValue(myData, results.getInt(myProperty.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.LONG)) {
							myProperty.setValue(myData, results.getLong(myProperty.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.FLOAT)) {
							myProperty.setValue(myData, results.getFloat(myProperty.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.DOUBLE)) {
							myProperty.setValue(myData, results.getDouble(myProperty.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.STRING)) {
							myProperty.setValue(myData, results.getString(myProperty.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.TIMESTAMP)) {
							myProperty.setValue(myData, results.getTimestamp(myProperty.getPropertyName()));
						}
						
						if(results.wasNull()) {
							myProperty.setValue(myData, null);
						}
					}
				}

				resultList.add(myData);
			}
		} catch (SecurityException se) {
			throw new DDBSToolkitException(
					"Security exception using reflection", se);
		} catch (IllegalArgumentException iae) {
			throw new DDBSToolkitException(
					"Illegal argument exception using reflection", iae);
		} catch (SQLException se) {
			throw new DDBSToolkitException(
					"SQL exception during parsing the request", se);
		}

		return resultList;
	}

}
