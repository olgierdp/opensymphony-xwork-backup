/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.VisitorValidatorTestAction;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;


/**
 * VisitorFieldValidatorTest
 * @author Jason Carreira
 * Created Aug 4, 2003 1:26:01 AM
 */
public class VisitorFieldValidatorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected VisitorValidatorTestAction action;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        action = new VisitorValidatorTestAction();

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
    }

    public void testArrayValidation() throws Exception {
        TestBean[] beanArray = action.getTestBeanArray();
        TestBean testBean = beanArray[0];
        testBean.setName("foo");
        validate("validateArray");

        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("testBeanArray.name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List errors = (List) fieldErrors.get("testBeanArray.name");
        assertEquals(4, errors.size());
    }

    public void testBeanMessagesUseBeanResourceBundle() throws Exception {
        validate("beanMessageBundle");
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertTrue(fieldErrors.containsKey("bean.count"));

        List beanCountMessages = (List) fieldErrors.get("bean.count");
        assertEquals(1, beanCountMessages.size());

        String beanCountMessage = (String) beanCountMessages.get(0);
        assertEquals("bean: Count must be between 1 and 100, current value is -1.", beanCountMessage);
    }

    public void testCollectionValidation() throws Exception {
        List testBeanList = action.getTestBeanList();
        TestBean testBean = (TestBean) testBeanList.get(0);
        testBean.setName("foo");
        validate("validateList");

        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("testBeanList.name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List errors = (List) fieldErrors.get("testBeanList.name");
        assertEquals(4, errors.size());
    }

    public void testContextIsOverriddenByContextParamInValidationXML() throws Exception {
        validate("visitorValidationAlias");
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(!fieldErrors.containsKey("bean.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testContextIsPropagated() throws Exception {
        validate("visitorValidation");
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(!fieldErrors.containsKey("bean.count"));
        assertTrue(fieldErrors.containsKey("bean.name"));
        assertTrue(fieldErrors.containsKey("bean.birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }

    private void validate(String context) throws ValidationException {
        ActionContext actionContext = ActionContext.getContext();
        actionContext.setName(context);
        ActionValidatorManager.validate(action, context);
    }
}
