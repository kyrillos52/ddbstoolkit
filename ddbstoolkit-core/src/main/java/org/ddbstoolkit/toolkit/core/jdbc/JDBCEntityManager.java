package org.ddbstoolkit.toolkit.core.jdbc;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.generation.ImplementableEntity;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * JDBC Entity manager
 * 
 * @author Cyril Grandjean
 * @1.0 Class creation
 */
public abstract class JDBCEntityManager implements DistributableEntityManager {

	/**
	 * Database peer
	 */
	protected Peer myPeer;

	/**
	 * Connector SQLite
	 */
	protected JDBCConnector myConnector;

	/**
	 * JDBC Prepared Statement Manager
	 */
	protected JDBCPreparedStatementManager jdbcPreparedStatementManager;
	
	/**
	 * DDBS Entity manager
	 */
	protected DDBSEntityManager<DDBSEntity<DDBSEntityProperty>> ddbsEntityManager;

	/**
	 * JDBC Entity manager constructor
	 * 
	 * @param myConnector
	 *            JDBC Connector
	 */
	public JDBCEntityManager(JDBCConnector myConnector) {
		super();
		this.myConnector = myConnector;
		this.ddbsEntityManager = new DDBSEntityManager<DDBSEntity<DDBSEntityProperty>>(new ClassInspector());
	}

	/**
	 * JDBC Entity manager constructor with peer
	 * 
	 * @param myConnector
	 *            JDBC Connector
	 * @param myPeer
	 *            Peer
	 */
	public JDBCEntityManager(JDBCConnector myConnector, Peer myPeer) {
		this(myConnector);
		this.myPeer = myPeer;
	}

	@Override
	public void setPeer(Peer myPeer) {
		this.myPeer = myPeer;
	}

	@Override
	public Peer getPeer() {
		return this.myPeer;
	}

	@Override
	public boolean isOpen() throws DDBSToolkitException {
		try {
			return myConnector.isOpen();
		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during checking SQL connection", sqle);
		}
	}

	@Override
	public void open() throws DDBSToolkitException {
		try {
			myConnector.open();
			jdbcPreparedStatementManager = new JDBCPreparedStatementManager(
					myConnector);
		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during opening SQL connection", sqle);
		}
	}

	@Override
	public void close() throws DDBSToolkitException {
		try {
			myConnector.close();
			jdbcPreparedStatementManager = null;
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during SQL connection", sqle);
		}
	}

