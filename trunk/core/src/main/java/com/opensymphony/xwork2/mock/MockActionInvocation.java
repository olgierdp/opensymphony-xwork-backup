/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock for an {@link ActionInvocation}.
 *
 * @author plightbo
 * @author Rainer Hermanns
 * @author tm_jee
 * @version $Id$
 */
public class MockActionInvocation implements ActionInvocation {

    private Object action;
    private ActionContext invocationContext;
    private ActionEventListener actionEventListener;
    private ActionProxy proxy;
    private Result result;
    private String resultCode;
    private ValueStack stack;
    
    private List<PreResultListener> preResultListeners = new ArrayList<PreResultListener>();

    public Object getAction() {
        return action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    public ActionContext getInvocationContext() {
        return invocationContext;
    }

    public void setInvocationContext(ActionContext invocationContext) {
        this.invocationContext = invocationContext;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    public void setProxy(ActionProxy proxy) {
        this.proxy = proxy;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ValueStack getStack() {
        return stack;
    }

    public void setStack(ValueStack stack) {
        this.stack = stack;
    }

    public boolean isExecuted() {
        return false;
    }

    public void addPreResultListener(PreResultListener listener) {
    	preResultListeners.add(listener);
    }

    public String invoke() throws Exception {
        for (Object preResultListener : preResultListeners) {
            PreResultListener listener = (PreResultListener) preResultListener;
            listener.beforeResult(this, resultCode);
        }
        return resultCode;
    }

    public String invokeActionOnly() throws Exception {
        return resultCode;
    }

    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }
    
    public ActionEventListener getActionEventListener() {
        return this.actionEventListener;
    }

    public void init(ActionProxy proxy) {
    }

}
