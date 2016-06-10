package org.ddbstoolkit.toolkit.modules.middleware.sqlspaces;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.core.DistributedEntityConverter;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleSpaceException;
import info.collide.sqlspaces.otm.ObjectTupleSpace;

/**
 * SQLSpaces receiver
 * User: Cyril GRANDJEAN
 * Date: 21/06/2012
 * Time: 10:18
 *
 * @version Creation of the class
 */
public class SqlSpacesReceiver implements Callback, DistributableReceiverInterface {

    /**
     * Name of the cluster
     */
    String clusterName;

    /**
     * TableManager involved
     */
    DistributableEntityManager entityManager;

    /**
     * Peer
     */
    Peer myPeer;

    /**
     * TupleSpace for peers
     */
    TupleSpace spacePeers;

    /**
     * TupleSpace for commands
     */
    TupleSpace commandPeers;

    /**
     * Ip address of the server
     */
    String ipAddressServer = "127.0.0.1";

    /**
     * Port of the server
     */
    private int port = 2525;

    /**
     * Registration number
     */
    private int registrationNumber;
    
    /**
     * Distributed entity converter
     */
    private DistributedEntityConverter entityConverter;

    @Override
    public void setEntityManager(DistributableEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Peer getMyPeer() {
        return myPeer;
    }

    /**
     * Create a SqlSpaces Receiver using localhost server
     * @param entityManager Entity manager that will receive commands
     * @param clusterName Name of the cluster
     * @param peerName Name of the peer
     */
    public SqlSpacesReceiver(DistributableEntityManager entityManager, String clusterName, String peerName) {
        super();
        this.entityManager = entityManager;
        this.clusterName = clusterName;

        this.myPeer = new Peer();
        this.myPeer.setName(peerName);
        
        this.entityConverter = new DistributedEntityConverter(this.myPeer);
    }

    /**
     * Create a SqlSpaces Receiver using an external server
     * @param entityManager Entity manager that will receive commands
     * @param clusterName Name of the cluster
     * @param peerName Name of the peer
     * @param ipAddress Ip address of the SQLSpaces Server
     * @param port Port of the SQLSpaces Server
     */
    public SqlSpacesReceiver(DistributableEntityManager entityManager, String clusterName, String peerName, String ipAddress, int port) {
        this(entityManager, clusterName, peerName);
        this.port = port;
        this.ipAddressServer = ipAddress;
    }

    /**
     * Start the receiver
     * @throws Exception Exception
     */
    @Override
    public void start() throws Exception {

        //Register the peer
        spacePeers = new ObjectTupleSpace(ipAddressServer, port, clusterName+"-peers");

        Random generator = new Random();

        Tuple template = new Tuple(String.class);

        //Set an unique identifier
        Tuple[] listPeers = spacePeers.readAll(template);
        if(listPeers.length == 0) {
            myPeer.setUid(String.valueOf(Math.abs(generator.nextInt())));
        } else {
        	
            boolean uidAttributed = false;

            while(!uidAttributed) {
            	
                int uid = Math.abs(generator.nextInt());

                boolean idExist = false;
                for(int counterPeer = 0; counterPeer < listPeers.length; counterPeer++) {
                    String encodedValue = (String) listPeers[counterPeer].getField(0).getValue();
                    Peer myPeer = (Peer)SqlSpacesConverter.fromString(encodedValue);
                    if(String.valueOf(uid).equals(myPeer.getUid()))  {
                        idExist = true;
                    }
                }
                if(!idExist)
                {
                    myPeer.setUid(String.valueOf(uid));
                    uidAttributed = true;
                }
            }
        }

        //Register in the peer space
        spacePeers.write(new Tuple(SqlSpacesConverter.toString(myPeer)));

        commandPeers = new TupleSpace(ipAddressServer, port, clusterName+"-commands");
        
        Tuple tmp = new Tuple(String.class);
        registrationNumber = commandPeers.eventRegister(Command.WRITE, tmp, this, false);
    }

    @Override
    public void call(Command c, int seq, Tuple afterTuple, Tuple beforeTuple) {

        DDBSCommand myCommand;
		try {
			myCommand = SqlSpacesConverter.getObject(afterTuple);
			
			 //If the command is for the receiver
	        if(myCommand.getDestination().equals(Peer.ALL) || myCommand.getDestination().getUid().equals(myPeer.getUid())) {
	        	
	            TupleSpace resultSpace = null;

	            try {

	                //System.out.println("Open connection");
	                entityManager.open();

	                //Write the results into the appropriate space
	                resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+afterTuple.getTupleID());

	                switch (myCommand.getAction()) {
	                    case LIST_ALL:
	                    	
	                    	//Get the list of entities
	                        List<? extends IEntity> results;
	                        
	                        if(myCommand.getConditions() != null) {
	                        	results = entityManager.listAll(myCommand.getObject(), myCommand.getConditions(), myCommand.getOrderBy());
	                        } else {
	                        	results = entityManager.listAllWithQueryString(myCommand.getObject(), myCommand.getConditionQueryString(), myCommand.getOrderBy());
	                        }

	                        for (IEntity iEntity : results) {
	                            resultSpace.write(new Tuple(SqlSpacesConverter.toString(iEntity)));
	                        }

	                        TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+afterTuple.getTupleID());
	                        ackSpace.write(new Tuple(myPeer.getUid()));
	                        ackSpace.disconnect();

	                        break;
	                    case READ:
	                        IEntity entity = entityConverter.enrichWithPeerUID(entityManager.read(myCommand.getObject()));
	                        resultSpace.write(new Tuple(SqlSpacesConverter.toString(entity)));
	                        break;
	                    case READ_LAST_ELEMENT:
	                        IEntity lastEntity = entityConverter.enrichWithPeerUID(entityManager.readLastElement(myCommand.getObject()));
	                        resultSpace.write(new Tuple(SqlSpacesConverter.toString(lastEntity)));
	                        break;
	                    case ADD:
	                        boolean resultAdd = entityManager.add(myCommand.getObject());
	                        resultSpace.write(new Tuple(resultAdd));
	                        break;
	                    case UPDATE:
	                        boolean resultUpdate = entityManager.update(myCommand.getObject());
	                        resultSpace.write(new Tuple(resultUpdate));
	                        break;
	                    case DELETE:
	                        boolean resultdelete = entityManager.delete(myCommand.getObject());
	                        resultSpace.write(new Tuple(resultdelete));
	                        break;
	                    case LOAD_ARRAY:
	                        IEntity loadedEntity = entityManager.loadArray(myCommand.getObject(), myCommand.getFieldToLoad(), myCommand.getOrderBy());
	                        resultSpace.write(new Tuple(SqlSpacesConverter.toString(loadedEntity)));
	                        break;
	                    case SET_AUTOCOMMIT:
	                    	entityManager.setAutoCommit(myCommand.getIsAutocommit());
	                        break;                
	                    case COMMIT:
	                    	entityManager.commit(myCommand.getDDBSTransaction());
	                    	break;
	                    case ROLLBACK:
	                    	entityManager.rollback(myCommand.getDDBSTransaction());
	                    	break;
	                    case TRANSACTION:
	                    	resultSpace.write(new Tuple(SqlSpacesConverter.toString(entityManager.executeTransaction(myCommand.getDDBSTransaction()))));
	                    	break;
	                    case CREATE_ENTITY:
	                    	resultSpace.write(new Tuple(entityManager.createEntity(myCommand.getObject())));
	                    	break;
	                    default:
	                        break;
	                }

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            finally{

	                try {
	                    entityManager.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }

	                try {
	                    resultSpace.disconnect();
	                } catch (TupleSpaceException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

       
    }

    @Override
    public void stop() throws Exception {

        //Disconnect the peer
        spacePeers.take(new Tuple(SqlSpacesConverter.toString(myPeer)));

        //Disconnect the command space
        commandPeers.disconnect();

        //Disconnect the peer space
        spacePeers.disconnect();
    }
}
