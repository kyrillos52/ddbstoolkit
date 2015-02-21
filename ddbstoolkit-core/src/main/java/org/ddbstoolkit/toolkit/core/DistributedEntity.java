package org.ddbstoolkit.toolkit.core;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * Class representing a distributed object
 * User: Cyril GRANDJEAN
 * Date: 18/06/2012
 * Time: 10:25
 *
 * @version Creation of the class
 */
public class DistributedEntity implements IEntity {

    /**
     * Node Id of the entity
     */
    public String node_id;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
    
    
}
