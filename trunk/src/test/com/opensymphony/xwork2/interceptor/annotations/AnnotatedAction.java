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
public class AnnotatedAction extends BaseAnnotatedAction {

    @Before
	public String before() {
		log = log + "before";
		return null;
	}
	
	public String execute() {
		log = log + "-execute";
		return Action.SUCCESS;
	}
	
	@BeforeResult
	public void beforeResult() throws Exception {
		log = log +"-beforeResult";
	}
	
	@After
	public void after() {
		log = log + "-after";
	}
}
