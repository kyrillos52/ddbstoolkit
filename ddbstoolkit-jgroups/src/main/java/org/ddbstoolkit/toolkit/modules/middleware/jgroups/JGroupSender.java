package org.ddbstoolkit.toolkit.modules.middleware.jgroups;

import org.ddbstoolkit.toolkit.core.DDBSAction;
import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.DistributableSenderInterface;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.ObjectComparator;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to send commands using JGroups technology
 * @version 1.0 Creation of the class
 */
public class JGroupSender extends ReceiverAdapter implements DistributableSenderInterface {

    /**
     * JGroups Channel
     */
    private Channel channel;

    /**
     * JGroups MessageDispatcher
     */
    private MessageDispatcher dispatcher;

    /**
     * Indicate if the connector is open
     */
    private boolean isOpen = false;

    /**
     * Name of the cluster
     */
    private String clusterName;

    /**
     * Timeout of the commands
     */
    private int timeout = 1000;

    /**
     * Name of the peers
     */
    private Peer myPeer;
    
	/**
	 * DDBS Entity manager
	 */
	protected DDBSEntityManager<DDBSEntity<DDBSEntityProperty>> ddbsEntityManager;

    /**
     * Constructor of the sender
     * @param clusterName name of the cluster
     * @param peerName name of the peer
     */
    public JGroupSender(String clusterName, String peerName) {
        super();
        this.clusterName = clusterName;

        this.myPeer = new Peer();
        this.myPeer.setName(peerName);
        this.ddbsEntityManager = new DDBSEntityManager<DDBSEntity<DDBSEntityProperty>>(new ClassInspector());
    }

    /**
     * Get the list of peers
     * @return List of peers
     * @throws Exception
     */
    @Override
    public List<Peer> getListPeers() throws Exception {

        DDBSCommand command = new DDBSCommand();
        command.setAction(DDBSAction.LIST_PEERS);
        command.setObject(null);
        command.setConditionQueryString(null);

        RspList<Peer> rsp_list = dispatcher.castMessage(null,
                new Message(null, null, command), new RequestOptions(ResponseMode.GET_ALL, timeout));

        List<Peer> listPeer = new ArrayList<Peer>();

        //Merge all the results on the same ArrayList
        if(rsp_list.getResults().size() > 0)
        {
            for (Peer peer : rsp_list.getResults()) {
                listPeer.add(peer);
            }
        }

        return listPeer;
    }

    /**
     * Set the maximum timeout of each request
     * @param timeout Timeout for this interface
     */
    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Set the peer associated with this interface
     * @param myPeer Peer to set
     */
    @Override
    public void setPeer(Peer myPeer) {
        this.myPeer = myPeer;
    }

    /**
     * Get the peer associated with this interface
     * @return  Get the current peer
     */
    @Override
    public Peer getPeer() {
        return this.myPeer;
    }

    /**
     * Indicates if the object can send commands
     * @return  boolean
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * Open connections
     * @throws Exception
     */
    @Override
    public void open() throws DDBSToolkitException {

        try {
			channel=new JChannel();
			
			channel.setDiscardOwnMessages(true);

	        dispatcher =new MessageDispatcher(channel, null, null);

	        channel.connect(clusterName);

	        myPeer.setUid(channel.getAddress().toString());

	        isOpen = true;
		} catch (Exception e) {
			throw new DDBSToolkitException("Error opening the connection", e);
		}
        
    }

    /**
     * Stop connections
     */
    @Override
    public void close() {

        channel.close();

        dispatcher.stop();

        isOpen = false;
    }

    /**
     * Get the address associated with a peer name
     * @param peerName Name of the peer
     * @return
     */
    private Address getAddressPeer(String peerName)
    {
        for (Address address : channel.getView().getMembers()) {
            if(address.toString().equals(peerName))
            {
                return address;
            }
        }
        return null;
    }
    
