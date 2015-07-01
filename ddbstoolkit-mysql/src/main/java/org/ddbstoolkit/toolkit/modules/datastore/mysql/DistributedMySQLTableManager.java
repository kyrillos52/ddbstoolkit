package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.jdbc.JDBCEntityManager;

/**
 * Class representing a distributed MySQL Database
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as "1characters" table
 */
public class DistributedMySQLTableManager extends JDBCEntityManager {

    public DistributedMySQLTableManager(MySQLConnector myConnector) {
    	super(myConnector);
    }

    public DistributedMySQLTableManager(MySQLConnector myConnector, Peer myPeer) {
    	super(myConnector);
    }
}
