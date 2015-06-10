package org.ddbstoolkit.toolkit.core.conditions;

import org.ddbstoolkit.toolkit.core.IEntity;

/**
 * Condition converter
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public interface ConditionsConverter {

	String getConditionsString(Conditions conditions, IEntity entity);
}
