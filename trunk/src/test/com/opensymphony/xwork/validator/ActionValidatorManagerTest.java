/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.test.SimpleAction2;
import com.opensymphony.xwork.test.SimpleAction3;
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

        final Validator expressionValidator = (Validator) validatorList.get(4);
        assertTrue(expressionValidator instanceof ExpressionValidator);

        final FieldValidator bazValidator1 = (FieldValidator) validatorList.get(5);
        assertEquals("baz", bazValidator1.getFieldName());
        assertTrue(bazValidator1 instanceof RequiredFieldValidator);

        final FieldValidator bazValidator2 = (FieldValidator) validatorList.get(6);
        assertEquals("baz", bazValidator2.getFieldName());
        assertTrue(bazValidator2 instanceof IntRangeFieldValidator);
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
}
