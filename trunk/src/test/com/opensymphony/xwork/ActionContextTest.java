/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * ActionContextTest
 *
 * @author Jason Carreira
 *         Created Feb 26, 2003 11:22:50 PM
 */
public class ActionContextTest extends TestCase {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String APPLICATION_KEY = "com.opensymphony.xwork.ActionContextTest.application";
    private static final String SESSION_KEY = "com.opensymphony.xwork.ActionContextTest.session";
    private static final String PARAMETERS_KEY = "com.opensymphony.xwork.ActionContextTest.params";
    private static final String ACTION_NAME = "com.opensymphony.xwork.ActionContextTest.actionName";

    //~ Instance fields ////////////////////////////////////////////////////////

    ActionContext context;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        OgnlValueStack valueStack = new OgnlValueStack();
        Map extraContext = valueStack.getContext();
        Map application = new HashMap();
        application.put(APPLICATION_KEY, APPLICATION_KEY);

        Map session = new HashMap();
        session.put(SESSION_KEY, SESSION_KEY);

        Map params = new HashMap();
        params.put(PARAMETERS_KEY, PARAMETERS_KEY);
        extraContext.put(ActionContext.APPLICATION, application);
        extraContext.put(ActionContext.SESSION, session);
        extraContext.put(ActionContext.PARAMETERS, params);
        extraContext.put(ActionContext.ACTION_NAME, ACTION_NAME);
        context = new ActionContext(extraContext);
        ActionContext.setContext(context);
    }

    public void tearDown() {
        ActionContext.setContext(null);
    }

    public void testContextParams() {
        assertTrue(ActionContext.getContext().getApplication().containsKey(APPLICATION_KEY));
        assertTrue(ActionContext.getContext().getSession().containsKey(SESSION_KEY));
        assertTrue(ActionContext.getContext().getParameters().containsKey(PARAMETERS_KEY));
        assertEquals(ActionContext.getContext().getName(), ACTION_NAME);
    }

    public void testGetContext() {
        ActionContext threadContext = ActionContext.getContext();
        assertEquals(context, threadContext);
    }

    public void testNewActionContextCanFindDefaultTexts() {
        OgnlValueStack valueStack = context.getValueStack();
        String actionErrorMessage = (String) valueStack.findValue("getText('xwork.error.action.execution')");
        assertNotNull(actionErrorMessage);
        assertEquals("Error during Action invocation", actionErrorMessage);
    }
}
