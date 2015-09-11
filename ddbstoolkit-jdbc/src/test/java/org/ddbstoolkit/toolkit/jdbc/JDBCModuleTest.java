package org.ddbstoolkit.toolkit.jdbc;

import org.ddbstoolkit.toolkit.core.DataModuleTest;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.jdbc.model.Actor;
import org.ddbstoolkit.toolkit.jdbc.model.Film;
import org.ddbstoolkit.toolkit.model.interfaces.ActorInterface;
import org.ddbstoolkit.toolkit.model.interfaces.FilmInterface;

/**
 * JUnit tests for all JDBC Modules
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCModuleTest extends DataModuleTest {

	@Override
	protected FilmInterface createFilm() {
		return new Film();
	}

	@Override
	protected ActorInterface createActor() {
		return new Actor();
	}
	
	@Override
	protected void addReceiverPeerUID(IEntity iEntity) {
		//Do nothing
	}
}
