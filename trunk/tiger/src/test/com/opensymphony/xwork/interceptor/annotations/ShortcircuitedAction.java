package com.opensymphony.xwork.interceptor.annotations;

import com.opensymphony.xwork.Action;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
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
