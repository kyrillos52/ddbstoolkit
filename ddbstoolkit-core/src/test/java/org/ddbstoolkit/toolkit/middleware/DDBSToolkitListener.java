package org.ddbstoolkit.toolkit.middleware;

import org.apache.log4j.Logger;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;

/**
 * DDBSToolkit test listener interface
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DDBSToolkitListener implements Runnable {
	
	/**
	 * DDBSToolkitListener logger
	 */
	private static final Logger logger = Logger.getLogger(DDBSToolkitListener.class);
	
	/**
	 * Wait time
	 */
	private static final int WAIT_TIME = 100;

	/**
	 * Distributed receiver interface
	 */
	private final DistributableReceiverInterface receiverInterface;
	
	/**
	 * Indicates if we are continuing to listen
	 */
	private boolean keepListening = true;

	/**
	 * DDBSToolkit lister constructor
	 * @param receiverInterface Receiver interface
	 */
	public DDBSToolkitListener(
			DistributableReceiverInterface receiverInterface) {
		super();
		this.receiverInterface = receiverInterface;
	}

	/**
	 * Indicate if we continue to listen on this interface
	 * @param keepListening Boolean which indicates if we are still continuing to listen on this interface
	 */
	public void setKeepListening(boolean keepListening) {
		this.keepListening = keepListening;
	}

	@Override
	public void run() {
		
		try {
			receiverInterface.start();
			
			while(keepListening) {
				Thread.sleep(WAIT_TIME);
			}
			
		} catch (Exception e) {
			logger.error("Error while trying to execute the receiver interface",e);
		}
	}
}
