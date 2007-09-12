/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.showcase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;


/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class PrintToConsoleResult implements Result {

	private static final Log _log = LogFactory.getLog(PrintToConsoleResult.class);
	
	private static final long serialVersionUID = -6173326554804520601L;
	
	private String param = "whatsoever";
	
	public void setParam(String param) { this.param = param; }
	public String getParam() { return this.param; }
	
	public void execute(ActionInvocation invocation) throws Exception {
		
		_log.debug("execute ...");
		
		String result = TextParseUtil.translateVariables(param, invocation.getStack());
		
		System.out.println(result);
	}
}
