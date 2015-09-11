package org.ddbstoolkit.toolkit.jdbc;

import org.ddbstoolkit.toolkit.core.DataModuleTest;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.jdbc.model.Actor;
import org.ddbstoolkit.toolkit.jdbc.model.Film;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;

/**
 * JUnit tests for all JDBC Modules
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class JDBCModuleTest extends DataModuleTest {

	@Override
	protected FilmBase createFilm() {
		return new Film();
	}

	@Override
	protected ActorBase createActor() {
		return new Actor();
	}
	
	@Override
	protected void addReceiverPeerUID(IEntity iEntity) {
		//Do nothing
	}
}
