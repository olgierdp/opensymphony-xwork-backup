/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.validator.validators.RepopulateConversionErrorFieldValidatorSupport;

import junit.framework.TestCase;

/**
 * Test RepopulateConversionErrorFieldValidatorSupport.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class RepopulateConversionErrorFieldValidatorSupportTest extends TestCase {

	
	InternalRepopulateConversionErrorFieldValidatorSupport validator1;
	ActionSupport action;
	
	public void testGetterSetterGetsCalledApropriately1() throws Exception {
		
		validator1.setRepopulateField("true");
		validator1.validate(action);

		
		ActionContext.getContext().getActionInvocation().invoke();
		
		Object valueFromStack = ActionContext.getContext().getValueStack().findValue("someFieldName", String.class);
		
		assertEquals(valueFromStack, "some value");
	}
	
	
	public void testGetterSetterGetsCalledApropriately2() throws Exception {
		
		validator1.setRepopulateField("false");
		validator1.validate(action);

		
		ActionContext.getContext().getActionInvocation().invoke();
		
		Object valueFromStack = ActionContext.getContext().getValueStack().findValue("someFieldName", String.class);
		
		assertEquals(valueFromStack, null);
	}
	
	
	protected void setUp() throws Exception {
		OgnlValueStack stack = new OgnlValueStack();
		MockActionInvocation invocation = new MockActionInvocation();
		invocation.setStack(stack);
		ActionContext.getContext().setValueStack(stack);
		ActionContext.getContext().setActionInvocation(invocation);
		
		String[] conversionErrorValue = new String[] { "some value" };
		Map conversionErrors = ActionContext.getContext().getConversionErrors();
		conversionErrors.put("someFieldName", conversionErrorValue);
		
		action = new ActionSupport();
		validator1 = 
			new InternalRepopulateConversionErrorFieldValidatorSupport();
		validator1.setFieldName("someFieldName");
		validator1.setValidatorContext(new DelegatingValidatorContext(action));
	}
	
	protected void tearDown() throws Exception {
		validator1 = null;
		action = null;
	}
	
	
	// === inner class ============
	
	class InternalRepopulateConversionErrorFieldValidatorSupport extends RepopulateConversionErrorFieldValidatorSupport {
		public boolean doValidateGetsCalled = false;
		
		protected void doValidate(Object object) throws ValidationException {
			doValidateGetsCalled = true;
		}
	}
}
