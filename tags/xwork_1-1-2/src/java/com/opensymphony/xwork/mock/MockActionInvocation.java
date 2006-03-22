/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.mock;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * Mock for an {@link ActionInvocation}.
 *
 * @author plightbo
 * @author Rainer Hermanns
 * @version $Id$
 */
public class MockActionInvocation implements ActionInvocation {

    private Object action;
    private ActionContext invocationContext;
    private ActionProxy proxy;
    private Result result;
    private String resultCode;
    private OgnlValueStack stack;

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

    public OgnlValueStack getStack() {
        return stack;
    }

    public void setStack(OgnlValueStack stack) {
        this.stack = stack;
    }

    public boolean isExecuted() {
        return false;
    }

    public void addPreResultListener(PreResultListener listener) {
    }

    public String invoke() throws Exception {
        return resultCode;
    }

    public String invokeActionOnly() throws Exception {
        return resultCode;
    }

}
