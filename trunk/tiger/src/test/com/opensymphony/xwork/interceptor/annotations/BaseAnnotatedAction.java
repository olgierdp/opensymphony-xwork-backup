package com.opensymphony.xwork.interceptor.annotations;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 */
public class BaseAnnotatedAction {

	protected String log = "";
	
	@Before
	public String baseBefore() {
		log = log + "baseBefore-";
		return null;
	}

}
