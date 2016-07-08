package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;

/**
 * Network peer
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class Peer implements Serializable {

	/**
	 * Indicate the we select all peer
	 */
	public static final Peer ALL = new Peer("ALL", null);
	
    /**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Unique identifier (UID) of a peer
     */
    protected String uid;

    /**
     * Name of the peer
     */
    protected String name;
    
    public Peer() {
		super();
	}
    
    public Peer(String uid, String name) {
		super();
		this.uid = uid;
		this.name = name;
	}

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
	public int hashCode() {
		return uid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Peer) {
			return uid.equals(((Peer)obj).getUid());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Peer [uid=" + uid + ", name=" + name + "]";
	}
}
