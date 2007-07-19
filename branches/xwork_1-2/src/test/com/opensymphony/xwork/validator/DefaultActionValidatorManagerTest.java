/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.ValidationOrderAction;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.XworkException;
import com.opensymphony.xwork.test.DataAware2;
import com.opensymphony.xwork.test.SimpleAction2;
import com.opensymphony.xwork.test.SimpleAction3;
import com.opensymphony.xwork.test.User;
import com.opensymphony.xwork.validator.validators.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * DefaultActionValidatorManagerTest
 *
 * @author Jason Carreira
 * @author tm_jee 
 * @version $Date$ $Id$
 */
public class DefaultActionValidatorManagerTest extends XWorkTestCase {

    protected final String alias = "validationAlias";

    ActionValidatorManager actionValidatorManager;

    protected void setUp() throws Exception {
        actionValidatorManager = new DefaultActionValidatorManager();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        actionValidatorManager = null;
        super.tearDown();
    }


    public void testBuildValidatorKey() {
        String validatorKey = new DefaultActionValidatorManager().buildValidatorKey(SimpleAction.class, alias);
        assertEquals(SimpleAction.class.getName() + "/" + alias, validatorKey);
    }

    public void testBuildsValidatorsForAlias() {
        List validatorList = actionValidatorManager.getValidators(SimpleAction.class, alias);

        // 6 in the class level + 2 in the alias
        assertEquals(9, validatorList.size());
    }

    public void testBuildsValidatorsForAliasError() {
        boolean pass = false;
        try {
            List validatorList = actionValidatorManager.getValidators(TestBean.class, "badtest");
        } catch (XworkException ex) {
            pass = true;
        }
        assertTrue("Didn't throw exception on load failure", pass);
    }


    public void testDefaultMessageInterpolation() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(TestBean.class, "beanMessageBundle");
        assertEquals(3, validatorList.size());

        try {
            TestBean bean = new TestBean();
            bean.setName("foo");
            bean.setCount(99);

            ValidatorContext context = new GenericValidatorContext(bean);
            actionValidatorManager.validate(bean, "beanMessageBundle", context);
            assertTrue(context.hasErrors());
            assertTrue(context.hasFieldErrors());

            List l = (List) context.getFieldErrors().get("count");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Smaller Invalid Count: 99", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testGetValidatorsForInterface() {
        List validatorList = actionValidatorManager.getValidators(DataAware2.class, alias);

        // 3 in interface hierarchy, 2 from parent interface (1 default + 1 context)
        assertEquals(3, validatorList.size());

        final FieldValidator dataValidator1 = (FieldValidator) validatorList.get(0);
        assertEquals("data", dataValidator1.getFieldName());
        assertTrue(dataValidator1 instanceof RequiredFieldValidator);

        final FieldValidator dataValidator2 = (FieldValidator) validatorList.get(1);
        assertEquals("data", dataValidator2.getFieldName());
        assertTrue(dataValidator2 instanceof RequiredStringValidator);

        final FieldValidator blingValidator = (FieldValidator) validatorList.get(2);
        assertEquals("bling", blingValidator.getFieldName());
        assertTrue(blingValidator instanceof RequiredStringValidator);
    }

