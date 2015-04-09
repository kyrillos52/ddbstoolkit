package org.ddbstoolkit.toolkit.core;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * Class representing a distributed object
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class DistributedEntity implements IEntity {

    /**
	 * Version Id
	 */
	private static final long serialVersionUID = -3845825399013088782L;
	
	/**
     * Node Id of the entity
     */
    public String node_id;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}    
}
