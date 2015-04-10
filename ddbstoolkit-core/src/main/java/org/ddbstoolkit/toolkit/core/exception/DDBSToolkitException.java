package org.ddbstoolkit.toolkit.core.exception;

/**
 * DDBSToolkit generic exception
 * @author Cyril Grandjean
 * @version 1.0: Class creation
 */
public class DDBSToolkitException extends Exception {

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = -5076241885943951698L;

	public DDBSToolkitException(String message)
	{
		super(message);
	}
	
	public DDBSToolkitException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
