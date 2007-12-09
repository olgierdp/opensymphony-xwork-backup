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
    
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) {
        return createActionProxy(namespace, actionName, null, extraContext, true, true);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map extraContext) {
        return createActionProxy(namespace, actionName, methodName, extraContext, true, true);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) {
        return createActionProxy(namespace, actionName, null, extraContext, executeResult, cleanupContext);
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map extraContext, boolean executeResult, boolean cleanupContext) {
        
        ActionInvocation inv = new DefaultActionInvocation(extraContext, true);
        container.inject(inv);
        return createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
    }
    
    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, boolean executeResult, boolean cleanupContext) {
        
        return createActionProxy(inv, namespace, actionName, null, executeResult, cleanupContext);
    }

    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {

        DefaultActionProxy proxy = new DefaultActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }

}
