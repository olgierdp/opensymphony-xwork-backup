/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.test.TestBean2;

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


    @Override
    public void setUp() throws Exception {
        super.setUp();
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        action = new VisitorValidatorModelAction();

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

    }

    public void testModelFieldErrorsAddedWithoutFieldPrefix() throws Exception {
        container.getInstance(ActionValidatorManager.class).validate(action, null);
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));

        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List<String> nameMessages = fieldErrors.get("name");
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

        container.getInstance(ActionValidatorManager.class).validate(action, null);
        assertTrue(action.hasFieldErrors());

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));

        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List<String> nameMessages = fieldErrors.get("name");
        assertEquals(1, nameMessages.size());

        String nameMessage = nameMessages.get(0);
        assertEquals("You must enter a name.", nameMessage);

        // should also have picked up validation check for DataAware interface
        assertTrue(fieldErrors.containsKey("data"));

        List<String> dataMessages = fieldErrors.get("data");
        assertEquals(1, dataMessages.size());

        String dataMessage = dataMessages.get(0);
        assertEquals("You must enter a value for data.", dataMessage);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
        Locale.setDefault(origLocale);
    }
}
