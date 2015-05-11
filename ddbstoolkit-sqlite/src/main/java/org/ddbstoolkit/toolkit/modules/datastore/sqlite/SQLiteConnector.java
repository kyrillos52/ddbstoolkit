package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import org.ddbstoolkit.toolkit.core.jdbc.JDBCConnector;

/**
 * Class representing a connection to SQLite
 * @version 1.0: Creation of the class
 */
public class SQLiteConnector extends JDBCConnector {

    /**
     * Constructor : initialisation of the object
     * @throws ClassNotFoundException 
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
