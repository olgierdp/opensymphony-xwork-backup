/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.util.OgnlValueStack;


/**
 * ActionProxy
 * @author Jason Carreira
 * Created Jun 9, 2003 11:27:55 AM
 */
public interface ActionProxy {
    //~ Methods ////////////////////////////////////////////////////////////////

    Action getAction();

    String getActionName();

    ActionConfig getConfig();

    void setExecuteResult(boolean executeResult);

    boolean getExecuteResult();

    ActionInvocation getInvocation();

    ActionContext getLastContext();

    String getNamespace();

    OgnlValueStack getValueStack();

    String execute() throws Exception;
}
