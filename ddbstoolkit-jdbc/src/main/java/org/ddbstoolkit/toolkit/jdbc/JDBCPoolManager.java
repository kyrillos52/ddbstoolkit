package org.ddbstoolkit.toolkit.jdbc;

import java.sql.SQLException;
import java.util.List;

import org.ddbstoolkit.toolkit.core.DDBSTransaction;
import org.ddbstoolkit.toolkit.core.TransactionCommand;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;

/**
 * JDBC Entity manager with a connection pool
 * @author Cyril Grandjean
 * @1.0 Class creation
 */
public abstract class JDBCPoolManager extends JDBCEntityManager {

	/**
	 * Connection pool JDBC
	 */
	protected JDBCConnectionPool connectionPool;

	public JDBCPoolManager(JDBCConnector jdbcConnector,
			JDBCConnectionPool connectionPool) {
		super(jdbcConnector);
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
	
	@Override
	public DDBSTransaction executeTransaction(
			List<TransactionCommand> transactionCommands)
			throws DDBSToolkitException {
		DDBSTransaction transaction = null;
		
		JDBCConnector connection = jdbcConnector; 
		
		try {
			transaction = connectionPool.createSession();
			connection = connectionPool.getJDBCConnector(connectionPool.getConnection(transaction));
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error while beginning the transaction", sqle);
		}
	
		for(TransactionCommand transactionCommand : transactionCommands) {
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
