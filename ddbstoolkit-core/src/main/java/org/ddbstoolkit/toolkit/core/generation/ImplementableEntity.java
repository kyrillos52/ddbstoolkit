package org.ddbstoolkit.toolkit.core.generation;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;

/**
 * Interface implemented an implemented Entity which will not use reflection mechanism
 * Created with IntelliJ IDEA.
 * User: cgrandjean
 * Date: 25/09/13
 * Time: 09:28
 */
public interface ImplementableEntity extends IEntity {

    <T extends IEntity> ArrayList<T> conversionResultSet(ResultSet results, T myObject) throws DDBSToolkitException;
}
