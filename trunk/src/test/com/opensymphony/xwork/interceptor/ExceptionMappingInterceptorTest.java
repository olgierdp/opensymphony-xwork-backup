package com.opensymphony.xwork.interceptor;

import junit.framework.TestCase;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ExceptionMappingConfig;
import com.mockobjects.dynamic.Mock;

import java.util.HashMap;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Aug 15, 2005
 * Time: 9:05:05 PM
 */
public class ExceptionMappingInterceptorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    ActionInvocation invocation;
    ExceptionMappingInterceptor interceptor;
    Mock mockInvocation;
    OgnlValueStack stack;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testThrownExceptionMatching() throws Exception {
        Mock action = new Mock(Action.class);
        mockInvocation.expectAndThrow("invoke", new XworkException("test"));
        mockInvocation.matchAndReturn("getAction", ((Action)action.proxy()));
        String result = interceptor.intercept(invocation);
        assertEquals(result, "spooky");
    }

    public void testThrownExceptionMatching2() throws Exception {
        Mock action = new Mock(Action.class);
        mockInvocation.expectAndThrow("invoke", new ValidationException("test"));
        mockInvocation.matchAndReturn("getAction", ((Action)action.proxy()));
        String result = interceptor.intercept(invocation);
        assertEquals(result, "throwable");
    }

    protected void setUp() throws Exception {
        super.setUp();
        stack = new OgnlValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(new HashMap()));

        ActionConfig actionConfig = new ActionConfig();
        actionConfig.addExceptionMapping(new ExceptionMappingConfig("xwork", "com.opensymphony.xwork.XworkException", "spooky"));
        actionConfig.addExceptionMapping(new ExceptionMappingConfig("throwable", "java.lang.Throwable", "throwable"));
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy)actionProxy.proxy()));

        invocation = (ActionInvocation) mockInvocation.proxy();
        interceptor = new ExceptionMappingInterceptor();
    }

}
