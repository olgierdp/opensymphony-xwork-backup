/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub value stack for testing
 */
public class StubValueStack implements ValueStack {
    Map<String, Object> ctx = new HashMap<String, Object>();
    CompoundRoot root = new CompoundRoot();
    
    public Map<String, Object> getContext() {
        return ctx;
    }

    public void setDefaultType(Class defaultType) {
    }

    public void setExprOverrides(Map<Object, Object> overrides) {
    }

    public Map<Object, Object> getExprOverrides() {
        return null;
    }

    public CompoundRoot getRoot() {
        return root;
    }

    public void setValue(String expr, Object value) {
        ctx.put(expr, value);
    }

    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        ctx.put(expr, value);
    }

    public String findString(String expr) {
        return (String) ctx.get(expr);
    }

    public String findString(String expr, boolean throwExceptionOnFailure) {
        return findString(expr, false);
    }

    public Object findValue(String expr) {
        return ctx.get(expr);
    }

    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        return findValue(expr, false);
    }

    public Object findValue(String expr, Class asType) {
        return ctx.get(expr);
    }

    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        return findValue(expr, asType, false);
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

    public void set(String key, Object o) {
        ctx.put(key, o);
    }

    public int size() {
        return root.size();
    }
}
