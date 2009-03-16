/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.HashMap;


/**
 * Simple Test ActionContext's ThreadLocal
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ActionContextThreadLocalTest extends XWorkTestCase {

	
	public void testGetContext() throws Exception {
		assertNotNull(ActionContext.getContext());
	}
	
	public void testSetContext() throws Exception {
		ActionContext context = new ActionContext(new HashMap());
		ActionContext.setContext(context);
		assertEquals(context, ActionContext.getContext());
	}
}
