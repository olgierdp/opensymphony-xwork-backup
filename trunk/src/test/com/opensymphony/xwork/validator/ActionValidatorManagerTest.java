/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.test.DataAware2;
import com.opensymphony.xwork.test.SimpleAction2;
import com.opensymphony.xwork.test.SimpleAction3;
import com.opensymphony.xwork.test.User;
import com.opensymphony.xwork.validator.validators.*;

import java.util.List;


/**
 * ActionValidatorManagerTest
 *
 * @author Jason Carreira
 *         Created Jun 9, 2003 11:03:01 AM
 */
public class ActionValidatorManagerTest extends XWorkTestCase {

    protected final String alias = "validationAlias";


    public void testBuildValidatorKey() {
        String validatorKey = ActionValidatorManager.buildValidatorKey(SimpleAction.class, alias);
        assertEquals(SimpleAction.class.getName() + "/" + alias, validatorKey);
    }

    public void testBuildsValidatorsForAlias() {
        List validatorList = ActionValidatorManager.getValidators(SimpleAction.class, alias);

        // 6 in the class level + 2 in the alias
        assertEquals(8, validatorList.size());
    }

    public void testDefaultMessageInterpolation() {
        // get validators
        List validatorList = ActionValidatorManager.getValidators(TestBean.class, "beanMessageBundle");
        assertEquals(3, validatorList.size());

        try {
            TestBean bean = new TestBean();
            bean.setName("foo");
            bean.setCount(99);

            ValidatorContext context = new GenericValidatorContext(bean);
            ActionValidatorManager.validate(bean, "beanMessageBundle", context);
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
        List validatorList = ActionValidatorManager.getValidators(DataAware2.class, alias);

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
        List validatorList = ActionValidatorManager.getValidators(SimpleAction3.class, alias);

        // 8 in the class hierarchy + 1 in the interface + 1 in interface alias
        assertEquals(10, validatorList.size());

        final FieldValidator barValidator1 = (FieldValidator) validatorList.get(0);
        assertEquals("bar", barValidator1.getFieldName());
        assertTrue(barValidator1 instanceof RequiredFieldValidator);

        final FieldValidator barValidator2 = (FieldValidator) validatorList.get(1);
        assertEquals("bar", barValidator2.getFieldName());
        assertTrue(barValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator dateValidator = (FieldValidator) validatorList.get(2);
        assertEquals("date", dateValidator.getFieldName());
        assertTrue(dateValidator instanceof DateRangeFieldValidator);

        final FieldValidator fooValidator = (FieldValidator) validatorList.get(3);
        assertEquals("foo", fooValidator.getFieldName());
        assertTrue(fooValidator instanceof IntRangeFieldValidator);

        final FieldValidator bazValidator = (FieldValidator) validatorList.get(4);
        assertEquals("baz", bazValidator.getFieldName());
        assertTrue(bazValidator instanceof IntRangeFieldValidator);

        final Validator expressionValidator = (Validator) validatorList.get(5);
        assertTrue(expressionValidator instanceof ExpressionValidator);

        final FieldValidator bazValidator1 = (FieldValidator) validatorList.get(6);
        assertEquals("baz", bazValidator1.getFieldName());
        assertTrue(bazValidator1 instanceof RequiredFieldValidator);

        final FieldValidator bazValidator2 = (FieldValidator) validatorList.get(7);
        assertEquals("baz", bazValidator2.getFieldName());
        assertTrue(bazValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator dataValidator1 = (FieldValidator) validatorList.get(8);
        assertEquals("data", dataValidator1.getFieldName());
        assertTrue(dataValidator1 instanceof RequiredFieldValidator);

        final FieldValidator dataValidator2 = (FieldValidator) validatorList.get(9);
        assertEquals("data", dataValidator2.getFieldName());
        assertTrue(dataValidator2 instanceof RequiredStringValidator);
    }

    public void testMessageInterpolation() {
        // get validators
        List validatorList = ActionValidatorManager.getValidators(TestBean.class, "beanMessageBundle");
        assertEquals(3, validatorList.size());

        try {
            TestBean bean = new TestBean();
            bean.setName("foo");
            bean.setCount(150);

            ValidatorContext context = new GenericValidatorContext(bean);
            ActionValidatorManager.validate(bean, "beanMessageBundle", context);
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
        List validatorList = ActionValidatorManager.getValidators(SimpleAction.class, alias);
        List validatorList2 = ActionValidatorManager.getValidators(SimpleAction2.class, alias);
        assertFalse(validatorList.size() == validatorList2.size());
    }

    public void testShortCircuit1() {
        // get validators
        List validatorList = ActionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("bad_email");
            user.setEmail2("bad_email");

            ValidatorContext context = new GenericValidatorContext(user);
            ActionValidatorManager.validate(user, null, context);
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
            assertEquals(1, l.size());
            assertEquals("Email does not start with mark", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testShortCircuit2() {
        // get validators
        List validatorList = ActionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("bad_email@foo.com");
            user.setEmail2(null);

            ValidatorContext context = new GenericValidatorContext(user);
            ActionValidatorManager.validate(user, null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            List l = (List) context.getFieldErrors().get("email");

            // shouldn't have any because action error prevents validation of anything else
            assertNull(l);
            l = (List) context.getFieldErrors().get("email2");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("You must enter a value for email2.", l.get(0));

            // check action errors
            assertTrue(context.hasActionErrors());
            l = (List) context.getActionErrors();
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Email not the same as email2", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testShortCircuitNoErrors() {
        // get validators
        List validatorList = ActionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("mark@mycompany.com");
            user.setEmail2("mark@mycompany.com");

            ValidatorContext context = new GenericValidatorContext(user);
            ActionValidatorManager.validate(user, null, context);
            assertFalse(context.hasErrors());
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }
}
