package org.ddbstoolkit.toolkit.modules.datastore.postgresql;

import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.jdbc.JDBCEntityManager;

/**
 * Class representing a distributed PostgreSQL Database
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as "1characters" table
 */
public class DistributedPostgreSQLTableManager extends JDBCEntityManager {

    public DistributedPostgreSQLTableManager(PostgreSQLConnector myConnector) {
    	super(myConnector);
    }

    public DistributedPostgreSQLTableManager(PostgreSQLConnector myConnector, Peer myPeer) {
    	super(myConnector);
    }
}
