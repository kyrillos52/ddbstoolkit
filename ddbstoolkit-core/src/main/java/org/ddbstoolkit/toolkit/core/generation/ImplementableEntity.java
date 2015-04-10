package org.ddbstoolkit.toolkit.core.generation;

import java.sql.ResultSet;
import java.util.List;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;

/**
 * Distributed entity which will not use reflection mechanism
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public interface ImplementableEntity extends IEntity {

	/**
	 * Convert a JDBC ResultSet into an array of objects
	 * @param aResultSet A JDBC ResultSet
	 * @param convertionObject Corresponding conversion object
	 * @return List of converted objects
	 * @throws DDBSToolkitException Generic DDBSToolkit exception
	 */
    <T extends IEntity> List<T> conversionResultSet(ResultSet aResultSet, T convertionObject) throws DDBSToolkitException;
}