    /**
     * List all entities of a specific object
     * @param object Object to search
     * @param conditions Conditions to filter the results
     * @param orderBy Order By Object
     * @return list of entities that match the request
     * @throws DDBSToolkitException Error during the process
     */
	@Override
	public <T extends IEntity> List<T> listAll(T object, Conditions conditions,
			OrderBy orderBy) throws DDBSToolkitException {
		
		try
    	{
    		//Connection must be established
            if(isOpen == true && object != null)
            {
            	DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);
            	
                DistributedEntity myEntity = (DistributedEntity) object;

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.LIST_ALL);
                command.setObject(object);
                command.setConditions(conditions);
                command.setOrderBy(orderBy);

                RspList<List<T>> rsp_list;
                if(myEntity.getPeerUid() != null && !myEntity.getPeerUid() .isEmpty())
                {
                    Address peerToSend = getAddressPeer(myEntity.getPeerUid() );
                    ArrayList<Address> toSend = new ArrayList<Address>();
                    toSend.add(peerToSend);

                    rsp_list = dispatcher.castMessage(toSend,
                            new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_ALL, timeout));
                    command.setDestination(new Peer(myEntity.getPeerUid(),null) );
                }
                else
                {
                    rsp_list = dispatcher.castMessage(null,
                            new Message(null, null, command), new RequestOptions(ResponseMode.GET_ALL, timeout));
                }

                List<T> listEntity = new ArrayList<T>();

                //Merge all the results on the same ArrayList
                if(rsp_list.getResults().size() > 0)
                {
                    for (List<T> list : rsp_list.getResults()) {
                        listEntity.addAll(list);
                    }
                }

                if((myEntity.getPeerUid() == null || myEntity.getPeerUid() .isEmpty()) && orderBy != null)
                {
                    Collections.sort(listEntity, new ObjectComparator(ddbsEntity, orderBy));
                }


