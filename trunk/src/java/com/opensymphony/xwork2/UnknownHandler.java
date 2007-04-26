/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Handles cases when the result or action is unknown.
 * <p/>
 * This allows other classes like Struts plugins to provide intelligent defaults easier.
 */
public interface UnknownHandler {
    
    /**
     * Handles the case when an action configuration is unknown.  Implementations can return a new ActionConfig
     * to be used to process the request.
     * 
     * @param namespace The namespace
     * @param actionName The action name
     * @return An generated ActionConfig, can return <tt>null</tt>
     * @throws XWorkException
     */
    public ActionConfig handleUnknownAction(String namespace, String actionName) throws XWorkException;
    
    /**
     * Handles the case when a result cannot be found for an action and result code. 
     * 
     * @param actionContext The action context
     * @param actionName The action name
     * @param actionConfig The action config
     * @param resultCode The returned result code
     * @return A result to be executed, can return <tt>null</tt>
     * @throws XWorkException
     */
    public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode) throws XWorkException;
    
    /**
     * Handles the case when an action method cannot be found.  This method is responsible both for finding the method and executing it.
     * 
     * @since 2.1
     * @param action The action object
     * @param methodName The method name to call
     * @return The result returned from invoking the action method
     * @throws NoSuchMethodException If the method cannot be found
     */
	public Object handleUnknownActionMethod(Object action, String methodName) throws NoSuchMethodException;
}