	/**
	 * Test database connection
	 * 
	 * @throws DDBSToolkitException
	 */
	private <T extends IEntity> void testConnection(T object)
			throws DDBSToolkitException {
		try {
			if (!myConnector.isOpen()) {
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

	@Override
	public <T extends IEntity> List<T> listAll(T object,
			List<String> conditionList, String orderBy)
			throws DDBSToolkitException {

		testConnection(object);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);

			StringBuilder listAllQuery = new StringBuilder();

			listAllQuery.append("SELECT ");

			Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity
					.getSupportedPrimaryTypeEntityProperties().iterator();
			while (iteratorProperties.hasNext()) {
				listAllQuery
						.append(iteratorProperties.next().getPropertyName());

				if (iteratorProperties.hasNext()) {
					listAllQuery.append(",");
				}
			}

			listAllQuery.append(" FROM ");
			listAllQuery.append(ddbsEntity.getDatastoreEntityName());

			// If there is conditions
			if (conditionList != null && !conditionList.isEmpty()) {
				listAllQuery.append(" WHERE ");

				Iterator<String> conditionIterator = conditionList.iterator();
				while (conditionIterator.hasNext()) {
					listAllQuery.append(conditionIterator.next());

					if (conditionIterator.hasNext()) {
						listAllQuery.append(" AND ");
					}
				}
			}

			if (orderBy != null && !orderBy.isEmpty()) {
				listAllQuery.append(" ORDER BY ");
				listAllQuery.append(orderBy);
			}

			listAllQuery.append(";");

			ResultSet results = myConnector.query(listAllQuery.toString());

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
	public <T extends IEntity> T read(T object) throws DDBSToolkitException {

		testConnection(object);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.READ);

			if (preparedRequest == null) {

				StringBuilder sqlReadString = new StringBuilder();

				sqlReadString.append("SELECT ");

				Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity
						.getSupportedPrimaryTypeEntityProperties().iterator();

				while (iteratorProperties.hasNext()) {
					sqlReadString.append(iteratorProperties.next()
							.getPropertyName());

					if (iteratorProperties.hasNext()) {
						sqlReadString.append(",");
					}
				}
				
				sqlReadString.append(" FROM ");
				sqlReadString.append(ddbsEntity.getDatastoreEntityName());
				sqlReadString.append(" WHERE ");

				Iterator<DDBSEntityProperty> iteratorIDProperties = ddbsEntity
						.getEntityIDProperties().iterator();

				while (iteratorIDProperties.hasNext()) {

					sqlReadString.append(iteratorIDProperties.next()
							.getPropertyName());

					sqlReadString.append(" = ?");

					if (iteratorIDProperties.hasNext()) {
						sqlReadString.append(" AND ");
					}
				}
				sqlReadString.append(";");

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity,
								PreparedStatementType.READ, sqlReadString.toString());
			}

			prepareParametersPreparedStatement(preparedRequest,
					ddbsEntity.getEntityIDProperties(), object);

			ResultSet results = myConnector
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

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.READ_LAST_ELEMENT);

			if (preparedRequest == null) {

				List<DDBSEntityProperty> ddbsIdProperties = ddbsEntity
						.getEntityIDProperties();

				if (ddbsIdProperties.isEmpty() || ddbsIdProperties.size() > 1) {
					throw new DDBSToolkitException(
							"There is more than one ID property");
				} else {
					StringBuilder sqlReadLastElementString = new StringBuilder();

					sqlReadLastElementString.append("SELECT ");

					Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity
							.getSupportedPrimaryTypeEntityProperties().iterator();

					while (iteratorProperties.hasNext()) {
						sqlReadLastElementString.append(iteratorProperties
								.next().getPropertyName());

						if (iteratorProperties.hasNext()) {
							sqlReadLastElementString.append(",");
						}
					}

					sqlReadLastElementString.append(" FROM ");
					sqlReadLastElementString.append(ddbsEntity.getDatastoreEntityName());
					sqlReadLastElementString.append(" WHERE ");
					sqlReadLastElementString.append(ddbsIdProperties.get(0)
							.getPropertyName());
					sqlReadLastElementString.append(" = (SELECT MAX(");
					sqlReadLastElementString.append(ddbsIdProperties.get(0)
							.getPropertyName());
					sqlReadLastElementString.append(") FROM ");
					sqlReadLastElementString.append(ddbsEntity.getDatastoreEntityName());
					sqlReadLastElementString.append(");");

					preparedRequest = jdbcPreparedStatementManager
							.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.READ_LAST_ELEMENT,
									sqlReadLastElementString.toString());
				}
			}

			ResultSet results = myConnector
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
	public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

