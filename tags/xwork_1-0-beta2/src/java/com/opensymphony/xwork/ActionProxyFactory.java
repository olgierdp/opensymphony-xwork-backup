/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Map;


/**
 * ActionProxyFactory
 * @author Jason Carreira
 * Created Jun 15, 2003 5:18:30 PM
 */
public abstract class ActionProxyFactory {
    //~ Static fields/initializers /////////////////////////////////////////////

    static ActionProxyFactory factory = new DefaultActionProxyFactory();

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void setFactory(ActionProxyFactory factory) {
        ActionProxyFactory.factory = factory;
    }

    public static ActionProxyFactory getFactory() {
        return factory;
    }

    public abstract ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception;

    public abstract ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception;

    public abstract ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception;

    public abstract ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception;

    public abstract ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult) throws Exception;
}
