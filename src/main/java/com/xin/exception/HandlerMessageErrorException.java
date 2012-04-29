package com.xin.exception;

/**
 * 职责链Handler处理过程中不能处理的情况下抛出的异常,封装在msg的content
 * @author micro
 *
 */
public class HandlerMessageErrorException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2266659042089345722L;

	public HandlerMessageErrorException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HandlerMessageErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public HandlerMessageErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public HandlerMessageErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
