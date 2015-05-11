package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import java.sql.*;

import org.ddbstoolkit.toolkit.core.jdbc.JDBCConnector;

/**
 * Class representing a connection into a MySQL Database
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 */
public class MySQLConnector extends JDBCConnector {

    /**
     * MySQL Database URL
     */
    private final String url;

    /**
     * MySQL User Login
     */
    private final String login;

    /**
     * MySQL User Password
     */
    private final String password;

    /**
     * Instantiate a MySQL connector
     * @param url MySQL Database URL
     * @param login MySQL User Login
     * @param password MySQL User Password
     * @throws ClassNotFoundException 
     */
    public MySQLConnector(final String url, final String login, final String password) throws ClassNotFoundException {
        super(url);
        this.url = url;
        this.login = login;
        this.password = password;

        final String driver = "org.gjt.mm.mysql.Driver";
        Class.forName(driver);
    }

    /**
     * Open connection to the database
     * @throws SQLException 
     */
    @Override
    public void open() throws SQLException
    {
        connector = DriverManager.getConnection(url,login,password);
    }
}
