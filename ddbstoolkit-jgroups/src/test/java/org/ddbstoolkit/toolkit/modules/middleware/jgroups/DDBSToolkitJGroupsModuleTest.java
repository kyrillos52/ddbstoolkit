package org.ddbstoolkit.toolkit.modules.middleware.jgroups;

import org.ddbstoolkit.toolkit.middleware.MiddlewareModuleTest;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.DistributedSQLiteTableManager;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.SQLiteConnector;

/**
 * JUnit tests to test JGroups module
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitJGroupsModuleTest extends MiddlewareModuleTest {

	/**
	 * SQLite Directory path String
	 */
	private final static String SQLITE_DIRECTORY = "/Users/Cyril/Desktop/";
	
	/**
	 * SQLLite Database file
	 */
	private final static String SQLITE_DATABASE = "ddbstoolkit.db";
	
	/**
	 * Cluster name
	 */
	private final static String CLUSTER_NAME = "defaultCluster";
	
	/**
	 * Sender peer name
	 */
	private final static String SENDER_NAME = "sender";
	
	/**
	 * Receiver peer name
	 */
	private final static String RECEIVER_NAME = "receiver";
	
	@Override
	public void instantiateReceiverAndSenderInterface() throws Exception {
		
		receiverInterface = new JGroupReceiver(new DistributedSQLiteTableManager(new SQLiteConnector(SQLITE_DIRECTORY, SQLITE_DATABASE)), CLUSTER_NAME, RECEIVER_NAME);
		
		senderInterface = new JGroupSender(CLUSTER_NAME, SENDER_NAME);
		
	}

}
