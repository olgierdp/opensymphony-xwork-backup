/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Map;


/**
 * DefaultActionProxyFactory
 * @author Jason Carreira
 * Created Jun 15, 2003 5:19:13 PM
 */
public class DefaultActionProxyFactory extends ActionProxyFactory {
    //~ Constructors ///////////////////////////////////////////////////////////

    public DefaultActionProxyFactory() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception {
        return new DefaultActionInvocation(actionProxy);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception {
        return new DefaultActionInvocation(actionProxy, extraContext);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
        return new DefaultActionInvocation(actionProxy, extraContext, pushAction);
    }

    /**
    * Use this method to build an DefaultActionProxy instance.
    */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception {
        return new DefaultActionProxy(namespace, actionName, extraContext, true);
    }

    /**
    * Use this method to build an DefaultActionProxy instance.
    */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult) throws Exception {
        return new DefaultActionProxy(namespace, actionName, extraContext, executeResult);
    }
}
