/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.List;

/**
 * An unknown handler manager contains a list of UnknownHandler and iterates on them by order
 *
 * @see com.opensymphony.xwork2.DefaultUnknownHandlerManager
 */
public interface UnknownHandlerManager {
    Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode);

    Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException;

    ActionConfig handleUnknownAction(String namespace, String actionName);

    boolean hasUnknownHandlers();

    List<UnknownHandler> getUnknownHandlers();
}
