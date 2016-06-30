package org.ddbstoolkit.toolkit.modules.middleware.jgroups;

import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.core.DistributedEntityConverter;
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
    private JChannel channel;

    /**
     * Name of the cluster
     */
    private final String clusterName;

    /**
     * TableManager involved
     */
    private DistributableEntityManager entityManager;

    /**
     * Manage synchronous connexion
     */
    private MessageDispatcher msgDispatcher;

    /**
     * Current peer of the receiver
     */
    private final Peer myPeer;
    
    /**
     * Distributed entity converter
     */
    private DistributedEntityConverter entityConverter;

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
     * @throws Exception Exception
     */
    @Override
    public void start() throws Exception {

        channel=new JChannel();

        msgDispatcher = new MessageDispatcher(channel, null, null, this);

        channel.connect(clusterName);

        this.myPeer.setUid(channel.getAddress().toString());
    }

    /**
     * Stop the receiver
     */
    @Override
    public void stop() throws Exception {
        channel.close();
        msgDispatcher.close();
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
     * @return Object handled
     */
    @Override
    public Object handle(Message msg) throws Exception {
    	
        DDBSCommand myCommand = (DDBSCommand) msg.getObject();

        try {

            entityManager.open();

            switch (myCommand.getAction()) {
                case LIST_ALL:
                	if(myCommand.getConditions() != null) {
                		return entityConverter.enrichWithPeerUID(entityManager.listAll(myCommand.getObject(), myCommand.getConditions(), myCommand.getOrderBy()));
                	} else {
                		return entityConverter.enrichWithPeerUID(entityManager.listAllWithQueryString(myCommand.getObject(), myCommand.getConditionQueryString(), myCommand.getOrderBy()));
                	}
                    
                case READ:
                    return entityConverter.enrichWithPeerUID(entityManager.read(myCommand.getObject()));
                case READ_LAST_ELEMENT:
                    return entityConverter.enrichWithPeerUID(entityManager.readLastElement(myCommand.getObject()));
                case ADD:
                    return entityManager.add(myCommand.getObject());
                case UPDATE:
                    return entityManager.update(myCommand.getObject());
                case DELETE:
                    return entityManager.delete(myCommand.getObject());
                case LIST_PEERS:
                    return myPeer;
                case LOAD_ARRAY:
                    return entityConverter.enrichWithPeerUID(entityManager.loadArray(myCommand.getObject(), myCommand.getFieldToLoad(), myCommand.getOrderBy()));
                case COMMIT:
                	entityManager.commit(myCommand.getDDBSTransaction());
                	break;
                case ROLLBACK:
                	entityManager.rollback(myCommand.getDDBSTransaction());
                	break;
                case TRANSACTION:
                	return entityManager.executeTransaction(myCommand.getDDBSTransaction());
                default:
                    break;
            }

        } catch (Exception e) {
            throw e;
        } finally{
            entityManager.close();
        }

        return null;
    }
}
