/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:hu_pengfei@yahoo.com.cn">Henry Hu</a>
 * @since 2005-7-6
 */
public class ActionGlobalContext implements Serializable {

    static ThreadLocal actionGlobalContextThreadLocal = new ActionGlobalContextThreadLocal();

    Map mapContext;

    private static class ActionGlobalContextThreadLocal extends ThreadLocal {
        private ActionGlobalContextThreadLocal() {
        }
    }

    public ActionGlobalContext(Map mapContext) {
        this.mapContext = mapContext;
    }

    public static void setContext(ActionGlobalContext actionGlobalContext) {
        actionGlobalContextThreadLocal.set(actionGlobalContext);
    }

    public static ActionGlobalContext getContext() {
        ActionGlobalContext actionGlobalContext = (ActionGlobalContext) actionGlobalContextThreadLocal.get();

        if (actionGlobalContext == null) {
            OgnlValueStack vs = new OgnlValueStack();
            actionGlobalContext = new ActionGlobalContext(vs.getContext());
            actionGlobalContextThreadLocal.set(actionGlobalContext);
        }

        return actionGlobalContext;
    }

    public Object get(Object key) {
        return mapContext.get(key);
    }

    public void set(Object key, Object value) {
        mapContext.put(key, value);
    }

    public void setActionExecuted(String actionExecuted) {
        set("Executed", actionExecuted);
    }

    public String getActionExecuted() {
        return (String) get("Executed");
    }

}