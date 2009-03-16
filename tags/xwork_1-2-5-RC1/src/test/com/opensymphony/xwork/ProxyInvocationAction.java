/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

/**
 * @version $Date$ $Id$
 */
public class ProxyInvocationAction extends ActionSupport implements ProxyInvocationInterface {
	
	private static final long serialVersionUID = -3336083736424831839L;

	public String show() {
        return "proxyResult";
    }
}
