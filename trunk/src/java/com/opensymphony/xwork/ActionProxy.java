/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Serializable;


/**
 * ActionProxy is an extra layer between XWork and the action so that different proxies are possible.
 *
 * An example of this would be a remote proxy, where the layer between XWork and the action might be RMI or SOAP.
 * @author Jason Carreira
 * Created Jun 9, 2003 11:27:55 AM
 */
public interface ActionProxy extends Serializable {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return the Action instance for this Proxy
     */
    Action getAction();

    /**
     * @return the alias name this ActionProxy is mapped to
     */
    String getActionName();

    /**
     * @return the ActionConfig this ActionProxy is built from
     */
    ActionConfig getConfig();

    /**
     * Sets whether this ActionProxy should also execute the Result after executing the Action
     * @param executeResult
     */
    void setExecuteResult(boolean executeResult);

    /**
     * @return the status of whether the ActionProxy is set to execute the Result after the Action is executed
     */
    boolean getExecuteResult();

    /**
     * @return the ActionInvocation associated with this ActionProxy
     */
    ActionInvocation getInvocation();

    /**
     * @return the namespace the ActionConfig for this ActionProxy is mapped to
     */
    String getNamespace();

    /**
     * Execute this ActionProxy. This will set the ActionContext from the ActionInvocation into the ActionContext
     * ThreadLocal before invoking the ActionInvocation, then set the old ActionContext back into the ThreadLocal.
     * @return the result code returned from executing the ActionInvocation
     * @throws Exception
     * @see com.opensymphony.xwork.ActionInvocation
     */
    String execute() throws Exception;
}