    public void testGetValidatorsFromInterface() {
        List validatorList = actionValidatorManager.getValidators(SimpleAction3.class, alias);

       
        
        
        // 9 in the class hierarchy + 1 in the interface + 1 in interface alias
        assertEquals(11, validatorList.size());

        // action-level validator comes first
        final Validator expressionValidator = (Validator) validatorList.get(0);
        assertTrue(expressionValidator instanceof ExpressionValidator);
        
        final FieldValidator barValidator1 = (FieldValidator) validatorList.get(1);
        assertEquals("bar", barValidator1.getFieldName());
        assertTrue(barValidator1 instanceof RequiredFieldValidator);

        final FieldValidator barValidator2 = (FieldValidator) validatorList.get(2);
        assertEquals("bar", barValidator2.getFieldName());
        assertTrue(barValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator doubleValidator = (FieldValidator) validatorList.get(3);
        assertEquals("percentage", doubleValidator.getFieldName());
        assertTrue(doubleValidator instanceof DoubleRangeFieldValidator);

        final FieldValidator dateValidator = (FieldValidator) validatorList.get(4);
        assertEquals("date", dateValidator.getFieldName());
        assertTrue(dateValidator instanceof DateRangeFieldValidator);

        final FieldValidator fooValidator = (FieldValidator) validatorList.get(5);
        assertEquals("foo", fooValidator.getFieldName());
        assertTrue(fooValidator instanceof IntRangeFieldValidator);

        final FieldValidator bazValidator = (FieldValidator) validatorList.get(6);
        assertEquals("baz", bazValidator.getFieldName());
        assertTrue(bazValidator instanceof IntRangeFieldValidator);

        final FieldValidator bazValidator1 = (FieldValidator) validatorList.get(7);
        assertEquals("baz", bazValidator1.getFieldName());
        assertTrue(bazValidator1 instanceof RequiredFieldValidator);

        final FieldValidator bazValidator2 = (FieldValidator) validatorList.get(8);
        assertEquals("baz", bazValidator2.getFieldName());
        assertTrue(bazValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator dataValidator1 = (FieldValidator) validatorList.get(9);
        assertEquals("data", dataValidator1.getFieldName());
        assertTrue(dataValidator1 instanceof RequiredFieldValidator);

        final FieldValidator dataValidator2 = (FieldValidator) validatorList.get(10);
        assertEquals("data", dataValidator2.getFieldName());
        assertTrue(dataValidator2 instanceof RequiredStringValidator);
    }

    public void testMessageInterpolation() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(TestBean.class, "beanMessageBundle");
        assertEquals(3, validatorList.size());

        try {
            TestBean bean = new TestBean();
            bean.setName("foo");
            bean.setCount(150);

            ValidatorContext context = new GenericValidatorContext(bean);
            actionValidatorManager.validate(bean, "beanMessageBundle", context);
            assertTrue(context.hasErrors());
            assertTrue(context.hasFieldErrors());

            List l = (List) context.getFieldErrors().get("count");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Count must be between 1 and 100, current value is 150.", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testSameAliasWithDifferentClass() {
        List validatorList = actionValidatorManager.getValidators(SimpleAction.class, alias);
        List validatorList2 = actionValidatorManager.getValidators(SimpleAction2.class, alias);
        assertFalse(validatorList.size() == validatorList2.size());
    }

    public void testSkipUserMarkerActionLevelShortCircuit() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("bad_email");
            user.setEmail2("bad_email");

            ValidatorContext context = new GenericValidatorContext(user);
            actionValidatorManager.validate(user, (String) null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            List l = (List) context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Not a valid e-mail.", l.get(0));
            l = (List) context.getFieldErrors().get("email2");
            assertNotNull(l);
            assertEquals(2, l.size());
            assertEquals("Not a valid e-mail2.", l.get(0));
            assertEquals("Email2 not from the right company.", l.get(1));

            // check action errors
            assertTrue(context.hasActionErrors());
            l = (List) context.getActionErrors();
            assertNotNull(l);
            assertEquals(2, l.size()); // both expression test failed see User-validation.xml
            assertEquals("Email does not start with mark", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testSkipAllActionLevelShortCircuit2() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            // * mark both email to starts with mark to get pass the action-level validator,
            // so we could concentrate on testing the field-level validators (User-validation.xml)
            // * make both email the same to pass the action-level validator at 
            // UserMarker-validation.xml
            user.setEmail("mark_bad_email_for_field_val@foo.com");
            user.setEmail2("mark_bad_email_for_field_val@foo.com");

            ValidatorContext context = new GenericValidatorContext(user);
            actionValidatorManager.validate(user, (String) null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            // we have an error in this field level, email does not ends with mycompany.com
            List l = (List) context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size()); // because email-field-val is short-circuit
            assertEquals("Email not from the right company.", l.get(0));

            
            // check action errors
            l = (List) context.getActionErrors();
            assertFalse(context.hasActionErrors());
            assertEquals(0, l.size());
            
            
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    
    public void testActionLevelShortCircuit() throws Exception {
    	
    	List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());
        
        User user = new User();
        // all fields will trigger error, but sc of action-level, cause it to not appear
        user.setName(null);		
        user.setEmail("tmjee(at)yahoo.co.uk");
        user.setEmail("tm_jee(at)yahoo.co.uk");
        
        ValidatorContext context = new GenericValidatorContext(user);
        actionValidatorManager.validate(user, (String) null, context);
    	
    	// check field level errors
        // shouldn't have any because action error prevents validation of anything else
        List l = (List) context.getFieldErrors().get("email2");
        assertNull(l);
    	
    	
        // check action errors
        assertTrue(context.hasActionErrors());
        l = (List) context.getActionErrors();
        assertNotNull(l);
        // we only get one, because UserMarker-validation.xml action-level validator
        // already sc it   :-)
        assertEquals(1, l.size()); 
        assertEquals("Email not the same as email2", l.get(0));
    }
    
    
    public void testShortCircuitNoErrors() {
        // get validators
        List validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("mark@mycompany.com");
            user.setEmail2("mark@mycompany.com");

            ValidatorContext context = new GenericValidatorContext(user);
            actionValidatorManager.validate(user, (String) null, context);
            assertFalse(context.hasErrors());
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }
    
    public void testFieldErrorsOrder() throws Exception {
    	ValidationOrderAction action = new ValidationOrderAction();
    	actionValidatorManager.validate(action, "actionContext");
    	Map fieldErrors = action.getFieldErrors();
    	Iterator i = fieldErrors.entrySet().iterator();
    	
    	assertNotNull(fieldErrors);
    	assertEquals(fieldErrors.size(), 12);
    	
    	
    	Map.Entry e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "username");
    	assertEquals(((List)e.getValue()).get(0), "username required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "password");
    	assertEquals(((List)e.getValue()).get(0), "password required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "confirmPassword");
    	assertEquals(((List)e.getValue()).get(0), "confirm password required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "firstName");
    	assertEquals(((List)e.getValue()).get(0), "first name required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "lastName");
    	assertEquals(((List)e.getValue()).get(0), "last name required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "city");
    	assertEquals(((List)e.getValue()).get(0), "city is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "province");
    	assertEquals(((List)e.getValue()).get(0), "province is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "country");
    	assertEquals(((List)e.getValue()).get(0), "country is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "postalCode");
    	assertEquals(((List)e.getValue()).get(0), "postal code is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "email");
    	assertEquals(((List)e.getValue()).get(0), "email is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "website");
    	assertEquals(((List)e.getValue()).get(0), "website is required");
    	
    	e = (Map.Entry) i.next();
    	assertEquals(e.getKey(), "passwordHint");
    	assertEquals(((List)e.getValue()).get(0), "password hint is required");
    	
    }
}
