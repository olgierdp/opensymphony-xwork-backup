/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;


/**
 *
 *
 * @author joew@thoughtworks.com
 * @author $Author$
 * @version $Revision$
 */
public class ComponentInterceptor extends AroundInterceptor {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String COMPONENT_MANAGER = "com.opensymphony.xwork.interceptor.component.ComponentManager";

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation dispatcher) throws Exception {
        ComponentManager container = (ComponentManager) ActionContext.getContext().get(COMPONENT_MANAGER);

        if (container != null) {
            container.initializeObject(dispatcher.getAction());
        }
    }
}
