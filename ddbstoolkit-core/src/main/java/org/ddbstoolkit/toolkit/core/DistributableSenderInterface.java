package org.ddbstoolkit.toolkit.core;

import java.util.ArrayList;

/**
 * Interface to send commands to the peers
 * User: Cyril GRANDJEAN
 * Date: 21/06/2012
 * Time: 10:11
 *
 * @version Creation of the class
 */
public interface DistributableSenderInterface extends DistributableEntityManager {

    /**
     * Get the list of peers
     * @return list of peers
     * @throws Exception
     */
    ArrayList<Peer> getListPeers() throws Exception;

    /**
     * Set a timeout
     * @param timeout Timeout for this interface
     */
    public void setTimeout(int timeout);
}
