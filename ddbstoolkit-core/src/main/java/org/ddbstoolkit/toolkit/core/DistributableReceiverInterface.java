package org.ddbstoolkit.toolkit.core;

/**
 * Interface to receive commands
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public interface DistributableReceiverInterface {

    /**
     * Set the entity manager
     * @param entityManager Entity manager to modify
     */
    public void setEntityManager(DistributableEntityManager entityManager);

    /**
     * Return the peer associated with the receiver
     * @return Peer associated with the receiver
     */
    public Peer getMyPeer();

    /**
     * Start to listen on the interface
     * @throws Exception Error during opening the connection
     */
    public void start() throws Exception;

    /**
     * Stop to listen on the interface
     * @throws Exception Error during closing the connection
     */
    public void stop() throws Exception;
}
