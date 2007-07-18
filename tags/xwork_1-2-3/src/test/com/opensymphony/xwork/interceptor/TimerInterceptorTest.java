/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.SimpleFooAction;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.mock.MockActionProxy;
import org.apache.commons.logging.Log;

/**
 * Unit test for {@link TimerInterceptor}.
 *
 * @author Claus Ibsen
 */
public class TimerInterceptorTest extends XWorkTestCase {

    private MyTimerInterceptor interceptor;
    private MockActionInvocation mai;
    private MockActionProxy ap;


    public void testTimerInterceptor() throws Exception {
        TimerInterceptor real = new TimerInterceptor();
        real.init();
        real.intercept(mai);
        real.destroy();
    }

    public void testInvalidLogLevel() throws Exception {
        TimerInterceptor real = new TimerInterceptor();
        real.setLogLevel("xxxx");
        real.init();
        try {
            real.intercept(mai);
            fail("Should not have reached this point.");
        } catch (IllegalArgumentException e) {
        	// success
        }
    }

    public void testDefault() throws Exception {
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testNoNamespace() throws Exception {
        ap.setNamespace(null);
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testInputMethod() throws Exception {
        ap.setMethod("input");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!input] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testTraceLevel() throws Exception {
        interceptor.setLogLevel("trace");
        interceptor.intercept(mai);
        assertNull(interceptor.message); // no default logging at trace level
        assertEquals("trace", interceptor.getLogLevel());
    }

    public void testDebugLevel() throws Exception {
        interceptor.setLogLevel("debug");
        interceptor.intercept(mai);
        assertNull(interceptor.message); // no default logging at debug level
    }

    public void testInfoLevel() throws Exception {
        interceptor.setLogLevel("info");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testWarnLevel() throws Exception {
        interceptor.setLogLevel("warn");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testErrorLevel() throws Exception {
        interceptor.setLogLevel("error");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testFatalLevel() throws Exception {
        interceptor.setLogLevel("fatal");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testLogCategory() throws Exception {
        interceptor.setLogCategory("com.mycompany.myapp.actiontiming");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertNotSame(interceptor.logger, TimerInterceptor.log);
    }

    public void testLogCategoryLevel() throws Exception {
        interceptor.setLogCategory("com.mycompany.myapp.actiontiming");
        interceptor.setLogLevel("error");
        interceptor.intercept(mai);
        assertTrue(interceptor.message.startsWith("Executed action [myApp/myAction!execute] took "));
        assertNotSame(interceptor.logger, TimerInterceptor.log);
        assertEquals("com.mycompany.myapp.actiontiming", interceptor.getLogCategory());
    }

    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new MyTimerInterceptor();
        interceptor.init();

        mai = new MockActionInvocation();
        ap = new MockActionProxy();
        ap.setActionName("myAction");
        ap.setNamespace("myApp");
        ap.setMethod("execute");
        mai.setAction(new SimpleFooAction());
        mai.setProxy(ap);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
        ap = null;
        mai = null;
    }

    private class MyTimerInterceptor extends TimerInterceptor {

        private Log logger;
        private String message;

        protected void doLog(Log logger, String message) {
            super.doLog(logger, message);

            this.logger = logger;
            this.message = message;
        }
    }

}
