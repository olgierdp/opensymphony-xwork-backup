/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.SimpleFooAction;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * Unit test for I18nInterceptor.
 *
 * @author Claus Ibsen
 */
public class I18nInterceptorTest extends TestCase {

    private I18nInterceptor interceptor;
    private ActionContext ac;
    private Map params;
    private Map session;
    private MockActionInvocation mai;

    public void testEmptyParamAndSession() throws Exception {
        interceptor.intercept(mai);
    }

    public void testNoSession() throws Exception {
        ac.setSession(null);
        interceptor.intercept(mai);
    }

    public void testDefaultLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "_"); // bad locale that would get us default locale instead
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.getDefault(), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testDenmarkLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("da", "DK");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testCountryOnlyLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "DK");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("DK");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testLanguageOnlyLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "da_");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale denmark = new Locale("da");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testWithVariant() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "fr_CA_xx");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        Locale variant = new Locale("fr", "CA", "xx");
        Locale locale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
        assertNotNull(locale); // should be stored here
        assertEquals(variant, locale);
        assertEquals("xx", locale.getVariant());
    }

    public void testRealLocaleObjectInParams() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, Locale.CANADA_FRENCH);
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should create a locale object
    }

    public void testRealLocalesInParams() throws Exception {
        Locale[] locales = new Locale[] { Locale.CANADA_FRENCH };
        assertTrue(locales.getClass().isArray());
        params.put(I18nInterceptor.DEFAULT_PARAMETER, locales);
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); // should have been removed

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); // should be stored here
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
    }

    public void testSetParameterAndAttributeNames() throws Exception {
        interceptor.setAttributeName("hello");
        interceptor.setParameterName("world");

        params.put("world", Locale.CHINA);
        interceptor.intercept(mai);

        assertNull(params.get("world")); // should have been removed

        assertNotNull(session.get("hello")); // should be stored here
        assertEquals(Locale.CHINA, session.get("hello"));
    }

    protected void setUp() throws Exception {
        interceptor = new I18nInterceptor();
        interceptor.init();
        params = new HashMap();
        session = new HashMap();

        Map ctx = new HashMap();
        ctx.put(ActionContext.PARAMETERS, params);
        ctx.put(ActionContext.SESSION, session);
        ac = new ActionContext(ctx);

        Action action = new SimpleFooAction();
        mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setInvocationContext(ac);
    }

    protected void tearDown() throws Exception {
        interceptor.destroy();
        interceptor = null;
        ac = null;
        params = null;
        session = null;
        mai = null;
    }

}
