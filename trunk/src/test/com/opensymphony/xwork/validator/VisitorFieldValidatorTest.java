/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.VisitorValidatorTestAction;

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
    }

    public void testArrayValidation() throws Exception {
        TestBean[] beanArray = action.getTestBeanArray();
        TestBean testBean = beanArray[0];
        testBean.setName("foo");
        ActionValidatorManager.validate(action, "validateArray");

        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List errors = (List) fieldErrors.get("name");
        assertEquals(4, errors.size());
    }

    public void testCollectionValidation() throws Exception {
        List testBeanList = action.getTestBeanList();
        TestBean testBean = (TestBean) testBeanList.get(0);
        testBean.setName("foo");
        ActionValidatorManager.validate(action, "validateList");

        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("name"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));

        List errors = (List) fieldErrors.get("name");
        assertEquals(4, errors.size());
    }

    public void testContextIsOverriddenByContextParamInValidationXML() throws Exception {
        ActionValidatorManager.validate(action, "visitorValidationAlias");
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("count"));
        assertTrue(fieldErrors.containsKey("name"));
        assertTrue(!fieldErrors.containsKey("birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }

    public void testContextIsPropagated() throws Exception {
        ActionContext.getContext().setName("visitorValidation");
        ActionValidatorManager.validate(action, "visitorValidation");
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        assertEquals(3, fieldErrors.size());
        assertTrue(!fieldErrors.containsKey("count"));
        assertTrue(fieldErrors.containsKey("name"));
        assertTrue(fieldErrors.containsKey("birth"));

        //the error from the action should be there too
        assertTrue(fieldErrors.containsKey("context"));
    }
}
