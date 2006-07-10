/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.Action;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class ShortcircuitedAction extends BaseAnnotatedAction {	
	@Before
	public String before() {
		log = log + "before";
		return "shortcircuit";
	}
	
	public String execute() {
		log = log + "-execute-";
		return Action.SUCCESS;
	}
}
