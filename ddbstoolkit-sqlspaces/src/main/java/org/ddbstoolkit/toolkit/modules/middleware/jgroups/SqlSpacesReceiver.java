package org.ddbstoolkit.toolkit.modules.middleware.jgroups;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.TupleSpaceException;
import info.collide.sqlspaces.otm.ObjectTupleSpace;
import info.collide.sqlspaces.commons.Callback;
import info.collide.sqlspaces.commons.Tuple;

import java.util.ArrayList;
import java.util.Random;

import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;

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
    int registrationNumber;

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
        this.clusterName = clusterName;
        this.entityManager = entityManager;
        this.port = port;
        this.ipAddressServer = ipAddress;
        this.myPeer = new Peer();
        this.myPeer.setName(peerName);
    }

    /**
     * Start the receiver
     * @throws Exception
     */
    @Override
    public void start() throws Exception {

        //Register the peer
        spacePeers = new ObjectTupleSpace(ipAddressServer, port, clusterName+"-peers");

        Random generator = new Random();

        Tuple template = new Tuple(String.class);

        //Set an unique identifier
        Tuple[] listPeers = spacePeers.readAll(template);
        if(listPeers.length == 0)
        {
            myPeer.setUid(String.valueOf(Math.abs(generator.nextInt())));
        }
        else
        {
            boolean uidAttributed = false;

            while(!uidAttributed)
            {
                int uid = Math.abs(generator.nextInt());

                boolean idExist = false;
                for(int i = 0; i < listPeers.length; i++)
                {
                    String encodedValue = (String) listPeers[i].getField(0).getValue();
                    Peer myPeer = (Peer)SqlSpacesConverter.fromString(encodedValue);
                    if(String.valueOf(uid).equals(myPeer.getUid()))
                    {
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

        //Set the entity manager with the new peer
        this.entityManager.setPeer(myPeer);

        //Register in the peer space
        spacePeers.write(new Tuple(SqlSpacesConverter.toString(myPeer)));

        commandPeers = new TupleSpace(ipAddressServer, port, clusterName+"-commands");
        Tuple tmp = new Tuple(Integer.class, String.class, String.class, String.class, String.class, String.class);
        registrationNumber = commandPeers.eventRegister(Command.WRITE, tmp, this, false);
    }

    @Override
    public void call(Command c, int seq, Tuple afterTuple, Tuple beforeTuple) {

        DDBSCommand myCommand = SqlSpacesConverter.getObject(afterTuple);

        //System.out.println("Request received");

        //System.out.println("Action n°"+ myCommand.getAction());

        //If the command is for the receiver
        if(myCommand.getDestination().equals(DDBSCommand.DESTINATION_ALL_PEERS) || myCommand.getDestination().equals(myPeer.getUid()))
        {
            TupleSpace resultSpace = null;

            try {

                //System.out.println("Open connection");
                entityManager.open();

                //System.out.println("Write in the space = "+clusterName+"-results-"+afterTuple.getTupleID());

                //Write the results into the appropriate space
                resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+afterTuple.getTupleID());

                switch (myCommand.getAction()) {
                    case DDBSCommand.LIST_ALL_COMMAND:
                        //Get the list of entities
                        ArrayList<? extends IEntity> results = entityManager.listAll(myCommand.getObject(), myCommand.getConditionList(), myCommand.getOrderBy());

                        //System.out.println("Size = "+results.size());

                        for (IEntity iEntity : results) {
                            resultSpace.write(new Tuple(SqlSpacesConverter.toString(iEntity)));
                        }

                        TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+afterTuple.getTupleID());
                        ackSpace.write(new Tuple(myPeer.getUid()));
                        ackSpace.disconnect();

                        break;
                    case DDBSCommand.READ_COMMAND:
                        IEntity entity = entityManager.read(myCommand.getObject());
                        resultSpace.write(new Tuple(SqlSpacesConverter.toString(entity)));
                        break;
                    case DDBSCommand.READ_LAST_ELEMENT_COMMAND:
                        IEntity lastEntity = entityManager.readLastElement(myCommand.getObject());
                        resultSpace.write(new Tuple(SqlSpacesConverter.toString(lastEntity)));
                        break;
                    case DDBSCommand.ADD_COMMAND:
                        boolean resultAdd = entityManager.add(myCommand.getObject());
                        resultSpace.write(new Tuple(resultAdd));
                        break;
                    case DDBSCommand.UPDATE_COMMAND:
                        boolean resultUpdate = entityManager.update(myCommand.getObject());
                        resultSpace.write(new Tuple(resultUpdate));
                        break;
                    case DDBSCommand.DELETE_COMMAND:
                        boolean resultdelete = entityManager.delete(myCommand.getObject());
                        resultSpace.write(new Tuple(resultdelete));
                        break;
                    case DDBSCommand.LOAD_ARRAY_COMMAND:
                        IEntity loadedEntity = entityManager.loadArray(myCommand.getObject(), myCommand.getFieldToLoad(), myCommand.getOrderBy());
                        resultSpace.write(new Tuple(SqlSpacesConverter.toString(loadedEntity)));
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{

                //System.out.println("Close connection");

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
