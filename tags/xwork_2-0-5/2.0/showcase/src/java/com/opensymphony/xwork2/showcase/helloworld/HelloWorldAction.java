/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.showcase.helloworld;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class HelloWorldAction extends ActionSupport {

	private static final long serialVersionUID = 6874543345469426109L;
	
	private static final Log _log = LogFactory.getLog(HelloWorldAction.class);
	
	private String message;
	
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
	@Override
	public String execute() throws Exception {
		
		_log.debug("execute ...");
		
		message = "Hello World";
		
		return SUCCESS;
	}
}
