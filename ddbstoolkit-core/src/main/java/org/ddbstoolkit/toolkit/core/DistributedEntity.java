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
     * Peer UID of the entity
     */
    private String peerUid;

    /**
     * Get Peer UID
     * @return Peer UID
     */
	public String getPeerUid() {
		return peerUid;
	}

	/**
	 * Set Peer UID
	 * @param peerUid Peer UID
	 */
	public void setPeerUid(String peerUid) {
		this.peerUid = peerUid;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}    
}
