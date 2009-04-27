/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class SomeUnknownHandler implements UnknownHandler{
    private ActionConfig actionConfig;
    private String actionMethodResult;

    public ActionConfig handleUnknownAction(String namespace, String actionName) throws XWorkException {
        return actionConfig;
    }

    public Object handleUnknownActionMethod(Object action, String methodName) throws NoSuchMethodException {
        return actionMethodResult;
    }

    public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig,
            String resultCode) throws XWorkException {
        return null;
    }

    public void setActionConfig(ActionConfig actionConfig) {
        this.actionConfig = actionConfig;
    }

    public void setActionMethodResult(String actionMethodResult) {
        this.actionMethodResult = actionMethodResult;
    }
}
