/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Map;


/**
 * The ActionProxyFactory is used to create ActionProxies to be executed. It is the entry point to XWork that is used
 * by a dispatcher to create an ActionProxy to execute for a particular namespace and action name.
 *
 * @author Jason Carreira
 *         Created Jun 15, 2003 5:18:30 PM
 * @see DefaultActionProxyFactory
 */
public abstract class ActionProxyFactory {

    static ActionProxyFactory factory = new DefaultActionProxyFactory();


    /**
     * Set the ActionProxyFactory implementation to use. If no instance is set, a new DefaultActionProxyFactory is used.
     *
     * @param factory
     */
    public static void setFactory(ActionProxyFactory factory) {
        ActionProxyFactory.factory = factory;
    }

    public static ActionProxyFactory getFactory() {
        return factory;
    }

    /**
     * Used by an ActionProxy or ActionProxyFactory to create an ActionInvocation to associate with an ActionProxy
     * as part of creating an ActionProxy. Client code should not need to call the createActionInvocation methods.
     *
     * @param actionProxy
     * @param extraContext
     * @return ActionInvocation
     * @throws Exception
     */
    public abstract ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception;

    /**
     * Used by an ActionProxy or ActionProxyFactory to create an ActionInvocation to associate with an ActionProxy
     * as part of creating an ActionProxy. Client code should not need to call the createActionInvocation methods.
     *
     * @param actionProxy
     * @return ActionInvocation
     * @throws Exception
     */
    public abstract ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception;

    /**
     * Used by an ActionProxy or ActionProxyFactory to create an ActionInvocation to associate with an ActionProxy
     * as part of creating an ActionProxy. Client code should not need to call the createActionInvocation methods.
     *
     * @param actionProxy
     * @param extraContext
     * @param pushAction   tells whether the Action should be pushed onto the ValueStack
     * @return ActionInvocation
     * @throws Exception
     */
    public abstract ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception;

    /**
     * Creates an ActionProxy for the given namespace and action name by looking up the configuration. The ActionProxy
     * should be fully initialized when it is returned, including having an ActionInvocation instance associated.
     *
     * @param namespace    the namespace of the action
     * @param actionName
     * @param extraContext a Map of extra parameters to be provided to the ActionProxy
     * @return ActionProxy
     * @throws Exception
     */
    public abstract ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception;

    /**
     * Creates an ActionProxy for the given namespace and action name by looking up the configuration. The ActionProxy
     * should be fully initialized when it is returned, including having an ActionInvocation instance associated.
     *
     * @param namespace     the namespace of the action
     * @param actionName
     * @param extraContext  a Map of extra parameters to be provided to the ActionProxy
     * @param executeResult flag which tells whether the result should be executed after the action
     * @param cleanupContext
     * @return ActionProxy
     * @throws Exception
     */
    public abstract ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception;
}
