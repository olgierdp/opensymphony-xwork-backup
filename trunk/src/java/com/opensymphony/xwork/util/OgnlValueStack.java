/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.io.ObjectStreamException;
import java.io.Serializable;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * OgnlValueStack allows multiple beans to be pushed in and dynamic Ognl expressions to be evaluated against it. When
 * evaluating an expression, the stack will be searched down the stack, from the latest objects pushed in to the
 * earliest, looking for a bean with a getter or setter for the given property or a method of the given name (depending
 * on the expression being evaluated).
 *
 * @author $Author$
 * @version $Revision$
 */
public class OgnlValueStack implements Serializable {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String VALUE_STACK = "com.opensymphony.xwork.util.OgnlValueStack.ValueStack";
    public static final String REPORT_ERRORS_ON_NO_PROP = "com.opensymphony.xwork.util.OgnlValueStack.ReportErrorsOnNoProp";
    private static CompoundRootAccessor accessor;
    private static Log LOG = LogFactory.getLog(OgnlValueStack.class);

    static {
        accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setPropertyAccessor(Iterator.class, new XWorkIteratorPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Enumeration.class, new XWorkEnumerationAcccessor());
        OgnlRuntime.setMethodAccessor(Object.class, new XWorkMethodAccessor());
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setNullHandler(Object.class, new InstantiatingNullHandler());
    }

    //~ Instance fields ////////////////////////////////////////////////////////

    CompoundRoot root;
    transient Map context;
    Class defaultType;

    //~ Constructors ///////////////////////////////////////////////////////////

    public OgnlValueStack() {
        setRoot(new CompoundRoot());
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static CompoundRootAccessor getAccessor() {
        return accessor;
    }

    public Map getContext() {
        return context;
    }

    /**
     * Sets the default type to convert to if no type is provided when getting a value.
     * @param defaultType
     */
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    /**
     * Get the CompoundRoot which holds the objects pushed onto the stack
     * @return
     */
    public CompoundRoot getRoot() {
        return root;
    }

    /**
     * Attempts to set a property on a bean in the stack with the given expression using the default search order.
     * @param expr the expression defining the path to the property to be set.
     * @param value the value to be set into the neamed property
     */
    public void setValue(String expr, Object value) {
        setValue(expr, value, false);
    }

    /**
     * Attempts to set a property on a bean in the stack with the given expression using the default search order.
     * @param expr the expression defining the path to the property to be set.
     * @param value the value to be set into the neamed property
     * @param throwExceptionOnFailure a flag to tell whether an exception should be thrown if there is no property with
     * the given name.
     */
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map context = getContext();

        try {
            context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            context.put(REPORT_ERRORS_ON_NO_PROP, new Boolean(throwExceptionOnFailure));
            Ognl.setValue(OgnlUtil.compile(expr), context, root, value);
        } catch (OgnlException e) {
            // ignore
        } finally {
            context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
            context.remove(REPORT_ERRORS_ON_NO_PROP);
        }
    }

    /**
     * Find a value by evaluating the given expression against the stack in the default search order.
     * @param expr the expression giving the path of properties to navigate to find the property value to return
     * @return the result of evaluating the expression
     */
    public Object findValue(String expr) {
        try {
            if (expr == null) {
                return null;
            }

            if (defaultType != null) {
                return findValue(expr, defaultType);
            }

            return Ognl.getValue(OgnlUtil.compile(expr), context, root);
        } catch (OgnlException e) {
            return null;
        } catch (Exception e) {
            LOG.warn("Caught an exception while evaluating expression '" + expr + "' against value stack", e);
            return null;
        }
    }

    /**
     * Find a value by evaluating the given expression against the stack in the default search order.
     * @param expr the expression giving the path of properties to navigate to find the property value to return
     * @param asType the type to convert the return value to
     * @return the result of evaluating the expression
     */
    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            return Ognl.getValue(OgnlUtil.compile(expr), context, root, asType);
        } catch (OgnlException e) {
            return null;
        } catch (Exception e) {
            LOG.warn("Caught an exception while evaluating expression '" + expr + "' against value stack", e);
            return null;
        }
    }

    /**
     * Get the object on the top of the stack without changing the stack.
     * @see {@link com.opensymphony.xwork.util.CompoundRoot#peek()}
     */
    public Object peek() {
        return root.peek();
    }

    /**
     * Get the object on the top of the stack and remove it from the stack.
     * @see {@link com.opensymphony.xwork.util.CompoundRoot#pop()}
     * @return
     */
    public Object pop() {
        return root.pop();
    }

    /**
     * Put this object onto the top of the stack
     * @param o the object to be pushed onto the stack
     * @see {@link com.opensymphony.xwork.util.CompoundRoot#push(java.lang.Object)}
     */
    public void push(Object o) {
        root.push(o);
    }

    /**
     * Get the number of objects in the stack
     * @return
     */
    public int size() {
        return root.size();
    }

    private void setRoot(CompoundRoot compoundRoot) {
        this.root = compoundRoot;
        this.context = Ognl.createDefaultContext(this.root, accessor, XWorkConverter.getInstance());
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(true);
        ((OgnlContext) context).setKeepLastEvaluation(true);
    }

    private Object readResolve() throws ObjectStreamException {
        OgnlValueStack aStack = new OgnlValueStack();
        aStack.setRoot(this.root);

        return aStack;
    }
}
