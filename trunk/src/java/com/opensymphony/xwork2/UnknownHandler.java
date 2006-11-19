/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Handles cases when the result or action is unknown.  This allows other classes like Struts plugins to
 * provide intelligent defaults easier.
 */
public interface UnknownHandler {
    
    /**
     * Handles the case when an action configuration is unknown.  Implementations can return a new ActionConfig
     * to be used to process the request.
     * 
     * @param namespace The namespace
     * @param actionName The action name
     * @return An generated ActionConfig, can return null;
     * @throws XWorkException
     */
    public ActionConfig handleUnknownAction(String namespace, String actionName) throws XWorkException;
    
    /**
     * Handles the case when a result cannot be found for an action and result code. 
     * 
     * @param actionContext The action context
     * @param actionConfig The action config
     * @param resultCode The returned result code
     * @return A result to be executed, can return null
     * @throws XWorkException
     */
    public Result handleUnknownResult(ActionContext actionContext, ActionConfig actionConfig, String resultCode) throws XWorkException;
}
