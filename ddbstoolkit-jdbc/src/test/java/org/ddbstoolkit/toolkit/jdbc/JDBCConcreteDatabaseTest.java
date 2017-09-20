package org.ddbstoolkit.toolkit.jdbc;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.jdbc.JDBCModuleTest;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConcreteDatabaseTest extends JDBCModuleTest {

    class MySQLConnector extends JDBCConnector {

        /**
         * MySQL JDBC Driver name
         */
        private static final String MYSQL_DRIVER = "org.gjt.mm.mysql.Driver";

        /**
         * MySQL Database URL
         */
        private String url;

        /**
         * MySQL User Login
         */
        private String login;

        /**
         * MySQL User Password
         */
        private String password;

        /**
         * JDBC Constructor
         * @param connector Connector
         */
        public MySQLConnector(Connection connector) {
            super(connector);
        }

        /**
         * Instantiate a MySQL connector
         * @param url MySQL Database URL
         * @param login MySQL User Login
         * @param password MySQL User Password
         * @throws ClassNotFoundException Class not found exception
         */
        public MySQLConnector(final String url, final String login, final String password) throws ClassNotFoundException {
            super(url);
            this.url = url;
            this.login = login;
            this.password = password;

            Class.forName(MYSQL_DRIVER);
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

    class DistributedMySQLTableManager extends JDBCEntityManager {
        public DistributedMySQLTableManager(MySQLConnector myConnector) {
            super(myConnector);
        }
    }


    /**
     * JDBC String
     */
    private static final String JDBC_STRING = "jdbc:mysql://localhost:3306/ddbstoolkit";

    /**
     * JDBC User
     */
    private static final String JDBC_USER = "root";

    /**
     * JDBC Password
     */
    private static final String JDBC_PASSWORD = "";

    @Before
    public void initialiseDatabase() throws ClassNotFoundException, DDBSToolkitException {

        manager = new DistributedMySQLTableManager(new MySQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD));

        manager.open();
    }

    @After
    public void closeConnection() throws DDBSToolkitException {
        if(manager.isOpen()) {
            manager.close();
        }
    }

    @Override
    public void instantiateManager() throws ClassNotFoundException {
        manager = new DistributedMySQLTableManager(new MySQLConnector(JDBC_STRING, JDBC_USER, JDBC_PASSWORD));
    }

    @Override
    protected void addReceiverPeerUID(IEntity iEntity) {
        //Nothing to add
    }
}
