/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.DefaultTextProvider;
import com.opensymphony.xwork.XworkException;
import ognl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;


/**
 * OgnlValueStack allows multiple beans to be pushed in and dynamic Ognl expressions to be evaluated against it. When
 * evaluating an expression, the stack will be searched down the stack, from the latest objects pushed in to the
 * earliest, looking for a bean with a getter or setter for the given property or a method of the given name (depending
 * on the expression being evaluated).
 *
 * @author Patrick Lightbody
 */
public class OgnlValueStack implements Serializable {
	
	private static final long serialVersionUID = -3444677371593148954L;
	
	public static final String VALUE_STACK = "com.opensymphony.xwork.util.OgnlValueStack.ValueStack";
    public static final String REPORT_ERRORS_ON_NO_PROP = "com.opensymphony.xwork.util.OgnlValueStack.ReportErrorsOnNoProp";
    private static CompoundRootAccessor accessor;
    private static Log LOG = LogFactory.getLog(OgnlValueStack.class);

    static {
        reset();
    }

    public static void reset() {
        accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setPropertyAccessor(Object.class, new ObjectAccessor());
        OgnlRuntime.setPropertyAccessor(Iterator.class, new XWorkIteratorPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Enumeration.class, new XWorkEnumerationAcccessor());
        OgnlRuntime.setPropertyAccessor(List.class, new XWorkListPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Map.class, new XWorkMapPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Collection.class, new XWorkCollectionPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Set.class, new XWorkCollectionPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(ObjectProxy.class, new ObjectProxyPropertyAccessor());
        OgnlRuntime.setMethodAccessor(Object.class, new XWorkMethodAccessor());
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setNullHandler(Object.class, new InstantiatingNullHandler());
    }

    public static class ObjectAccessor extends ObjectPropertyAccessor {
        public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
            Object obj = super.getProperty(map, o, o1);
            link(map, o.getClass(), (String) o1);

            map.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, o.getClass());
            map.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, o1.toString());
            OgnlContextState.updateCurrentPropertyPath(map, o1);
            return obj;
        }

        public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
            super.setProperty(map, o, o1, o2);
        }
    }

    public static void link(Map context, Class clazz, String name) {
        context.put("__link", new Object[]{clazz, name});
    }


    CompoundRoot root;
    transient Map context;
    Class defaultType;
    Map overrides;


    public OgnlValueStack() {
        setRoot(new CompoundRoot());
        push(DefaultTextProvider.INSTANCE);
    }

    public OgnlValueStack(OgnlValueStack vs) {
        setRoot(new CompoundRoot(vs.getRoot()));
    }


    public static CompoundRootAccessor getAccessor() {
        return accessor;
    }

    public Map getContext() {
        return context;
    }

    /**
     * Sets the default type to convert to if no type is provided when getting a value.
     *
     * @param defaultType
     */
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    public void setExprOverrides(Map overrides) {
    	if (this.overrides == null) {
    		this.overrides = overrides;
    	}
    	else {
    		this.overrides.putAll(overrides);
    	}
    }

    /**
     * Get the CompoundRoot which holds the objects pushed onto the stack
     */
    public CompoundRoot getRoot() {
        return root;
    }

    /**
     * Determine whether devMode is enabled.
     * @return true if devMode was enabled, false otherwise.
     */
    public boolean isDevModeEnabled() {
        Boolean devMode = (Boolean) context.get(ActionContext.DEV_MODE);
        if (devMode != null) {
        	return devMode.booleanValue();
        } else {
            return false;
        }
    }

    /**
     * Attempts to set a property on a bean in the stack with the given expression using the default search order.
     *
     * @param expr  the expression defining the path to the property to be set.
     * @param value the value to be set into the neamed property
     */
    public void setValue(String expr, Object value) {
        setValue(expr, value, isDevModeEnabled());
    }

    /**
     * Attempts to set a property on a bean in the stack with the given expression using the default search order.
     *
     * @param expr                    the expression defining the path to the property to be set.
     * @param value                   the value to be set into the neamed property
     * @param throwExceptionOnFailure a flag to tell whether an exception should be thrown if there is no property with
     *                                the given name.
     */
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map context = getContext();

        try {
            context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
            OgnlUtil.setValue(expr, context, root, value);
        } catch (OgnlException e) {
            if (throwExceptionOnFailure) {
                String msg = "Error setting expr '" + expr + "' with value '" + value + "'";
                LOG.error(msg, e);
                throw new XworkException(msg, e);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", e);
                }
            }
        } catch (XworkException re) { //XW-281
            if (throwExceptionOnFailure) {
                String msg = "Error setting expr '" + expr + "' with value '" + value + "'";
                LOG.error(msg, re);
                throw re;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", re);
                }
            }
        } catch (RuntimeException re) { //XW-281
            if (throwExceptionOnFailure) {
                String msg = "Error setting expr '" + expr + "' with value '" + value + "'";
                LOG.error(msg, re);
                throw new XworkException(msg, re);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", re);
                }
            }
        } finally {
            OgnlContextState.clear(context);
            context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
            context.remove(REPORT_ERRORS_ON_NO_PROP);
        }
    }

    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    /**
     * Find a value by evaluating the given expression against the stack in the default search order.
     *
     * @param expr the expression giving the path of properties to navigate to find the property value to return
     * @return the result of evaluating the expression
     */
    public Object findValue(String expr) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            if (defaultType != null) {
                return findValue(expr, defaultType);
            }

            Object value = OgnlUtil.getValue(expr, context, root);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            OgnlContextState.clear(context);
        }
    }

    /**
     * Find a value by evaluating the given expression against the stack in the default search order.
     *
     * @param expr   the expression giving the path of properties to navigate to find the property value to return
     * @param asType the type to convert the return value to
     * @return the result of evaluating the expression
     */
    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            Object value = OgnlUtil.getValue(expr, context, root, asType);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            OgnlContextState.clear(context);
        }
    }

    private Object findInContext(String name) {
        return getContext().get(name);
    }

    /**
     * Log a failed lookup, being more verbose when devMode=true.
     *
     * @param expr The failed expression
     * @param e The thrown exception.
     */
    private void logLookupFailure(String expr, Exception e) {
        StringBuffer msg = new StringBuffer();
        msg.append("Caught an exception while evaluating expression '").append(expr).append("' against value stack");
        if (isDevModeEnabled() && LOG.isWarnEnabled()) {
            LOG.warn( msg , e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else if ( LOG.isDebugEnabled() ) {
            LOG.debug( msg , e);
        }
    }

    /**
     * Get the object on the top of the stack without changing the stack.
     *
     * @see CompoundRoot#peek()
     */
    public Object peek() {
        return root.peek();
    }

    /**
     * Get the object on the top of the stack and remove it from the stack.
     *
     * @return the object on the top of the stack
     * @see CompoundRoot#pop()
     */
    public Object pop() {
        return root.pop();
    }

    /**
     * Put this object onto the top of the stack
     *
     * @param o the object to be pushed onto the stack
     * @see CompoundRoot#push(Object)
     */
    public void push(Object o) {
        root.push(o);
    }

    /**
     * Get the number of objects in the stack
     * s
     *
     * @return the number of objects in the stack
     */
    public int size() {
        return root.size();
    }

    private void setRoot(CompoundRoot compoundRoot) {
        this.root = compoundRoot;
        this.context = Ognl.createDefaultContext(this.root, accessor, XWorkConverter.getInstance());
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    private Object readResolve() {
        OgnlValueStack aStack = new OgnlValueStack();
        aStack.setRoot(this.root);

        return aStack;
    }
}
