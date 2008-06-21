/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import junit.framework.TestCase;

import java.util.HashMap;


/**
 * Simple Test ActionContext's ThreadLocal
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ActionContextThreadLocalTest extends TestCase {

	
	public void testGetContext() throws Exception {
	    ActionContext.setContext(null);
		assertNull(ActionContext.getContext());
	}
	
	public void testSetContext() throws Exception {
		ActionContext context = new ActionContext(new HashMap<String, Object>());
		ActionContext.setContext(context);
		assertEquals(context, ActionContext.getContext());
	}
}
