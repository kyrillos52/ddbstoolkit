package org.ddbstoolkit.toolkit.middleware;

import java.util.List;

import org.ddbstoolkit.toolkit.core.DataModuleTest;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.core.DistributableSenderInterface;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * JUnit tests for all Middleware Modules
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public abstract class MiddlewareModuleTest extends DataModuleTest {
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	/**
	 * Receiver peer
	 */
	protected Peer receiverPeer;
	
	/**
	 * Receiver thread
	 */
	protected Thread receiverThread;
	
	/**
	 * Distributed sender interface
	 */
	protected DistributableSenderInterface senderInterface;
	
	/**
	 * Distributed receiver interface
	 */
	protected DistributableReceiverInterface receiverInterface;
	
	/**
	 * Wait time
	 */
	private static final int WAIT_TIME = 1000;
	
	/**
	 * Runnable for the listener
	 */
	private DDBSToolkitListener ddbsToolkitListener;
	
	/**
	 * Add receiver peer uid
	 * @param iEntity Entity
	 * @throws DDBSToolkitException Toolkit exception
	 */
	@Override
	protected void addReceiverPeerUID(IEntity iEntity) throws DDBSToolkitException {
		if(iEntity instanceof DistributedEntity) {
			if(receiverPeer == null) {
				List<Peer> peers;
				try {
					peers = senderInterface.getListPeers();
					receiverPeer = peers.get(0);
				} catch (Exception e) {
					throw new DDBSToolkitException("error",e);
				}
				
			}
			((DistributedEntity)iEntity).setPeerUid(receiverPeer.getUid());
		}
	}
	
	
	
	/**
	 * Instantiate and start a new listener thread
	 * @throws Exception throw an error
	 */
	public void instantiateAndStartDistributableReceiverInterface() throws Exception
	{
		instantiateReceiverAndSenderInterface();
		
		ddbsToolkitListener = new DDBSToolkitListener(receiverInterface);
		
		receiverThread = new Thread(ddbsToolkitListener);
		receiverThread.start();
		
		Thread.sleep(WAIT_TIME);
		
		senderInterface.open();
		
		List<Peer> peers = senderInterface.getListPeers();
		
		Assert.assertEquals(peers.size(), 1);
		
		ActorBase actor = createActor();
		actor.setPeerUid(Peer.ALL.getUid());
		
		FilmBase film = createFilm();
		film.setPeerUid(Peer.ALL.getUid());
		
		senderInterface.createEntity(actor);
		senderInterface.createEntity(film);
		
		manager = senderInterface;
		
		receiverPeer = peers.get(0);
	}
	
	public abstract void instantiateReceiverAndSenderInterface() throws Exception;
	
	@Override
	public void instantiateManager() throws Exception {
		instantiateAndStartDistributableReceiverInterface();
	}
	
	@After
	public void closeConnection() throws DDBSToolkitException 
	{
		try {
			ddbsToolkitListener.setKeepListening(false);
			receiverInterface.stop();
			senderInterface.close();
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