                return listEntity;
            }
            else
            {
            	if(!isOpen())
				{
					throw new DDBSToolkitException("The database connection is not opened");
				}
				else
				{
					throw new DDBSToolkitException("The object passed in parameter is null");
				}
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
	}

    /**
     * List all objects in a network
     * @param object Object to search
     * @param conditionList List of conditions to filter the results
     * @param orderBy String to order the results
     * @return result list
     * @throws Exception
     */
    @Override
    public <T extends IEntity> List<T> listAllWithQueryString(T object, String conditionQueryString, OrderBy orderBy) throws DDBSToolkitException {

    	try
    	{
    		//Connection must be established
            if(isOpen == true && object != null)
            {
            	DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);
            	
                DistributedEntity myEntity = (DistributedEntity) object;

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.LIST_ALL);
                command.setObject(object);
                command.setConditionQueryString(conditionQueryString);
                command.setOrderBy(orderBy);

                RspList<List<T>> rsp_list;
                if(myEntity.getPeerUid() != null && !myEntity.getPeerUid() .isEmpty())
                {
                    Address peerToSend = getAddressPeer(myEntity.getPeerUid() );
                    ArrayList<Address> toSend = new ArrayList<Address>();
                    toSend.add(peerToSend);

                    rsp_list = dispatcher.castMessage(toSend,
                            new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_ALL, timeout));
                    command.setDestination(new Peer(myEntity.getPeerUid(),null) );
                }
                else
                {
                    rsp_list = dispatcher.castMessage(null,
                            new Message(null, null, command), new RequestOptions(ResponseMode.GET_ALL, timeout));
                }

                List<T> listEntity = new ArrayList<T>();

                //Merge all the results on the same ArrayList
                if(rsp_list.getResults().size() > 0)
                {
                    for (List<T> list : rsp_list.getResults()) {
                        listEntity.addAll(list);
                    }
                }

                if((myEntity.getPeerUid() == null || myEntity.getPeerUid() .isEmpty()) && orderBy != null)
                {
                    Collections.sort(listEntity, new ObjectComparator(ddbsEntity, orderBy));
                }


                return listEntity;
            }
            else
            {
            	if(!isOpen())
				{
					throw new DDBSToolkitException("The database connection is not opened");
				}
				else
				{
					throw new DDBSToolkitException("The object passed in parameter is null");
				}
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    /**
     * Read an object in the network
     * @param object object to read
     * @return object read
     * @throws Exception
     */
    @Override
    public <T extends IEntity> T read(T object) throws DDBSToolkitException {

    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) object;

            if(isOpen == true && object != null && myDistributedEntity.getPeerUid()  != null)
            {
                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.READ);
                command.setObject(object);
                command.setConditionQueryString(null);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid() );

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<T> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                T myEntity = null;

                if(rsp_list.getResults().size() > 0)
                {
                    myEntity = rsp_list.getResults().get(0);
                }

                return myEntity;
            }
            else
            {
            	if(!isOpen())
				{
					throw new DDBSToolkitException("The database connection is not opened");
				}
				else
				{
					throw new DDBSToolkitException("The object passed in parameter is null");
				}
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    /**
     * Read the last element added
     * @param object object to read
     * @return last object added
     * @throws Exception
     */
    @Override
    public <T extends IEntity> T readLastElement(T object) throws DDBSToolkitException {

    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) object;

            if(isOpen == true && object != null && myDistributedEntity.getPeerUid()  != null)
            {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.READ_LAST_ELEMENT);
                command.setObject(object);
                command.setConditionQueryString(null);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid());

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<T> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                T myEntity = null;

                if(rsp_list.getResults().size() > 0)
                {
                    myEntity = rsp_list.getResults().get(0);
                }

                return myEntity;
            }
            else
            {
            	if(!isOpen())
				{
					throw new DDBSToolkitException("The database connection is not opened");
				}
				else
				{
					throw new DDBSToolkitException("The object passed in parameter is null");
				}
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    /**
     * Add an object
     * @param objectToAdd Add an object to the database or data source
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToAdd;

            if(isOpen == true && objectToAdd != null && myDistributedEntity.getPeerUid() != null)
            {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.ADD);
                command.setObject(objectToAdd);
                command.setConditionQueryString(null);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid());

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<Boolean> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                boolean result = false;

                if(rsp_list.getResults().size() > 0)
                {
                    result = rsp_list.getResults().get(0);
                }

                return result;
            }
            else
            {
                return false;
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    /**
     * Update an object
     * @param objectToUpdate Object to update
     * @return object updated
     * @throws Exception
     */
    @Override
    public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {

    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToUpdate;

            //Connection must be established
            if(isOpen == true && objectToUpdate != null && myDistributedEntity.getPeerUid()  != null)
            {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.UPDATE);
                command.setObject(objectToUpdate);
                command.setConditionQueryString(null);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid() );

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<Boolean> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                boolean result = false;

                if(rsp_list.getResults().size() > 0)
                {
                    result = rsp_list.getResults().get(0);
                }

                return result;
            }
            else
            {
                return false;
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    /**
     * Delete an object
     * @param objectToDelete Object to delete
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToDelete;

            //Connection must be established
            if(isOpen == true && objectToDelete != null && myDistributedEntity.getPeerUid() != null)
            {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.DELETE);
                command.setObject(objectToDelete);
                command.setConditionQueryString(null);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid());

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<Boolean> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                boolean result = false;

                if(rsp_list.getResults().size() > 0)
                {
                    result = rsp_list.getResults().get(0);
                }

                return result;
            }
            else
            {
                return false;
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    @Override
    public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException {
        
    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToCreate;

            //Connection must be established
            if(isOpen == true && objectToCreate != null && myDistributedEntity.getPeerUid()  != null)
            {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.CREATE_ENTITY);
                command.setObject(objectToCreate);
                command.setConditionQueryString(null);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid());

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<Boolean> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                boolean result = false;

                if(rsp_list.getResults().size() > 0)
                {
                    result = rsp_list.getResults().get(0);
                }

                return result;
            }
            else
            {
                return false;
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }

    /**
     * Load an array
     * @param objectToLoad Object to load
     * @param field Array to load
     * @param orderBy order by field
     * @return  object loaded
     * @throws Exception
     */
    @Override
    public <T extends IEntity> T loadArray(T objectToLoad, String field, OrderBy orderBy) throws DDBSToolkitException {
    	try
    	{
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToLoad;

            //Connection must be established
            if(isOpen == true && objectToLoad != null && myDistributedEntity.getPeerUid() != null && field != null && !field.isEmpty())
            {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.LOAD_ARRAY);
                command.setObject(objectToLoad);
                command.setConditionQueryString(null);
                command.setFieldToLoad(field);
                command.setOrderBy(orderBy);

                Address peerToSend = getAddressPeer(myDistributedEntity.getPeerUid());

                ArrayList<Address> toSend = new ArrayList<Address>();
                toSend.add(peerToSend);

                RspList<T> rsp_list = dispatcher.castMessage(toSend,
                        new Message(peerToSend, null, command), new RequestOptions(ResponseMode.GET_FIRST, timeout));

                T myEntity = null;

                if(rsp_list.getResults().size() > 0)
                {
                    myEntity = rsp_list.getResults().get(0);
                }

                return myEntity;
            }
            else
            {
                return null;
            }
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
    }
}
