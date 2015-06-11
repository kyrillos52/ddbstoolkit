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
                case DDBSCommand.LIST_ALL_COMMAND:
                    return entityManager.listAll(myCommand.getObject(), myCommand.getConditionQueryString(), myCommand.getOrderBy());
                case DDBSCommand.READ_COMMAND:
                    return entityManager.read(myCommand.getObject());
                case DDBSCommand.READ_LAST_ELEMENT_COMMAND:
                    return entityManager.readLastElement(myCommand.getObject());
                case DDBSCommand.ADD_COMMAND:
                    return entityManager.add(myCommand.getObject());
                case DDBSCommand.UPDATE_COMMAND:
                    return entityManager.update(myCommand.getObject());
                case DDBSCommand.DELETE_COMMAND:
                    return entityManager.delete(myCommand.getObject());
                case DDBSCommand.LIST_PEERS_COMMAND:
                    return myPeer;
                case DDBSCommand.LOAD_ARRAY_COMMAND:
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
