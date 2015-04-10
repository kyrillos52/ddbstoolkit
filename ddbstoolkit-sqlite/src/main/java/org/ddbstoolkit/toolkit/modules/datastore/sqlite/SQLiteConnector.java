package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import java.sql.*;

/**
 * Class representing a connector SQLite
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 11:13
 *
 * @version Creation of the class
 */
public class SQLiteConnector {

    /**
     * Path of the database
     */
    private String path = "";

    /**
     * Name of the database
     */
    private String name = "";

    /**
     * Connection object
     */
    private Connection connector;

    /**
     * Constructor : initialisation of the object
     */
    public SQLiteConnector(String name) {
        super();
        this.name = name;

        final String driver = "org.sqlite.JDBC";
        try {
            //Load the JDBC driver class dynamically.
            Driver d = (Driver)Class.forName(driver).newInstance();
            DriverManager.registerDriver(d);
        }
        catch(Exception e){
            System.out.println("Error during the loading of the driver :"+ e.getMessage());
        }
    }


    public SQLiteConnector(String path, String name) {
        super();

        this.path = path;
        this.name = name;

        final String driver = "org.sqlite.JDBC";
        try {
            //Load the JDBC driver class dynamically.
            Driver d = (Driver)Class.forName(driver).newInstance();
            DriverManager.registerDriver(d);
        }
        catch(Exception e){
            System.out.println("Error during the loading of the driver :"+ e.getMessage());
        }
    }



    /**
     * Function which check if the connection is opened
     * @return boolean
     */
    public boolean isOpen()
    {
        boolean returnValue = false;
        try {
            if(connector == null ||  connector.isClosed())
            {
                returnValue = false;
            }
            else
            {
                returnValue = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Open connection to the database
     */
    public void open()
    {
        try{
            String url = "jdbc:sqlite:"+this.path+this.name;
            this.connector = DriverManager.getConnection(url);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Close connection to the database
     */
    public void close()
    {
        try{
            connector.close();
        }
        catch(Exception e){
            System.out.println("Failure during closing:"+e.getMessage());
        }
    }

    /**
     * Function to launch SQL request
     * @param sql Request to execute (without protection)
     * @return Result of the request
     */
    public int executeQuery(String sql)
    {
        int nbResults = 0;
        try{
            Statement stmt = connector.createStatement();
            nbResults = stmt.executeUpdate(sql);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        return nbResults;
    }

    /**
     * Function to execute prepared query
     * @param preparedRequest SQL request to execute
     * @return Result of the request
     */
    public int executePreparedQuery(PreparedStatement preparedRequest)
    {
        int nbResults = 0;
        try{
            nbResults = preparedRequest.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        return nbResults;
    }

    /**
     * Function to query SQL request
     * @param sql SQL request to execute (without protection)
     * @return Result of the request
     */
    public ResultSet query(String sql)
    {
        ResultSet results = null;
        try{
            Statement stmt = connector.createStatement();
            results = stmt.executeQuery(sql);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        return results;
    }

    /**
     * Query a prepared request
     * @param preparedRequest Prepared Request
     * @return Result of the request
     */
    public ResultSet queryPreparedStatement(PreparedStatement preparedRequest)
    {
        ResultSet results = null;
        try{
            results = preparedRequest.executeQuery();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        return results;
    }



    /**
     * Prepare a request
     * @param sql SQL request to prepare
     * @return Prepared request
     */
    public PreparedStatement prepareStatement(String sql)
    {
        PreparedStatement result = null;
        try {
            result = connector.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
