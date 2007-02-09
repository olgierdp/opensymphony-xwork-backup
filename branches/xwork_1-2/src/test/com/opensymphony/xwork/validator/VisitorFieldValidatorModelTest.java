/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.test.TestBean2;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.*;


/**
 * VisitorFieldValidatorModelTest
 *
 * @author Jason Carreira
 *         Date: Mar 18, 2004 2:51:42 PM
 */
public class VisitorFieldValidatorModelTest extends XWorkTestCase {

    protected VisitorValidatorModelAction action;
    private Locale origLocale;


    public void setUp() {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        action = new VisitorValidatorModelAction();

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
    }

    public void testModelFieldErrorsAddedWithoutFieldPrefix() throws Exception {
        ActionValidatorManagerFactory.getInstance().validate(action, null);
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();

        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));

        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List nameMessages = (List) fieldErrors.get("name");
        assertEquals(1, nameMessages.size());

        String nameMessage = (String) nameMessages.get(0);
        assertEquals("You must enter a name.", nameMessage);
    }

    public void testModelFieldErrorsAddedWithoutFieldPrefixForInterface() throws Exception {
        TestBean origBean = action.getBean();
        TestBean2 bean = new TestBean2();
        bean.setBirth(origBean.getBirth());
        bean.setCount(origBean.getCount());
        action.setBean(bean);
        assertTrue(action.getBean() instanceof TestBean2);

        ActionValidatorManagerFactory.getInstance().validate(action, null);
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();

        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));

        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List nameMessages = (List) fieldErrors.get("name");
        assertEquals(1, nameMessages.size());

        String nameMessage = (String) nameMessages.get(0);
        assertEquals("You must enter a name.", nameMessage);

        // should also have picked up validation check for DataAware interface
        assertTrue(fieldErrors.containsKey("data"));

        List dataMessages = (List) fieldErrors.get("data");
        assertEquals(1, dataMessages.size());

        String dataMessage = (String) dataMessages.get(0);
        assertEquals("You must enter a value for data.", dataMessage);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
        Locale.setDefault(origLocale);
    }
}
