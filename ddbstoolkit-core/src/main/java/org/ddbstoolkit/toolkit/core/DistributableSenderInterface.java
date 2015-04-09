package org.ddbstoolkit.toolkit.core;

import java.util.ArrayList;

/**
 * Interface to send commands to the peers
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public interface DistributableSenderInterface extends DistributableEntityManager {

    /**
     * Get the list of peers
     * @return List of peers
     * @throws Exception
     */
    ArrayList<Peer> getListPeers() throws Exception;

    /**
     * Set a timeout
     * @param timeout Timeout for this interface
     */
    public void setTimeout(int timeout);
}
