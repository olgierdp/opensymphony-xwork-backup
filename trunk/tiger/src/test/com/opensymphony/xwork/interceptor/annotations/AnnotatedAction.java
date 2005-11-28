package com.opensymphony.xwork.interceptor.annotations;

import com.opensymphony.xwork.Action;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
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
