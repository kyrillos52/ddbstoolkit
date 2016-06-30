package org.ddbstoolkit.toolkit.modules.middleware.jgroups;
import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.jdbc.model.Actor;
import org.ddbstoolkit.toolkit.jdbc.model.Film;
import org.ddbstoolkit.toolkit.middleware.MiddlewareModuleTest;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.ddbstoolkit.toolkit.modules.datastore.sqlite.SQLiteConnector;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesReceiver;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesSender;

/**
 * JUnit tests to test JGroups module
 * @version 1.0 Creation of the class
 */
public class DDBSToolkitSQLSpacesModuleTest extends MiddlewareModuleTest {
	
	/**
	 * SQLLite Database file
	 */
	private static final String SQLITE_DATABASE = ":memory:";
	
	/**
	 * Cluster name
	 */
	private static final String CLUSTER_NAME = "defaultCluster";
	
	/**
	 * Sender peer name
	 */
	private static final String SENDER_NAME = "sender";
	
	/**
	 * Receiver peer name
	 */
	private static final String RECEIVER_NAME = "receiver";
	
	@Override
	public void instantiateReceiverAndSenderInterface() throws Exception {
				
		receiverInterface = new SqlSpacesReceiver(new DistributedSQLiteTableManagerTest(new SQLiteConnector(SQLITE_DATABASE)), CLUSTER_NAME, RECEIVER_NAME);
		
		senderInterface = new SqlSpacesSender(CLUSTER_NAME, SENDER_NAME);
		
	}

	@Override
	protected FilmBase createFilm() throws DDBSToolkitException {
		FilmBase film = new Film();
		addReceiverPeerUID(film);
		return film;
	}

	@Override
	protected ActorBase createActor() throws DDBSToolkitException {
		ActorBase actor = new Actor();
		addReceiverPeerUID(actor);
		return actor;
	}
	
	@Override
	protected FilmBase createFilm(Integer filmID, String filmName,
			Integer duration, Timestamp creationDate, Long longField,
			Float floatField) throws DDBSToolkitException {
		FilmBase film = new Film(filmID, filmName,
				duration, creationDate, longField,
				floatField);
		addReceiverPeerUID(film);
		return film;
	}

	@Override
	protected ActorBase createActor(Integer actorId, String actorName,
			Integer filmId) throws DDBSToolkitException {
		ActorBase actor = new Actor(actorId, actorName,
				filmId);
		addReceiverPeerUID(actor);
		return actor;
	}

}
