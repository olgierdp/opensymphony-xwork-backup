/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.test.SimpleAction2;
import com.opensymphony.xwork.test.SimpleAction3;
import com.opensymphony.xwork.test.User;
import com.opensymphony.xwork.validator.validators.DateRangeFieldValidator;
import com.opensymphony.xwork.validator.validators.ExpressionValidator;
import com.opensymphony.xwork.validator.validators.IntRangeFieldValidator;
import com.opensymphony.xwork.validator.validators.RequiredFieldValidator;
import com.opensymphony.xwork.validator.validators.RequiredStringValidator;

import junit.framework.TestCase;

import java.util.List;


/**
 * ActionValidatorManagerTest
 * @author Jason Carreira
 * Created Jun 9, 2003 11:03:01 AM
 */
public class ActionValidatorManagerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected final String alias = "validationAlias";

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testBuildValidatorKey() {
        String validatorKey = ActionValidatorManager.buildValidatorKey(SimpleAction.class, alias);
        assertEquals(SimpleAction.class.getName() + "/" + alias, validatorKey);
    }

    public void testBuildsValidatorsForAlias() {
        List validatorList = ActionValidatorManager.getValidators(SimpleAction.class, alias);

        // 2 in the class level + 2 in the alias
        assertEquals(7, validatorList.size());

        final FieldValidator bazValidator1 = (FieldValidator) validatorList.get(0);
        assertEquals("baz", bazValidator1.getFieldName());
        assertTrue(bazValidator1 instanceof RequiredFieldValidator);

        final FieldValidator bazValidator2 = (FieldValidator) validatorList.get(1);
        assertEquals("baz", bazValidator2.getFieldName());
        assertTrue(bazValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator barValidator1 = (FieldValidator) validatorList.get(2);
        assertEquals("bar", barValidator1.getFieldName());
        assertTrue(barValidator1 instanceof RequiredFieldValidator);

        final FieldValidator barValidator2 = (FieldValidator) validatorList.get(3);
        assertEquals("bar", barValidator2.getFieldName());
        assertTrue(barValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator dateValidator = (FieldValidator) validatorList.get(4);
        assertEquals("date", dateValidator.getFieldName());
        assertTrue(dateValidator instanceof DateRangeFieldValidator);

        final FieldValidator fooValidator = (FieldValidator) validatorList.get(5);
        assertEquals("foo", fooValidator.getFieldName());
        assertTrue(fooValidator instanceof IntRangeFieldValidator);

        final Validator expressionValidator = (Validator) validatorList.get(6);
        assertTrue(expressionValidator instanceof ExpressionValidator);
    }

    public void testGetValidatorsForInterface() {
        List validatorList = ActionValidatorManager.getValidators(SimpleAction3.class, alias);

        // 5 in the class hierarchy + 1 in the interface
        assertEquals(6, validatorList.size());

        final FieldValidator dataValidator = (FieldValidator) validatorList.get(0);
        assertEquals("data", dataValidator.getFieldName());
        assertTrue(dataValidator instanceof RequiredStringValidator);

        final FieldValidator barValidator1 = (FieldValidator) validatorList.get(1);
        assertEquals("bar", barValidator1.getFieldName());
        assertTrue(barValidator1 instanceof RequiredFieldValidator);

        final FieldValidator barValidator2 = (FieldValidator) validatorList.get(2);
        assertEquals("bar", barValidator2.getFieldName());
        assertTrue(barValidator2 instanceof IntRangeFieldValidator);

        final FieldValidator dateValidator = (FieldValidator) validatorList.get(3);
        assertEquals("date", dateValidator.getFieldName());
        assertTrue(dateValidator instanceof DateRangeFieldValidator);

        final FieldValidator fooValidator = (FieldValidator) validatorList.get(4);
        assertEquals("foo", fooValidator.getFieldName());
        assertTrue(fooValidator instanceof IntRangeFieldValidator);

        final Validator expressionValidator = (Validator) validatorList.get(5);
        assertTrue(expressionValidator instanceof ExpressionValidator);
    }

    public void testSameAliasWithDifferentClass() {
        List validatorList = ActionValidatorManager.getValidators(SimpleAction.class, alias);
        List validatorList2 = ActionValidatorManager.getValidators(SimpleAction2.class, alias);
        assertFalse(validatorList.size() == validatorList2.size());
    }

    public void testShortCircuit() {
        // get validators
        List validatorList = ActionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("mark@mycompany.com");
            user.setEmail2("mark@mycompany.com");

            // this should work
            ValidatorContext context = new GenericValidatorContext(user);
            ActionValidatorManager.validate(user, null, context);
            assertFalse(context.hasErrors());

            // this should short-circuit at the second validator for email
            user.setEmail("bad_email");
            user.setEmail2("bad_email");
            context = new GenericValidatorContext(user);
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
}
