package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;

/**
 * Class representing a peer
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class Peer implements Serializable {

    /**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 886731428324212350L;

	/**
     * Unique identifier (UID) of a peer
     */
    protected String uid;

    /**
     * Name of the peer
     */
    protected String name;

    /**
     * Get the UID of the peer
     * @return UID of the peer
     */
    public String getUid() {
        return uid;
    }

    /**
     * Set the UID of the peer
     * @param uid UID of the peer
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get the name of the peer
     * @return name of the peer
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the peer
     * @param name name of the peer
     */
    public void setName(String name) {
        this.name = name;
    }

	@Override
	public String toString() {
		return "Peer [uid=" + uid + ", name=" + name + "]";
	}
}
