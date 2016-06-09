package org.ddbstoolkit.toolkit.modules.middleware.jgroups;

import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.DistributedSQLiteTableManager;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.SQLiteConnector;

/**
 * Middleware SQL Lite module
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DistributedSQLiteTableManagerTest extends DistributedSQLiteTableManager {

	public DistributedSQLiteTableManagerTest(SQLiteConnector myConnector) {
		super(myConnector);
	}

	@Override
	public void open() throws DDBSToolkitException {
		if(!isOpen()) {
			super.open();
		}
		
	}

	@Override
	public void close() throws DDBSToolkitException {
	}
	
	

}
