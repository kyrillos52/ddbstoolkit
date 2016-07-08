package org.ddbstoolkit.toolkit.modules.datastore.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.ddbstoolkit.toolkit.jdbc.JDBCConnector;

/**
 * Class representing a connection into a PostgreSQL Database
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 */
public class PostgreSQLConnector extends JDBCConnector {

	/**
	 * PostgreSQL JDBC Driver name
	 */
    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	
    /**
     * PostgreSQL Database URL
     */
    private String url;

    /**
     * PostgreSQL User Login
     */
    private String login;

    /**
     * PostgreSQL User Password
     */
    private String password;
    
    /**
	 * JDBC Constructor
	 * @param connector Connector
	 */
	public PostgreSQLConnector(Connection connector) {
		super(connector);
	}

    /**
     * Instantiate a MySQL connector
     * @param url MySQL Database URL
     * @param login MySQL User Login
     * @param password MySQL User Password
     * @throws ClassNotFoundException Class not found exception
     */
    public PostgreSQLConnector(final String url, final String login, final String password) throws ClassNotFoundException {
        super(url);
        this.url = url;
        this.login = login;
        this.password = password;
        
        Class.forName(POSTGRESQL_DRIVER);
    }

    /**
     * Open connection to the database
     * @throws SQLException SQL Exception
     */
    @Override
    public void open() throws SQLException {
        connector = DriverManager.getConnection(url,login,password);
    }
}
