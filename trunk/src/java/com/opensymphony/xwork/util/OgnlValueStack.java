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


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class OgnlValueStack implements Serializable {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String VALUE_STACK = "com.opensymphony.xwork.util.OgnlValueStack.ValueStack";
    private static CompoundRootAccessor accessor;

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

    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    public CompoundRoot getRoot() {
        return root;
    }

    public void setValue(String expr, Object value) {
        try {
            getContext().put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            Ognl.setValue(OgnlUtil.compile(expr), context, root, value);
        } catch (OgnlException e) {
            // ignore
        } finally {
            getContext().remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
        }
    }

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
        }
    }

    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            return Ognl.getValue(OgnlUtil.compile(expr), context, root, asType);
        } catch (OgnlException e) {
            return null;
        }
    }

    public Object peek() {
        return root.peek();
    }

    public Object pop() {
        return root.pop();
    }

    public void push(Object o) {
        root.push(o);
    }

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
