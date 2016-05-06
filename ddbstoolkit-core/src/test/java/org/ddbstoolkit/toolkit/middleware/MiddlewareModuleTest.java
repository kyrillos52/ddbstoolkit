package org.ddbstoolkit.toolkit.middleware;

import org.ddbstoolkit.toolkit.core.DataModuleTest;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.core.DistributableSenderInterface;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * JUnit tests for all Middleware Modules
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class MiddlewareModuleTest extends DataModuleTest {
	
	/**
	 * Wait time
	 */
	private final int WAIT_TIME = 1000;
	
	/**
	 * Distributed sender interface
	 */
	protected DistributableSenderInterface senderInterface;
	
	/**
	 * Runnable for the listener
	 */
	private DDBSToolkitListener ddbsToolkitListener;
	
	/**
	 * Distributed receiver interface
	 */
	protected DistributableReceiverInterface receiverInterface;
	
	/**
	 * Receiver peer
	 */
	protected Peer receiverPeer;
	
	/**
	 * Receiver thread
	 */
	protected Thread receiverThread;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	/**
	 * Add receiver peer uid
	 * @param distributedEntity Distributed Entity
	 */
	@Override
	protected void addReceiverPeerUID(IEntity iEntity)
	{
		if(iEntity instanceof DistributedEntity) {
			((DistributedEntity)iEntity).setPeerUid(receiverPeer.getUid());
		}
	}
	
	/**
	 * Instantiate and start a new listener thread
	 * @param receiverInterface receiver interface
	 * @throws Exception
	 */
	@Before
	public void instantiateAndStartDistributableReceiverInterface() throws Exception
	{
		instantiateReceiverAndSenderInterface();
		
		ddbsToolkitListener = new DDBSToolkitListener(receiverInterface);
		
		receiverThread = new Thread(ddbsToolkitListener);
		receiverThread.start();
		
		Thread.sleep(WAIT_TIME);
		
		senderInterface.open();
		
		Assert.assertEquals(senderInterface.getListPeers().size(), 1);
		 
		receiverPeer = senderInterface.getListPeers().get(0);
		
		manager = senderInterface;
	}
	
	public abstract void instantiateReceiverAndSenderInterface() throws Exception;
	
	@Override
	public void instantiateManager() throws Exception {
		instantiateReceiverAndSenderInterface();
	}
	
	@After
	public void closeConnection() throws DDBSToolkitException 
	{
		try {
			ddbsToolkitListener.setKeepListening(false);
			senderInterface.close();
			receiverInterface.stop();
		} catch(Exception e) {
			throw new DDBSToolkitException("Error closing the connexion", e);
		}
		
	}

	@Override
	public void testIsOpen() throws Exception {
		//TODO Nothing
	}

	@Override
	protected String getLikeExpression() {
		return "%2%";
	}
	
	
}
