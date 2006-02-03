/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.annotations;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class BaseAnnotatedAction {

	protected String log = "";
	
	@Before
	public String baseBefore() {
		log = log + "baseBefore-";
		return null;
	}

}
