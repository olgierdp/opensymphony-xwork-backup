/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.util.CompoundRoot;
import com.opensymphony.xwork.util.CompoundRootAccessor;
import com.opensymphony.xwork.util.OgnlUtil;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.util.Map;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class OgnlValueStack {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String VALUE_STACK = "com.opensymphony.xwork.util.OgnlValueStack.ValueStack";
    private static CompoundRootAccessor accessor;

    static {
        accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, accessor);
    }

    //~ Instance fields ////////////////////////////////////////////////////////

    CompoundRoot root;
    Map context;

    //~ Constructors ///////////////////////////////////////////////////////////

    public OgnlValueStack() {
        this.root = new CompoundRoot();
        this.context = Ognl.createDefaultContext(this.root, accessor, DefaultConverter.getInstance());
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static CompoundRootAccessor getAccessor() {
        return accessor;
    }

    public Map getContext() {
        return context;
    }

    public CompoundRoot getRoot() {
        return root;
    }

    public void setValue(String expr, Object value) {
        try {
            Ognl.setValue(OgnlUtil.compile(expr), context, root, value);
        } catch (OgnlException e) {
            // ignore
        }
    }

    public Object findValue(String expr) {
        try {
            return Ognl.getValue(OgnlUtil.compile(expr), context, root);
        } catch (OgnlException e) {
            return null;
        }
    }

    public Object findValue(String expr, Class asType) {
        try {
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
}
