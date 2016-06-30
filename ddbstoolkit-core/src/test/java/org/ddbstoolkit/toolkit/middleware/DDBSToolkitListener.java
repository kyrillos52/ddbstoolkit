package org.ddbstoolkit.toolkit.middleware;

import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;

/**
 * DDBSToolkit test listener interface
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DDBSToolkitListener implements Runnable {

	/**
	 * Distributed receiver interface
	 */
	private final DistributableReceiverInterface receiverInterface;
	
	/**
	 * Indicates if we are continuing to listen
	 */
	private boolean keepListening = true;
	
	/**
	 * Wait time
	 */
	private static final int WAIT_TIME = 100;

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
			e.printStackTrace();
		}
	}
}
