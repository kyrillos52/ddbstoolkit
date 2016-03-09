package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import java.sql.Connection;

import org.ddbstoolkit.toolkit.jdbc.JDBCConnector;

/**
 * Class representing a connection to SQLite
 * @version 1.0: Creation of the class
 */
public class SQLiteConnector extends JDBCConnector {

	 /**
	 * JDBC Constructor
	 * @param connector Connector
	 */
	public SQLiteConnector(Connection connector) {
		super(connector);
	}
	
    /**
     * Constructor : initialisation of the object
     * @param name SQL Database name
     * @throws ClassNotFoundException Class not found exception
     */
    public SQLiteConnector(String name) throws ClassNotFoundException {
        super("jdbc:sqlite:"+name);

        final String driver = "org.sqlite.JDBC";
        Class.forName(driver);
    }


    public SQLiteConnector(String path, String name) throws ClassNotFoundException {
        this(path+name);
    }
}
