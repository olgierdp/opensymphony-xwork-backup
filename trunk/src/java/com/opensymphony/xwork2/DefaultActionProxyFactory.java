/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Map;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;


/**
 * DefaultActionProxyFactory
 *
 * @author Jason Carreira
 *         Created Jun 15, 2003 5:19:13 PM
 */
public class DefaultActionProxyFactory implements ActionProxyFactory {

    protected Container container;
    protected ObjectFactory objectFactory;
    
    public DefaultActionProxyFactory() {
        super();
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    /**
     * Use this method to build an DefaultActionProxy instance.
     */
    public ActionProxy createActionProxy(Configuration config, String namespace, String actionName, Map extraContext) throws Exception {
        ActionProxy proxy = new DefaultActionProxy(objectFactory, config, namespace, actionName, extraContext, true, true);
        container.inject(proxy);
        return proxy;
    }

    /**
     * Use this method to build an DefaultActionProxy instance.
     */
    public ActionProxy createActionProxy(Configuration config, String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
        ActionProxy proxy = new DefaultActionProxy(objectFactory, config, namespace, actionName, extraContext, executeResult, cleanupContext);
        container.inject(proxy);
        return proxy;
    }
}
