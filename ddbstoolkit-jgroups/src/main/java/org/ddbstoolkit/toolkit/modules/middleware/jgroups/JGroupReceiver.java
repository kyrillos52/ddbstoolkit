package org.ddbstoolkit.toolkit.modules.middleware.jgroups;

import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.core.Peer;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

/**
 * Receiver using JGroups technology
 * @version 1.0 Creation of the class
 */
public class JGroupReceiver implements RequestHandler, DistributableReceiverInterface {

    /**
     * JGroups channel
     */
    JChannel channel;

    /**
     * Name of the cluster
     */
    String clusterName;

    /**
     * TableManager involved
     */
    DistributableEntityManager entityManager;

    /**
     * Manage synchronous connexion
     */
    MessageDispatcher msgDispatcher;

    /**
     * Current peer of the receiver
     */
    Peer myPeer;

    @Override
    public Peer getMyPeer() {
        return myPeer;
    }

    /**
     * Constructor of the receiver
     * @param entityManager Manager that will execute commands
     * @param clusterName Name of the cluster
     * @param peerName Name of the peer
     */
    public JGroupReceiver(DistributableEntityManager entityManager, String clusterName, String peerName) {
        super();
        this.entityManager = entityManager;
        this.clusterName = clusterName;

        this.myPeer = new Peer();
        this.myPeer.setName(peerName);
    }

    /**
     * Start the receiver
     * @throws Exception
     */
    @Override
    public void start() throws Exception {

        channel=new JChannel();

        msgDispatcher = new MessageDispatcher(channel, null, null, this);

        channel.connect(clusterName);

        this.myPeer.setUid(channel.getAddress().toString());
        entityManager.setPeer(this.myPeer);
    }

    /**
     * Stop the receiver
     */
    @Override
    public void stop() throws Exception {

        channel.close();
    }

    /**
     * Set the entity manager
     * @param entityManager Entity manager to modify
     */
    @Override
    public void setEntityManager(DistributableEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * When received a message
     * @param msg Message received
     * @return
     */
    @Override
    public Object handle(Message msg) throws Exception {
    	
        DDBSCommand myCommand = (DDBSCommand) msg.getObject();

        try {

            entityManager.open();

            switch (myCommand.getAction()) {
                case LIST_ALL:
                    return entityManager.listAllWithQueryString(myCommand.getObject(), myCommand.getConditionQueryString(), myCommand.getOrderBy());
                case READ:
                    return entityManager.read(myCommand.getObject());
                case READ_LAST_ELEMENT:
                    return entityManager.readLastElement(myCommand.getObject());
                case ADD:
                    return entityManager.add(myCommand.getObject());
                case UPDATE:
                    return entityManager.update(myCommand.getObject());
                case DELETE:
                    return entityManager.delete(myCommand.getObject());
                case LIST_PEERS:
                    return myPeer;
                case LOAD_ARRAY:
                    return entityManager.loadArray(myCommand.getObject(), myCommand.getFieldToLoad(), myCommand.getOrderBy());
                default:
                    break;
            }

        } catch (Exception e) {
            throw e;
        }
        finally{
            entityManager.close();
        }

        return null;
    }
}
