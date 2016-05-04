package org.ddbstoolkit.toolkit.modules.middleware.jgroups;
import java.sql.Timestamp;
import org.ddbstoolkit.toolkit.jdbc.model.Actor;
import org.ddbstoolkit.toolkit.jdbc.model.Film;
import org.ddbstoolkit.toolkit.middleware.MiddlewareModuleTest;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.DistributedSQLiteTableManager;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.SQLiteConnector;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesReceiver;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesSender;

/**
 * JUnit tests to test JGroups module
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitSQLSpacesModuleTest extends MiddlewareModuleTest {

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
		
		receiverInterface = new SqlSpacesReceiver(new DistributedSQLiteTableManager(new SQLiteConnector(SQLITE_DIRECTORY, SQLITE_DATABASE)), CLUSTER_NAME, RECEIVER_NAME);
		
		senderInterface = new SqlSpacesSender(CLUSTER_NAME, SENDER_NAME);
		
	}

	@Override
	protected FilmBase createFilm() {
		return new Film();
	}

	@Override
	protected ActorBase createActor() {
		return new Actor();
	}
	
	@Override
	protected FilmBase createFilm(Integer filmID, String filmName,
			Integer duration, Timestamp creationDate, Long longField,
			Float floatField) {
		return new Film(filmID, filmName,
				duration, creationDate, longField,
				floatField);
	}

	@Override
	protected ActorBase createActor(Integer actorId, String actorName,
			Integer filmId) {
		return new Actor(actorId, actorName,
				filmId);
	}

}
