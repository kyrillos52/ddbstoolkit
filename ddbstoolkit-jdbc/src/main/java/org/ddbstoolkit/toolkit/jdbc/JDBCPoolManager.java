package org.ddbstoolkit.toolkit.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DDBSTransaction;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.TransactionCommand;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;

/**
 * JDBC Entity manager with a connection pool
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCPoolManager extends JDBCEntityManager {

	/**
	 * Connection pool JDBC
	 */
	protected JDBCConnectionPool connectionPool;

	public JDBCPoolManager(JDBCConnectionPool connectionPool) {
		super(null);
		this.connectionPool = connectionPool;
	}

	@Override
	public boolean isOpen() throws DDBSToolkitException {
		return true;
	}

	@Override
	public void open() throws DDBSToolkitException {
		try {
			jdbcConnector = connectionPool.getJDBCConnector(connectionPool.getConnection());
			
			jdbcPreparedStatementManager = new JDBCPreparedStatementManager(
						jdbcConnector);

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during opening SQL connection", sqle);
		}
	}
	
	/**
	 * Test database connection
	 * 
	 * @throws DDBSToolkitException
	 */
	private <T extends IEntity> void testConnection(JDBCConnector connector, T object)
			throws DDBSToolkitException {
		try {
			if (!connector.isOpen()) {
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
	public DDBSTransaction executeTransaction(
			DDBSTransaction transaction)
			throws DDBSToolkitException {
		
		JDBCConnector connection = null; 
		
		try {
			transaction = connectionPool.createSession();
			connection = connectionPool.getJDBCConnector(connectionPool.getConnection(transaction));
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while beginning the transaction", sqle);
		}
		
		JDBCPreparedStatementManager jdbcPreparedStatementManager = new JDBCPreparedStatementManager(
				connection);
	
		for(TransactionCommand transactionCommand : transaction.getTransactionCommands()) {
			switch (transactionCommand.getDataAction()) {
			case ADD:
				add(jdbcPreparedStatementManager, connection, transactionCommand.getEntity());
				break;
			case UPDATE:
				update(jdbcPreparedStatementManager, connection, transactionCommand.getEntity());
				break;
			case DELETE:
				delete(jdbcPreparedStatementManager, connection, transactionCommand.getEntity());
				break;
			case CREATE_ENTITY:
				createEntity(jdbcPreparedStatementManager, connection, transactionCommand.getEntity());
				break;
			default:
				break;
			}
		}
			
		return transaction;
	}
	
	private boolean add(JDBCPreparedStatementManager jdbcPreparedStatementManager, JDBCConnector connection, IEntity objectToAdd) throws DDBSToolkitException {

		testConnection(connection, objectToAdd);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToAdd);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.ADD);

			if (preparedRequest == null) {

				String sqlAddString = getInsertSQLString(ddbsEntity);

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.ADD,
								sqlAddString);
			}

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, ddbsEntity.getEntityIDProperties(), objectToAdd);

			return connection.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}
	
	private boolean update(JDBCPreparedStatementManager jdbcPreparedStatementManager, JDBCConnector connection, IEntity objectToUpdate) throws DDBSToolkitException {

		testConnection(connection, objectToUpdate);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToUpdate);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.UPDATE);

			if (preparedRequest == null) {

				String sqlUpdateString = getUpdateSQLString(ddbsEntity);

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.UPDATE,
								sqlUpdateString);
			}

			List<DDBSEntityProperty> listPreparedEntities = new ArrayList<>();
			listPreparedEntities.addAll(ddbsEntity.getEntityNonIDProperties());
			listPreparedEntities.addAll(ddbsEntity.getEntityIDProperties());

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, ddbsEntity.getEntityIDProperties(), objectToUpdate);

			return connection.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}
	
	public boolean delete(JDBCPreparedStatementManager jdbcPreparedStatementManager, JDBCConnector connection, IEntity objectToDelete) throws DDBSToolkitException {

		testConnection(connection, objectToDelete);

		try {
			DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToDelete);

			PreparedStatement preparedRequest = jdbcPreparedStatementManager
					.getJDBCPreparedStatements(ddbsEntity, PreparedStatementType.DELETE);

			if (preparedRequest == null) {

				String sqlDeleteString = getDeleteSQLString(ddbsEntity);

				preparedRequest = jdbcPreparedStatementManager
						.setJDBCPreparedStatements(ddbsEntity, PreparedStatementType.DELETE,
								sqlDeleteString.toString());
			}

			jdbcConditionConverter.prepareParametersPreparedStatement(preparedRequest, ddbsEntity.getEntityIDProperties(), objectToDelete);

			return connection.executePreparedQuery(preparedRequest) == 1;

		} catch (SQLException sqle) {
			throw new DDBSToolkitException(
					"Error during execution of the SQL request", sqle);
		}
	}
	
	public boolean createEntity(JDBCPreparedStatementManager jdbcPreparedStatementManager, JDBCConnector connection, IEntity objectToCreate)
			throws DDBSToolkitException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void commit(DDBSTransaction transaction) throws DDBSToolkitException {
		try {
			connectionPool.getConnection(transaction).commit();
			connectionPool.endSession(transaction);
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while beginning the transaction", sqle);
		}
	}

	@Override
	public void rollback(DDBSTransaction transaction)
			throws DDBSToolkitException {
		try {
			connectionPool.getConnection(transaction).rollback();
			connectionPool.endSession(transaction);
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while beginning the transaction", sqle);
		}
	}
	
	
}
