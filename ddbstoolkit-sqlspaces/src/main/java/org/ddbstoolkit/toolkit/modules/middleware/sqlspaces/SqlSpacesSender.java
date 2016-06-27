package org.ddbstoolkit.toolkit.modules.middleware.sqlspaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddbstoolkit.toolkit.core.DDBSAction;
import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.DDBSTransaction;
import org.ddbstoolkit.toolkit.core.DistributableSenderInterface;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.ObjectComparator;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.TransactionCommand;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;

import info.collide.sqlspaces.client.TupleSpace;
import info.collide.sqlspaces.commons.Tuple;
import info.collide.sqlspaces.commons.TupleID;
import info.collide.sqlspaces.commons.TupleSpaceException;

/**
 * Class to send commands using SQLSpaces
 * User: Cyril GRANDJEAN
 * Date: 21/06/2012
 * Time: 11:39
 *
 * @version Creation of the class
 */
public class SqlSpacesSender implements DistributableSenderInterface {

    /**
     * Peer corresponding to the interface
     */
    private Peer myPeer;

    /**
     * Indicate if the connection is open
     */
    private boolean isOpen = false;

    /**
     * Maximum timeout
     */
    private int timeout = 1000;

    /**
     * Name of the cluster
     */
    private String clusterName;

    /**
     * TupleSpace for peers
     */
    private TupleSpace spacePeers;

    /**
     * TupleSpace for commands
     */
    private TupleSpace commandPeers;

    /**
     * Ip address of the server
     */
    private String ipAddressServer;

    /**
     * Port of the server
     */
    private int port;
    
	/**
	 * DDBS Entity manager
	 */
	protected DDBSEntityManager<DDBSEntity<DDBSEntityProperty>> ddbsEntityManager;

    /**
     * Create a SqlSpaces Sender using localhost server
     * @param clusterName Name of the cluster
     * @param peerName Name of the peer
     */
    public SqlSpacesSender(String clusterName, String peerName) {
        super();
        this.clusterName = clusterName;

        this.ipAddressServer = "127.0.0.1";
        this.port = 2525;
        this.myPeer = new Peer();
        this.myPeer.setName(peerName);
        this.ddbsEntityManager = new DDBSEntityManager<DDBSEntity<DDBSEntityProperty>>(new ClassInspector());
    }

    /**
     * Create a SqlSpaces Sender using an external server
     * @param clusterName Name of the cluster
     * @param peerName Name of the peer
     * @param ipAddress Ip address of the SQLSpaces Server
     * @param port Port of the SQLSpaces Server
     */
    public SqlSpacesSender(String clusterName, String peerName, String ipAddress, int port) {
        this.clusterName = clusterName;
        this.port = port;
        this.ipAddressServer = ipAddress;
        this.myPeer = new Peer();
        this.myPeer.setName(peerName);
        this.ddbsEntityManager = new DDBSEntityManager<DDBSEntity<DDBSEntityProperty>>(new ClassInspector());
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void open() throws DDBSToolkitException {

        //Get the peers space
        try {
			spacePeers = new TupleSpace(ipAddressServer, port, clusterName+"-peers");
			
			commandPeers = new TupleSpace(ipAddressServer, port, clusterName+"-commands");

	        isOpen = true;
		} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}  
    }

    @Override
    public void close() throws DDBSToolkitException{

    	try {
    		spacePeers.disconnect();

            commandPeers.disconnect();

            isOpen = false;
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} 
    }

    @SuppressWarnings("unchecked")
	@Override
	public <T extends IEntity> List<T> listAllWithQueryString(T object, String conditionQueryString, OrderBy orderBy) throws DDBSToolkitException {
	   
    	testConnection(object);
    	
    	try {
    		
	        //Connection must be established
	        if(isOpen && object != null) {
	            DistributedEntity myEntity = (DistributedEntity) object;
	            
	            DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);
	
	            DDBSCommand command = new DDBSCommand();
	            command.setAction(DDBSAction.LIST_ALL);
	            command.setConditionQueryString(conditionQueryString);
	
	            if(myEntity.getPeerUid() != null && !myEntity.getPeerUid().isEmpty()) {
	                command.setDestination(new Peer(myEntity.getPeerUid(), null));
	            } else {
	                command.setDestination(Peer.ALL);
	            }
	            command.setOrderBy(orderBy);
	            command.setObject(object);
	
	            //Get the number of peers
	            int numberOfPeers = getListPeers().size();
	
	            TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));
	
