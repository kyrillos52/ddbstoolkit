package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;

/**
 * Class representing a peer
 * User: Cyril GRANDJEAN
 * Date: 18/06/2012
 * Time: 10:58
 *
 * @version Creation of the class
 */
public class Peer implements Serializable {

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
}
