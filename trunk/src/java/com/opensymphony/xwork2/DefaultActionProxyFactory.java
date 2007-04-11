/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Map;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;


/**
 * Default factory for {@link com.opensymphony.xwork2.ActionProxyFactory}.
 *
 * @author Jason Carreira
 */
public class DefaultActionProxyFactory implements ActionProxyFactory {

    protected Container container;
    
    public DefaultActionProxyFactory() {
        super();
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception {
        return createActionProxy(namespace, actionName, extraContext, true, true);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
        ActionProxy proxy = new DefaultActionProxy(namespace, actionName, extraContext, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }

}
