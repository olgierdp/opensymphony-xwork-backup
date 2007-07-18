/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for {@link ActionContext}.
 *
 * @author Jason Carreira
 */
public class ActionContextTest extends TestCase {

    private static final String APPLICATION_KEY = "com.opensymphony.xwork.ActionContextTest.application";
    private static final String SESSION_KEY = "com.opensymphony.xwork.ActionContextTest.session";
    private static final String PARAMETERS_KEY = "com.opensymphony.xwork.ActionContextTest.params";
    private static final String ACTION_NAME = "com.opensymphony.xwork.ActionContextTest.actionName";

    private ActionContext context;

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

    public void testApplication() {
        Map app = new HashMap();
        context.setApplication(app);
        assertEquals(app, context.getApplication());
    }

    public void testContextMap() {
        Map map = new HashMap();
        context.setContextMap(map);
        assertEquals(map, context.getContextMap());
    }

    public void testParameters() {
        Map param = new HashMap();
        context.setParameters(param);
        assertEquals(param, context.getParameters());
    }

    public void testConversionErrors() {
        Map errors = context.getConversionErrors();
        assertNotNull(errors);
        assertEquals(0, errors.size());

        Map errors2 = new HashMap();
        context.setConversionErrors(errors);
        assertEquals(errors2, context.getConversionErrors());
    }

    public void testStaticMethods() {
        assertEquals(context, ActionContext.getContext());

        ActionContext context2 = new ActionContext(null);
        ActionContext.setContext(context2);

        assertEquals(context2, ActionContext.getContext());
    }

}