	            //Space to receive ACK
	            TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+id);
	
	            Tuple template = new Tuple(String.class);
	
	            long endTime = System.currentTimeMillis() + timeout;
	
	            //Wait for the answers
	            while((endTime - System.currentTimeMillis() > 0) && numberOfPeers > 0) {
	                ackSpace.waitToTake(template,(endTime - System.currentTimeMillis()));
	
	                numberOfPeers--;
	            }
	
	            ackSpace.disconnect();
	
	            TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
	
	            Tuple[] list = resultSpace.takeAll(template);
	
	            List<T> returnList = new ArrayList<T>();
	
	            for(int counterTuple = 0; counterTuple < list.length; counterTuple++) {
	                Tuple myTuple = list[counterTuple];
	                String encodedValue = (String) myTuple.getField(0).getValue();
	                returnList.add((T) SqlSpacesConverter.fromString(encodedValue));
	            }
	
	            if((myEntity.getPeerUid() == null || myEntity.getPeerUid().isEmpty()) && orderBy != null) {
	                Collections.sort(returnList, new ObjectComparator(ddbsEntity, orderBy));
	            }
	
	            resultSpace.disconnect();
	
	            return returnList;
	        } else {
	            return null;
	        }
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} 
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T extends IEntity> T read(T object) throws DDBSToolkitException {

    	testConnection(object);
    	
    	try {
    		DistributedEntity myDistributedEntity = (DistributedEntity) object;

            if(isOpen && object != null && myDistributedEntity.getPeerUid() != null) {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.READ);
                command.setDestination(new Peer(myDistributedEntity.getPeerUid(),null));
                command.setObject(object);

                TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                Tuple template = new Tuple(String.class);
                Tuple result = resultSpace.waitToTake(template);

                resultSpace.disconnect();

                return (T) SqlSpacesConverter.fromString((String) result.getField(0).getValue());
            } else {
                return null;
            }
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} catch (IOException e) {
			throw new DDBSToolkitException("Error executing the middleware request - IO Exception", e);
		} catch (ClassNotFoundException e) {
			throw new DDBSToolkitException("Class not found exception", e);
		} catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} 
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T extends IEntity> T readLastElement(T object) throws DDBSToolkitException {

    	testConnection(object);
    	
    	try {
    		DistributedEntity myDistributedEntity = (DistributedEntity) object;

            if(isOpen && object != null && myDistributedEntity.getPeerUid() != null) {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.READ_LAST_ELEMENT);
                command.setDestination(new Peer(myDistributedEntity.getPeerUid(),null));
                command.setObject(object);

                TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                Tuple template = new Tuple(String.class);
                Tuple result = resultSpace.waitToTake(template);

                resultSpace.disconnect();

                return (T) SqlSpacesConverter.fromString((String) result.getField(0).getValue());
            } else {
                return null;
            }
    	}
    	catch (TupleSpaceException tse) {
			throw new DDBSToolkitException("Error executing the middleware request", tse);
		} catch (IOException ioe) {
			throw new DDBSToolkitException("Error executing the middleware request - IO Exception", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found exception", cnfe);
		}
    	catch (Exception e) {
    		throw new DDBSToolkitException("Error executing the middleware request", e);
    	}
    }

    @Override
    public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

    	testConnection(objectToAdd);
    	
    	try {
    		
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToAdd;

            if(isOpen && objectToAdd != null && myDistributedEntity.getPeerUid() != null) {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.ADD);
                command.setDestination(new Peer(myDistributedEntity.getPeerUid(),null));
                command.setObject(myDistributedEntity);

                TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                Tuple template = new Tuple(Boolean.class);
                Tuple result = resultSpace.waitToTake(template);

                resultSpace.disconnect();

                return (Boolean) result.getField(0).getValue();
            } else {
                return false;
            }
    	} catch (TupleSpaceException tse) {
			throw new DDBSToolkitException("Error executing the middleware request", tse);
		} catch (IOException ioe) {
			throw new DDBSToolkitException("Error executing the middleware request - IO Exception", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found exception", cnfe);
		} catch (Exception e) {
    		throw new DDBSToolkitException("Error executing the middleware request", e);
    	}
    }

    @Override
    public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {
    	
    	testConnection(objectToUpdate);
    	
    	try {
    		DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToUpdate);
    		
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToUpdate;

            //Connection must be established
            if(isOpen && objectToUpdate != null && myDistributedEntity.getPeerUid() != null) {

                //List of primary keys
            	List<DDBSEntityProperty> listPrimaryKeys = ddbsEntity.getEntityIDProperties();

                DistributedEntity myEntity = (DistributedEntity) objectToUpdate;

                if(listPrimaryKeys.isEmpty() || myEntity.getPeerUid() == null) {
                    return false;
                } else {

                    DDBSCommand command = new DDBSCommand();
                    command.setAction(DDBSAction.UPDATE);
                    command.setDestination(new Peer(myDistributedEntity.getPeerUid(), null));
                    command.setObject(myDistributedEntity);

                    TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                    TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                    Tuple template = new Tuple(Boolean.class);
                    Tuple result = resultSpace.waitToTake(template);

                    resultSpace.disconnect();

                    return (Boolean) result.getField(0).getValue();
                }
            } else {
                return false;
            }
    	} catch (TupleSpaceException tse) {
			throw new DDBSToolkitException("Error executing the middleware request", tse);
		} catch (IOException ioe) {
			throw new DDBSToolkitException("Error executing the middleware request - IO Exception", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found exception", cnfe);
		}
    	catch (Exception e) {
    		throw new DDBSToolkitException("Error executing the middleware request", e);
    	}
    }

    @Override
    public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

    	testConnection(objectToDelete);
    	
    	try {
    		
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToDelete;
    		
    		DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(objectToDelete);

            //Connection must be established
            if(isOpen && objectToDelete != null && myDistributedEntity.getPeerUid() != null) {

                //Check the primary key
                List<DDBSEntityProperty> listPrimaryKeys = ddbsEntity.getEntityIDProperties();

                DistributedEntity myEntity = (DistributedEntity) objectToDelete;

                if(listPrimaryKeys.isEmpty() || myEntity.getPeerUid() == null) {
                    return false;
                } else {

                    DDBSCommand command = new DDBSCommand();
                    command.setAction(DDBSAction.DELETE);
                    command.setDestination(new Peer(myDistributedEntity.getPeerUid(),null));
                    command.setObject(myDistributedEntity);

                    TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                    TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                    Tuple template = new Tuple(Boolean.class);
                    Tuple result = resultSpace.waitToTake(template);

                    resultSpace.disconnect();

                    return (Boolean) result.getField(0).getValue();
                }
            } else {
                return false;
            }
    	} catch (TupleSpaceException tse) {
			throw new DDBSToolkitException("Error executing the middleware request", tse);
		} catch (IOException ioe) {
			throw new DDBSToolkitException("Error executing the middleware request - IO Exception", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found exception", cnfe);
		}
    	catch (Exception e) {
    		throw new DDBSToolkitException("Error executing the middleware request", e);
    	}
    }

    @Override
    public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException {

    	try {
    		DistributedEntity myDistributedEntity = (DistributedEntity) objectToCreate;

            if(isOpen && objectToCreate != null && myDistributedEntity.getPeerUid() != null) {

                DDBSCommand command = new DDBSCommand();
                command.setAction(DDBSAction.CREATE_ENTITY);
                command.setDestination(new Peer(myDistributedEntity.getPeerUid(),null));
                command.setObject(objectToCreate);

                TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                Tuple template = new Tuple(Boolean.class);
                Tuple result = resultSpace.waitToTake(template);

                resultSpace.disconnect();

                return (Boolean) result.getField(0).getValue();
            } else {
                return false;
            }
    	} catch (TupleSpaceException tse) {
			throw new DDBSToolkitException("Error executing the middleware request", tse);
		} catch (IOException ioe) {
			throw new DDBSToolkitException("Error executing the middleware request - IO Exception", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found exception", cnfe);
		} catch (Exception e) {
    		throw new DDBSToolkitException("Error executing the middleware request", e);
    	}
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T extends IEntity> T loadArray(T objectToLoad, String field, OrderBy orderBy) throws DDBSToolkitException {

    	testConnection(objectToLoad);
    	
    	DistributedEntity myDistributedEntity = (DistributedEntity) objectToLoad;
    	
    	if(myDistributedEntity.getPeerUid() != null && field != null && !field.isEmpty()) {
    
			try {

	            DDBSCommand command = new DDBSCommand();
	            command.setAction(DDBSAction.LOAD_ARRAY);
	            command.setDestination(new Peer(myDistributedEntity.getPeerUid(),null));
	            command.setObject(objectToLoad);
	            command.setFieldToLoad(field);
	            command.setOrderBy(orderBy);
	
	            TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));
	
	            TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
	            Tuple template = new Tuple(String.class);
	            Tuple result = resultSpace.waitToTake(template);
	
	            resultSpace.disconnect();
	
	            return (T) SqlSpacesConverter.fromString((String) result.getField(0).getValue());
		   
			} catch (TupleSpaceException tse) {
				throw new DDBSToolkitException("Error executing the middleware request", tse);
			} catch (IOException ioe) {
				throw new DDBSToolkitException("Error executing the middleware request - IO Exception", ioe);
			} catch (ClassNotFoundException cnfe) {
				throw new DDBSToolkitException("Class not found exception", cnfe);
			} catch (Exception e) {
				throw new DDBSToolkitException("Error executing the middleware request", e);
			}
    	} else {
        	throw new IllegalArgumentException("Error in the parameters");
        }
    }

    @Override
    public List<Peer> getListPeers() throws Exception {

        Tuple template = new Tuple(String.class);

        Tuple[] list = spacePeers.readAll(template);

        List<Peer> listPeers = new ArrayList<Peer>();
        for(int counterList = 0; counterList < list.length; counterList++) {
            String peerString = (String)list[counterList].getField(0).getValue();
            listPeers.add((Peer) SqlSpacesConverter.fromString(peerString));
        }
        return listPeers;
    }

	@Override
	public <T extends IEntity> List<T> listAll(T object, Conditions conditions,
			OrderBy orderBy) throws DDBSToolkitException {
		
		try {
    		
	        //Connection must be established
	        if(isOpen && object != null) {
	            DistributedEntity myEntity = (DistributedEntity) object;
	            
	            DDBSEntity<DDBSEntityProperty> ddbsEntity = ddbsEntityManager.getDDBSEntity(object);
	
	            DDBSCommand command = new DDBSCommand();
	            command.setAction(DDBSAction.LIST_ALL);
	            command.setConditions(conditions);
	
	            if(myEntity.getPeerUid() != null && !myEntity.getPeerUid().isEmpty()) {
	                command.setDestination(new Peer(myEntity.getPeerUid(), null));
	            } else {
	                command.setDestination(Peer.ALL);
	            }
	            command.setOrderBy(orderBy);
	            command.setObject(object);
	
	            //Get the number of peers
	            int numberOfPeers = getListPeers().size();
	
	            TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));
	
	            //Space to receive ACK
	            TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+id);
	
	            Tuple template = new Tuple(String.class);
	
	            long endTime = System.currentTimeMillis() + timeout;
	
	            //Wait for the answers
	            while((endTime - System.currentTimeMillis() > 0) && numberOfPeers > 0) {
	                ackSpace.waitToTake(template,(endTime - System.currentTimeMillis()));
	
	                numberOfPeers--;
	            }
	
	            ackSpace.disconnect();
	
	            TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
	
	            Tuple[] list = resultSpace.takeAll(template);
	
	            List<T> returnList = new ArrayList<T>();
	
	            for(int counterTuple = 0; counterTuple < list.length; counterTuple++) {
	                Tuple myTuple = list[counterTuple];
	                String encodedValue = (String) myTuple.getField(0).getValue();
	                returnList.add((T) SqlSpacesConverter.fromString(encodedValue));
	            }
	
	            if((myEntity.getPeerUid() == null || myEntity.getPeerUid().isEmpty()) && orderBy != null) {
	                Collections.sort(returnList, new ObjectComparator(ddbsEntity, orderBy));
	            }
	
	            resultSpace.disconnect();
	
	            return returnList;
	        } else {
	            return null;
	        }
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
	}

	@Override
	public void setAutoCommit(boolean isAutoCommit) throws DDBSToolkitException {
		
		try {
    		
	        //Connection must be established
	        if(isOpen) {
	
	            DDBSCommand command = new DDBSCommand();
	            command.setAction(DDBSAction.SET_AUTOCOMMIT);
	            command.setIsAutocommit(isAutoCommit);
	            command.setDestination(Peer.ALL);
	
	            //Get the number of peers
	            int numberOfPeers = getListPeers().size();
	
	            TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));
	
	            //Space to receive ACK
	            TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+id);
	
	            Tuple template = new Tuple(String.class);
	
	            long endTime = System.currentTimeMillis() + timeout;
	
	            //Wait for the answers
	            while((endTime - System.currentTimeMillis() > 0) && numberOfPeers > 0) {
	                ackSpace.waitToTake(template,(endTime - System.currentTimeMillis()));
	
	                numberOfPeers--;
	            }
	
	            ackSpace.disconnect();
	
	            TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
	
	            resultSpace.takeAll(template);
	
	            resultSpace.disconnect();
	        }
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
		
	}

	@Override
	public void commit(DDBSTransaction transaction) throws DDBSToolkitException {
		
		try {
    		
	        //Connection must be established
	        if(isOpen) {
	
	            DDBSCommand command = new DDBSCommand();
	            command.setAction(DDBSAction.COMMIT);
	            command.setDDBSTransaction(transaction);
	            command.setDestination(Peer.ALL);
	
	            //Get the number of peers
	            int numberOfPeers = getListPeers().size();
	
	            TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));
	
	            //Space to receive ACK
	            TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+id);
	
	            Tuple template = new Tuple(String.class);
	
	            long endTime = System.currentTimeMillis() + timeout;
	
	            //Wait for the answers
	            while((endTime - System.currentTimeMillis() > 0) && numberOfPeers > 0) {
	                ackSpace.waitToTake(template,(endTime - System.currentTimeMillis()));
	
	                numberOfPeers--;
	            }
	
	            ackSpace.disconnect();
	
	            TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
	
	            resultSpace.takeAll(template);
	
	            resultSpace.disconnect();
	        }
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
	}

	@Override
	public void rollback(DDBSTransaction transaction)
			throws DDBSToolkitException {
		
		try {
    		
	        //Connection must be established
	        if(isOpen) {
	
	            DDBSCommand command = new DDBSCommand();
	            command.setAction(DDBSAction.ROLLBACK);
	            command.setDDBSTransaction(transaction);
	            command.setDestination(Peer.ALL);
	
	            //Get the number of peers
	            int numberOfPeers = getListPeers().size();
	
	            TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));
	
	            //Space to receive ACK
	            TupleSpace ackSpace = new TupleSpace(ipAddressServer, port, clusterName+"-ack-"+id);
	
	            Tuple template = new Tuple(String.class);
	
	            long endTime = System.currentTimeMillis() + timeout;
	
	            //Wait for the answers
	            while((endTime - System.currentTimeMillis() > 0) && numberOfPeers > 0) {
	                ackSpace.waitToTake(template,(endTime - System.currentTimeMillis()));
	
	                numberOfPeers--;
	            }
	
	            ackSpace.disconnect();
	
	            TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
	        	
	            resultSpace.takeAll(template);
	
	            resultSpace.disconnect();
	        }
    	} catch (TupleSpaceException e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		} catch (Exception e) {
			throw new DDBSToolkitException("Error executing the middleware request", e);
		}
		
	}

	@Override
	public DDBSTransaction executeTransaction(DDBSTransaction transaction)
			throws DDBSToolkitException {
		
		if(transaction.getTransactionCommands() != null && !transaction.getTransactionCommands().isEmpty()) {
			
			Map<String, DDBSTransaction> transactionsPerPeerId = new HashMap<>();
			for(TransactionCommand transactionCommand : transaction.getTransactionCommands()) {
				
				if(transactionCommand.getEntity() instanceof DistributedEntity) {
						
					DistributedEntity entity = (DistributedEntity)transactionCommand.getEntity();
					
					if(!transactionsPerPeerId.containsKey(entity.getPeerUid())) {
						transactionsPerPeerId.put(entity.getPeerUid(), new DDBSTransaction(transaction.getTransactionId()));
					}
					
					transactionsPerPeerId.get(entity.getPeerUid()).getTransactionCommands().add(transactionCommand);
				}
			}
			
			//Connection must be established
            if(isOpen) {

            	for(String peerId :transactionsPerPeerId.keySet()) {
            		
            		try {
            			DDBSCommand command = new DDBSCommand();
                        command.setAction(DDBSAction.TRANSACTION);
                        command.setDestination(new Peer(peerId, ""));
                        command.setDDBSTransaction(transactionsPerPeerId.get(peerId));

                        TupleID id = commandPeers.write(SqlSpacesConverter.getTuple(command, timeout));

                        TupleSpace resultSpace = new TupleSpace(ipAddressServer, port, clusterName+"-results-"+id);
                        Tuple template = new Tuple(String.class);
                        resultSpace.waitToTake(template);

                        resultSpace.disconnect();
                        
                        return transaction;
            		} catch(Exception e) {
            			throw new DDBSToolkitException("Error while executing the transaction", e);
            		}
                    
            	}
            }
		}
		return null;
	}
	
	/**
	 * Test middleware connection
	 * 
	 * @throws DDBSToolkitException
	 */
	private <T extends IEntity> void testConnection(T object)
			throws DDBSToolkitException {
		if (!isOpen()) {
			throw new DDBSToolkitException(
					"The database connection is not opened");
		}
		if (object == null) {
			throw new IllegalArgumentException(
					"The object passed in parameter is null");
		}

	}
}
