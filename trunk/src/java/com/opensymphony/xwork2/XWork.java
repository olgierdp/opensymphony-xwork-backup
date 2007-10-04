/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Collections;
import java.util.Map;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;

/**
 * Simple facade to make using XWork standalone easier
 */
public class XWork {
    
    ConfigurationManager configurationManager;
    
    public XWork() {
        this(new ConfigurationManager());
    }
    
    public XWork(ConfigurationManager mgr) {
        this.configurationManager = mgr;
    }
    
    /**
     * Executes an action
     * 
     * @param namespace The namespace
     * @param name The action name
     * @param method The method name
     * @throws Exception If anything goes wrong
     */
    public void executeAction(String namespace, String name, String method) throws XWorkException {
        executeAction(namespace, name, method, Collections.EMPTY_MAP);
    }
    
    /**
     * Executes an action with extra context information
     * 
     * @param namespace The namespace
     * @param name The action name
     * @param method The method name
     * @param extraContext A map of extra context information
     * @throws Exception If anything goes wrong
     */
    public void executeAction(String namespace, String name, String method, Map extraContext) throws XWorkException {
        Configuration config = configurationManager.getConfiguration();
        try {
            ActionProxy proxy = config.getContainer().getInstance(ActionProxyFactory.class).createActionProxy(
                    namespace, name, extraContext, true, false);
            proxy.setMethod(method);
        
            proxy.execute();
        } catch (Exception e) {
            throw new XWorkException(e);
        } finally {
            ActionContext.setContext(null);
        }
    }
}
