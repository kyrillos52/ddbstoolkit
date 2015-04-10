package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import java.sql.*;

/**
 * Class representing a connection into MySQL Database
 * @author Cyril Grandjean
 * @version 1.0: Creation of the class
 */
public class MySQLConnector {

    /**
     * URL of the connection
     */
    private final String url;

    /**
     * Login of the connection
     */
    private final String login;

    /**
     * Password of the connection
     */
    private final String password;

    /**
     * Connection object
     */
    private Connection connector;

    /**
     * Instantiate a MySQL connector
     * @param url URL of the connector
     * @param login Login of the DB user
     * @param password Password of the DB user
     * @throws ClassNotFoundException 
     */
    public MySQLConnector(final String url, final String login, final String password) throws ClassNotFoundException {
        super();
        this.url = url;
        this.login = login;
        this.password = password;

        final String driver = "org.gjt.mm.mysql.Driver";
        Class.forName(driver);
    }



    /**
     * Function which check if the connection is opened
     * @return boolean
     * @throws SQLException 
     */
    public boolean isOpen() throws SQLException
    {
        boolean returnValue = false;
        
        if(connector == null ||  connector.isClosed())
        {
            returnValue = false;
        }
        else
        {
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Open connection to the database
     * @throws SQLException 
     */
    public void open() throws SQLException
    {
        connector = DriverManager.getConnection(url,login,password);
    }

    /**
     * Close connection to the database
     * @throws SQLException 
     */
    public void close() throws SQLException
    {
        connector.close();
    }

    /**
     * Function to launch SQL request
     * @param sql Request to execute (without protection)
     * @return Result of the request
     * @throws SQLException 
     */
    public int executeQuery(String sql) throws SQLException
    {
        Statement stmt = connector.createStatement();
        int nbResults = stmt.executeUpdate(sql);

        return nbResults;
    }

    /**
     * Function to execute prepared query
     * @param preparedRequest SQL request to execute
     * @return Result of the request
     * @throws SQLException 
     */
    public int executePreparedQuery(java.sql.PreparedStatement preparedRequest) throws SQLException
    {
        int nbResults = preparedRequest.executeUpdate();

        return nbResults;
    }

    /**
     * Function to query SQL request
     * @param sql SQL request to execute (without protection)
     * @return Result of the request
     * @throws SQLException 
     */
    public ResultSet query(String sql) throws SQLException
    {
    	
        Statement stmt = connector.createStatement();
        ResultSet results = stmt.executeQuery(sql);

        return results;
    }

    /**
     * Query a prepared request
     * @param preparedRequest Prepared Request
     * @return Result of the request
     * @throws SQLException 
     */
    public ResultSet queryPreparedStatement(java.sql.PreparedStatement preparedRequest) throws SQLException
    {
        ResultSet results = preparedRequest.executeQuery();

        return results;
    }



    /**
     * Prepare a request
     * @param sql SQL request to prepare
     * @return Prepared request
     * @throws SQLException 
     */
    public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException
    {
        java.sql.PreparedStatement result = connector.prepareStatement(sql);
        return result;
    }
}
