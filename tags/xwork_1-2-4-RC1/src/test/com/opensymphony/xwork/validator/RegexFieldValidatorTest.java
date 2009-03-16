/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
*/
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.validators.RegexFieldValidator;

import java.util.List;

/**
 * Unit test for RegexFieldValidator.
 * <p/>
 * This unit test is only to test that the regex field validator works, not to
 * unit test the build in reg.exp from JDK. That is why the expressions are so simple.
 *
 * @author Claus Ibsen
 */
public class RegexFieldValidatorTest extends XWorkTestCase {

    public void testMatch() throws Exception {
        MyTestPerson testPerson = new MyTestPerson();
        testPerson.setUsername("Secret");

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.getContext().setValueStack(stack);

        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setExpression("^Sec.*");
        validator.setValidatorContext(new GenericValidatorContext(new Object()));
        validator.setFieldName("username");
        validator.validate(testPerson);

        assertFalse(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertFalse(validator.getValidatorContext().hasFieldErrors());
    }

    public void testFail() throws Exception {
        MyTestPerson testPerson = new MyTestPerson();
        testPerson.setUsername("Superman");

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.getContext().setValueStack(stack);

        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setExpression("^Sec.*");
        validator.setValidatorContext(new GenericValidatorContext(new Object()));
        validator.setFieldName("username");
        validator.validate(testPerson);

        assertTrue(validator.getValidatorContext().hasErrors());
        assertTrue(validator.getValidatorContext().hasFieldErrors());
        List msgs = (List) validator.getValidatorContext().getFieldErrors().get("username");
        assertNotNull(msgs);
        assertTrue(msgs.size() == 1); // should contain 1 error message

        // when failing the validator will not add action errors/msg
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
    }

    public void testNoFieldName() throws Exception {
        MyTestPerson testPerson = new MyTestPerson();
        testPerson.setUsername("NoExpression");

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.getContext().setValueStack(stack);

        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setExpression("^Sec.*");
        validator.setValidatorContext(new GenericValidatorContext(new Object()));
        validator.setFieldName(null);
        validator.validate(testPerson);

        assertFalse(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertFalse(validator.getValidatorContext().hasFieldErrors());
    }

    public void testGetExpression() throws Exception {
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setExpression("^Hello.*");
        assertEquals("^Hello.*", validator.getExpression());
    }

    private class MyTestPerson {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
