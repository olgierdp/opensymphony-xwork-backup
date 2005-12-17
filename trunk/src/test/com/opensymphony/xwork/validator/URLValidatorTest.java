/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.TextUtils;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.validators.URLValidator;

import junit.framework.TestCase;

/**
 * Test case for URLValidator
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class URLValidatorTest extends TestCase {

	
	public void testAcceptNullValueForMutualExclusionOfValidators() throws Exception {
		OgnlValueStack stack = new OgnlValueStack();
		ActionContext. getContext().setValueStack(stack);
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl1");
		validator.validate(new MyObject());
		
		assertFalse(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertFalse(validator.getValidatorContext().hasFieldErrors());
	}
	
	public void testInvalidEmptyValue() throws Exception {
		OgnlValueStack stack = new OgnlValueStack();
		ActionContext.getContext().setValueStack(stack);
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl2");
		validator.validate(new MyObject());
		
		assertFalse(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertFalse(validator.getValidatorContext().hasFieldErrors());
	}
	
	public void testInvalidValue() throws Exception {
		OgnlValueStack stack = new OgnlValueStack();
		ActionContext.getContext().setValueStack(stack);
		
		URLValidator validator = new URLValidator();
		validator.setValidatorContext(new GenericValidatorContext(new Object()));
		validator.setFieldName("testingUrl3");
		validator.validate(new MyObject());
		
		assertTrue(validator.getValidatorContext().hasErrors());
		assertFalse(validator.getValidatorContext().hasActionErrors());
		assertFalse(validator.getValidatorContext().hasActionMessages());
		assertTrue(validator.getValidatorContext().hasFieldErrors());
	}
	
	
	
	
	
	class MyObject {
		public String getTestingUrl1() {
			return null;
		}
		
		public String getTestingUrl2() {
			return "";
		}
		
		public String getTestingUrl3() {
			return "sasdasd@asddd";
		}
	}
}