		testConnection(objectToAdd);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToAdd);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.ADD);

			if (preparedRequest == null) {

				StringBuilder sqlAddString = new StringBuilder();
				StringBuilder sqlAddPart2String = new StringBuilder();

				sqlAddString.append("INSERT INTO ");
				sqlAddString.append(ddbsEntity.getDatastoreEntityName());
				sqlAddString.append(" (");

				Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity
						.getNotIncrementingEntityProperties().iterator();

				while (iteratorProperties.hasNext()) {

					sqlAddString.append(iteratorProperties.next()
							.getPropertyName());
					sqlAddPart2String.append("?");

					if (iteratorProperties.hasNext()) {
						sqlAddString.append(",");
						sqlAddPart2String.append(",");
					}
				}

				sqlAddString.append(") VALUES (");
				sqlAddString.append(sqlAddPart2String);
				sqlAddString.append(");");

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.ADD,
								sqlAddString.toString());
			}

			prepareParametersPreparedStatement(preparedRequest,
					ddbsEntity.getNotIncrementingEntityProperties(), objectToAdd);

			return myConnector.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}

	@Override
	public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {

		testConnection(objectToUpdate);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToUpdate);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.UPDATE);

			if (preparedRequest == null) {

				StringBuilder sqlUpdateString = new StringBuilder();

				sqlUpdateString.append("UPDATE ");
				sqlUpdateString.append(ddbsEntity.getDatastoreEntityName());
				sqlUpdateString.append(" SET ");

				Iterator<DDBSEntityProperty> iteratorProperties = ddbsEntity
						.getEntityNonIDProperties().iterator();

				while (iteratorProperties.hasNext()) {

					sqlUpdateString.append(iteratorProperties.next()
							.getPropertyName());
					sqlUpdateString.append(" = ?");

					if (iteratorProperties.hasNext()) {
						sqlUpdateString.append(",");
					}
				}

				sqlUpdateString.append(" WHERE ");

				Iterator<DDBSEntityProperty> iteratorIDProperties = ddbsEntity
						.getEntityIDProperties().iterator();

				while (iteratorIDProperties.hasNext()) {

					sqlUpdateString.append(iteratorIDProperties.next()
							.getPropertyName());
					sqlUpdateString.append(" = ?");

					if (iteratorIDProperties.hasNext()) {
						sqlUpdateString.append(" AND ");
					}
				}

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.UPDATE,
								sqlUpdateString.toString());
			}

			List<DDBSEntityProperty> listPreparedEntities = new ArrayList<>();
			listPreparedEntities.addAll(ddbsEntity.getEntityNonIDProperties());
			listPreparedEntities.addAll(ddbsEntity.getEntityIDProperties());

			prepareParametersPreparedStatement(preparedRequest,
					listPreparedEntities, objectToUpdate);

			return myConnector.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}

	@Override
	public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

		testConnection(objectToDelete);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToDelete);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.DELETE);

			if (preparedRequest == null) {

				StringBuilder sqlDeleteString = new StringBuilder();

				sqlDeleteString.append("DELETE FROM ");
				sqlDeleteString.append(ddbsEntity.getDatastoreEntityName());
				sqlDeleteString.append(" WHERE ");

				Iterator<DDBSEntityProperty> iteratorIDProperties = ddbsEntity
						.getEntityIDProperties().iterator();

				while (iteratorIDProperties.hasNext()) {

					sqlDeleteString.append(iteratorIDProperties.next()
							.getPropertyName());
					sqlDeleteString.append(" = ?");

					if (iteratorIDProperties.hasNext()) {
						sqlDeleteString.append(" AND ");
					}
				}

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.DELETE,
								sqlDeleteString.toString());
			}

			prepareParametersPreparedStatement(preparedRequest,
					ddbsEntity.getEntityIDProperties(), objectToDelete);

			return myConnector.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}

	@Override
	public <T extends IEntity> T loadArray(T objectToLoad, String field,
			String orderBy) throws DDBSToolkitException {

		testConnection(objectToLoad);

		if (objectToLoad != null && field != null && !field.isEmpty()) {
			
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToLoad);

			List<DDBSEntityProperty> idProperties = ddbsEntity
					.getEntityIDProperties();

			if (idProperties.size() > 0) {
				List<String> listCondition = new ArrayList<String>();
				for (DDBSEntityProperty idProperty : idProperties) {
					listCondition.add(idProperty.getPropertyName() + " = "
							+ idProperty.getValue(objectToLoad));
				}

				DDBSEntityProperty propertyName = ddbsEntity
						.getDDBSEntityProperty(field);

				if (propertyName != null) {
					try {
						IEntity objectLinked = (IEntity) Class.forName(
								propertyName.getObjectTypeName()).newInstance();

						List<IEntity> listObject = listAll(objectLinked,
								listCondition, orderBy);

						Field f = objectToLoad.getClass().getField(field);

						Object array = Array
								.newInstance(Class.forName(propertyName
										.getObjectTypeName()), listObject
										.size());

						int i = 0;
						for (IEntity entity : listObject) {
							Array.set(array, i, entity);
							i++;
						}

						f.set(objectToLoad, array);

						return objectToLoad;
					} catch (InstantiationException | IllegalAccessException
							| ClassNotFoundException e) {
						throw new DDBSToolkitException("Class "
								+ propertyName.getObjectTypeName()
								+ " not found", e);
					} catch (NoSuchFieldException | SecurityException e) {
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
		
		Class<?> objectClass ;
		try {
			objectClass = Class.forName(ddbsEntity.getFullClassName());
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found using reflection",
					cnfe);
		}

		// For each object
		try {
			while (results.next()) {

				// Instantiate the object
				@SuppressWarnings("unchecked")
				T myData = (T) objectClass.newInstance();

				// Set object properties
				for (DDBSEntityProperty myProperty : listProperties) {
					Field f = myData.getClass().getField(myProperty.getName());

					// If it's not an array
					if (!myProperty.isArray()) {
						if (myProperty.getDdbsToolkitSupportedEntity().equals(
								DDBSToolkitSupportedEntity.INTEGER)) {
							f.set(myData, results.getInt(myProperty
									.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(
								DDBSToolkitSupportedEntity.LONG)) {
							f.set(myData, results.getLong(myProperty
									.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(
								DDBSToolkitSupportedEntity.FLOAT)) {
							f.set(myData, results.getFloat(myProperty
									.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(
								DDBSToolkitSupportedEntity.DOUBLE)) {
							f.set(myData, results.getDouble(myProperty
									.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(
								DDBSToolkitSupportedEntity.STRING)) {
							f.set(myData, results.getString(myProperty
										.getPropertyName()));
						} else if (myProperty.getDdbsToolkitSupportedEntity().equals(
								DDBSToolkitSupportedEntity.TIMESTAMP)) {
							f.set(myData, results.getTimestamp(myProperty
									.getPropertyName()));
						}
					}
				}
				
				if(myData instanceof DistributedEntity && myPeer != null)
				{
					((DistributedEntity)myData).setPeerUid(myPeer.getUid());
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
		} catch (InstantiationException ie) {
			throw new DDBSToolkitException(
					"Problem during instantiation of the object using reflection",
					ie);
		} catch (IllegalAccessException iae) {
			throw new DDBSToolkitException(
					"Illegal access exception using reflection", iae);
		} catch (NoSuchFieldException nsfe) {
			throw new DDBSToolkitException(
					"No such field exception using reflection", nsfe);
		}

		return resultList;
	}

	/**
	 * Prepare SQL Request with parameters
	 * 
	 * @param ddbsEntities
	 *            DDBS Entities
	 * @return
	 * @throws SQLException
	 *             Error when preparing query
	 */
	protected <T extends DDBSEntityProperty> PreparedStatement prepareParametersPreparedStatement(
			PreparedStatement preparedStatement, List<T> ddbsEntityProperties, IEntity entity)
			throws SQLException {
		int counterParameter = 1;
		for (DDBSEntityProperty ddbsEntityProperty : ddbsEntityProperties) {
			if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.INTEGER)) {
				preparedStatement.setInt(counterParameter,
						(Integer) ddbsEntityProperty.getValue(entity));
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.LONG)) {
				preparedStatement.setLong(counterParameter,
						(Long) ddbsEntityProperty.getValue(entity));
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.FLOAT)) {
				preparedStatement.setFloat(counterParameter,
						(Float) ddbsEntityProperty.getValue(entity));
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.DOUBLE)) {
				preparedStatement.setDouble(counterParameter,
						(Double) ddbsEntityProperty.getValue(entity));
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.STRING)) {
				if (ddbsEntityProperty.getValue(entity) != null) {
					preparedStatement.setString(counterParameter,
							(String) ddbsEntityProperty.getValue(entity));
				} else {
					preparedStatement.setString(counterParameter, "");
				}

			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.TIMESTAMP)) {
				preparedStatement.setTimestamp(counterParameter,
						(Timestamp) ddbsEntityProperty.getValue(entity));
			}
			counterParameter++;
		}

		return preparedStatement;
	}

}
