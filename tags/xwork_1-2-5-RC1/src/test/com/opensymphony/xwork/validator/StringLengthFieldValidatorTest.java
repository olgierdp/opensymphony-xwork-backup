/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.validator.validators.StringLengthFieldValidator;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class StringLengthFieldValidatorTest extends XWorkTestCase {

	protected InternalActionSupport action;
	protected StringLengthFieldValidator validator;
	
	public void testStringLengthEmptyNoTrim1() throws Exception {
		action.setMyField("");
		
		validator.setTrim(false);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "");
		assertFalse(action.hasFieldErrors());
	}
	
	public void testStringLengthNullNoTrim() throws Exception {
		action.setMyField(null);

		validator.setTrim(false);
		validator.validate(action);
		
		assertEquals(action.getMyField(), null);
		assertFalse(action.hasFieldErrors());
	}
	
	public void testStringLengthEmptyTrim1() throws Exception {
		action.setMyField("   ");
		
		validator.setTrim(true);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "   ");
		assertFalse(action.hasFieldErrors());
	}
	
	public void testStringLengthEmptyNoTrim2() throws Exception {
		action.setMyField("          ");
		
		validator.setTrim(false);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "          ");
		assertTrue(action.hasFieldErrors());
	}
	
	
	public void testStringLengthNullTrim() throws Exception {
		action.setMyField(null);

		validator.setTrim(true);
		validator.validate(action);
		
		assertEquals(action.getMyField(), null);
		assertFalse(action.hasFieldErrors());
	}
	
	public void testInvalidStringLengthNoTrim() throws Exception {
		action.setMyField("abcdefghijklmn");
		
		validator.setTrim(false);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "abcdefghijklmn");
		assertTrue(action.hasFieldErrors());
	}
	
	public void testInvalidStringLengthTrim() throws Exception {
		action.setMyField("abcdefghijklmn   ");
		
		validator.setTrim(true);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "abcdefghijklmn   ");
		assertTrue(action.hasFieldErrors());
	}
	
	public void testValidStringLengthNoTrim() throws Exception {
		action.setMyField("   ");
		
		validator.setTrim(false);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "   ");
		assertFalse(action.hasFieldErrors());
	}
	
	public void testValidStringLengthTrim() throws Exception {
		action.setMyField("asd          ");
		
		validator.setTrim(true);
		validator.validate(action);
		
		assertEquals(action.getMyField(), "asd          ");
		assertFalse(action.hasFieldErrors());
	}
	
	
	protected void setUp() throws Exception {
		super.setUp();
		action = new InternalActionSupport();
		validator = new StringLengthFieldValidator();
		validator.setFieldName("myField");
		validator.setMessageKey("error");
		validator.setValidatorContext(new DelegatingValidatorContext(action));
		validator.setMaxLength(5);
		validator.setMinLength(2);
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
		action = null;
		validator = null;
	}
	
	public static class InternalActionSupport extends ActionSupport {
		
		private static final long serialVersionUID = 1L;

		private String myField;
		public String getMyField() { return this.myField; }
		public void setMyField(String myField) { this.myField = myField; }
	}
}
