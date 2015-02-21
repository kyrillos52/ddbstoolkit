package org.ddbstoolkit.toolkit.core;

/**
 * Interface to receive commands
 * User: Cyril GRANDJEAN
 * Date: 21/06/2012
 * Time: 10:12
 *
 * @version Creation of the class
 */
public interface DistributableReceiverInterface {

    /**
     * Set the entity manager
     * @param entityManager Entity manager to modify
     */
    public void setEntityManager(DistributableEntityManager entityManager);

    /**
     * Return the peer associated with the interface
     * @return
     */
    public Peer getMyPeer();

    /**
     * Start to listen on the interface
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * Stop to listen on the interface
     * @throws Exception
     */
    public void stop() throws Exception;
}
