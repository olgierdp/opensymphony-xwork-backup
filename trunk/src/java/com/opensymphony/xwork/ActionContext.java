/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * @author $Author$
 * @version $Revision$
 */
public class ActionContext {
    //~ Static fields/initializers /////////////////////////////////////////////

    static ThreadLocal actionContext = new ActionContextThreadLocal();
    public static final String ACTION_NAME = "com.opensymphony.xwork.ActionContext.name";
    public static final String VALUE_STACK = OgnlValueStack.VALUE_STACK;
    public static final String SESSION = "com.opensymphony.xwork.ActionContext.session";
    public static final String APPLICATION = "com.opensymphony.xwork.ActionContext.application";
    public static final String PARAMETERS = "com.opensymphony.xwork.ActionContext.parameters";
    public static final String LOCALE = "com.opensymphony.xwork.ActionContext.locale";
    public static final String TYPE_CONVERTER = "com.opensymphony.xwork.ActionContext.typeConverter";
    public static final String ACTION_INVOCATION = "com.opensymphony.xwork.ActionContext.actionInvocation";
    public static final String CONVERSION_ERRORS = "com.opensymphony.xwork.ActionContext.conversionErrors";

    //~ Instance fields ////////////////////////////////////////////////////////

    Map context;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ActionContext(Map context) {
        this.context = context;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setActionInvocation(ActionInvocation actionInvocation) {
        put(ACTION_INVOCATION, actionInvocation);
    }

    public ActionInvocation getActionInvocation() {
        return (ActionInvocation) get(ACTION_INVOCATION);
    }

    /**
     * Set an application level Map.
     */
    public void setApplication(Map application) {
        put(APPLICATION, application);
    }

    /**
     * Returns a Map of the ServletContext when in a servlet environment or
     * a generic application level Map otherwise.
     *
     * @return Map of ServletContext or generic application level Map
     */
    public Map getApplication() {
        return (Map) get(APPLICATION);
    }

    public static void setContext(ActionContext aContext) {
        actionContext.set(aContext);
    }

    /**
     * Returns the ActionContext specific to the current thread.
     *
     * @return ActionContext for the current thread
     */
    public static ActionContext getContext() {
        ActionContext context = (ActionContext) actionContext.get();
        if (context == null) {
            OgnlValueStack vs = new OgnlValueStack();
            context = new ActionContext(vs.getContext());
            setContext(context);
        }
        return context;
    }

    public void setContextMap(Map lookup) {
        getContext().context = lookup;
    }

    public Map getContextMap() {
        return context;
    }

    public void setConversionErrors(Map conversionErrors) {
        put(CONVERSION_ERRORS, conversionErrors);
    }

    public Map getConversionErrors() {
        Map errors = (Map) get(CONVERSION_ERRORS);

        if (errors == null) {
            errors = new HashMap();
            setConversionErrors(errors);
        }

        return errors;
    }

    /**
     * Sets the Locale for the current request
     * @param locale
     */
    public void setLocale(Locale locale) {
        put(LOCALE, locale);
    }

    /**
     * Gets the Locale of the current request
     * @return Locale
     */
    public Locale getLocale() {
        Locale locale = (Locale) get(LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
            setLocale(locale);
        }

        return locale;
    }

    /**
     * Stores the name of the current Action in the ActionContext.
     *
     * @param name The name of the current action.
     */
    public void setName(String name) {
        put(ACTION_NAME, name);
    }

    /**
     * Returns the name of the current Action.
     *
     * @return The current Action name.
     */
    public String getName() {
        return (String) get(ACTION_NAME);
    }

    /**
     * Set a Map of parameters.
     *
     * @param parameters The parameters for the current action context.
     */
    public void setParameters(Map parameters) {
        put(PARAMETERS, parameters);
    }

    /**
     * Returns a Map of the HttpServletRequest parameters when in a servlet
     * environment or a generic Map of parameters otherwise.
     *
     * @return Map of HttpServletRequest parameters, generic Map of parameters or
     * multipart Map.
     */
    public Map getParameters() {
        return (Map) get(PARAMETERS);
    }

    /**
     * Set a session Map.
     */
    public void setSession(Map session) {
        put(SESSION, session);
    }

    /**
     * Returns the HttpSession when in a servlet environment or a generic
     * session map otherwise.
     *
     * @return a map of HttpSession or a generic session map
     */
    public Map getSession() {
        return (Map) get(SESSION);
    }

    public void setValueStack(OgnlValueStack stack) {
        put(VALUE_STACK, stack);
    }

    public OgnlValueStack getValueStack() {
        return (OgnlValueStack) get(VALUE_STACK);
    }

    /**
     * Returns a value that is stored in the current ActionContext
     * by doing a lookup using the value's key.
     *
     * @param key The key used to find the value.
     * @return The value that was found using the key.
     */
    public Object get(Object key) {
        return context.get(key);
    }

    /**
     * Stores a value in the current ActionContext.  The value can
     * be looked up using the key.
     *
     * @param key The key of the value.
     * @param value The value to be stored.
     */
    public void put(Object key, Object value) {
        context.put(key, value);
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private static class ActionContextThreadLocal extends ThreadLocal {
        protected Object initialValue() {
            OgnlValueStack vs = new OgnlValueStack();

            return new ActionContext(vs.getContext());
        }
    }
}
