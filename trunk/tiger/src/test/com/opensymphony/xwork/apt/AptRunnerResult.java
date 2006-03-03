/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.apt;

/**
 * @author Rainer Hermanns
 * @author Nils Hartmann
 */
public class AptRunnerResult {
	
	private final String _command;
	private final int _result;
	private final String _output;
	private final String _error;
	
	public String getCommand() {
		return _command;
	}
	public String getError() {
		return _error;
	}
	
	public boolean containsErrors() {
        return (_error != null) && (_error.length()>0);
	}
	
	public String getOutput() {
		return _output;
	}
	public int getResult() {
		return _result;
	}
	
	public boolean succeeded() {
		return (_result == 0);
	}
	
	public AptRunnerResult(int result, String command, String output, String error) {
		super();
		this._result = result;
		_command = command;
		_output = output;
		_error = error;
	}
	
	public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("[AptRunnerResult:");
			buffer.append(" _command: ");
			buffer.append(_command);
			buffer.append(" _result: ");
			buffer.append(_result);
			buffer.append(" _output: ");
			buffer.append(_output);
			buffer.append(" _error: ");
			buffer.append(_error);
			buffer.append("]");
			return buffer.toString();
		}
	
	
	
	
	
	

}
