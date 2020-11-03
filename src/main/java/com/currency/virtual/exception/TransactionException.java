package com.currency.virtual.exception;

/**
 * @author lingting 2020-09-02 14:12
 */
public class TransactionException extends Exception {

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

}
