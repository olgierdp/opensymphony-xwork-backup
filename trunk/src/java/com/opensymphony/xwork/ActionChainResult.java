/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A special kind of view that invokes GenericDispatch (using the previously existing
 * ActionContext) and executes another action. This view takes one required parameter:
 * <ul>
 *  <li><b>actionName</b> - the name of the action that will be chained to</li>
 * </ul>
 *
 * @author $Author$
 * @version $Revision$
 */
public class ActionChainResult implements Result {
    
    private static final Log log = LogFactory.getLog(ActionChainResult.class);
    public static final String DEFAULT_PARAM = "actionName";
    private static final String CHAIN_HISTORY = "CHAIN_HISTORY";

    //~ Instance fields ////////////////////////////////////////////////////////

    private ActionProxy proxy;
    private String actionName;

    /**
     * used to determine which namespace the Action is in that we're chaining
     * to.  if namespace is null, this defaults to the current namespace.
     */
    private String namespace;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * sets the namespace of the Action that we're chaining to.  if namespace
     * is null, this defaults to the current namespace.
     * @param namespace the name of the namespace we're chaining to
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ActionChainResult)) {
            return false;
        }

        final ActionChainResult actionChainResult = (ActionChainResult) o;

        if ((actionName != null) ? (!actionName.equals(actionChainResult.actionName)) : (actionChainResult.actionName != null)) {
            return false;
        }

        return true;
    }

    /**
     * @param invocation the DefaultActionInvocation calling the action call stack
     */
    public void execute(ActionInvocation invocation) throws Exception {
        
        if (isInChainHistory(namespace, actionName)) {
            throw new XworkException("infinite recursion detected");
        }
        addToHistory(namespace, actionName);

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.VALUE_STACK, ActionContext.getContext().getValueStack());
        extraContext.put(ActionContext.PARAMETERS, ActionContext.getContext().getParameters());
        extraContext.put("com.opensymphony.xwork.interceptor.component.ComponentManager", ActionContext.getContext().get("com.opensymphony.xwork.interceptor.component.ComponentManager"));
        extraContext.put(CHAIN_HISTORY, ActionContext.getContext().get(CHAIN_HISTORY));

        if (log.isDebugEnabled()) {
            log.debug("Chaining to action " + actionName);
        }

        // if the namespace wasn't explicitly defined, assume the current one
        if (this.namespace == null) {
            this.namespace = invocation.getProxy().getNamespace();
        }

        proxy = ActionProxyFactory.getFactory().createActionProxy(this.namespace, actionName, extraContext);
        proxy.execute();

    }

    public int hashCode() {
        return ((actionName != null) ? actionName.hashCode() : 0);
    }

    private boolean isInChainHistory(String namespace, String actionName) {
        Set chainHistory = (Set) ActionContext.getContext().get(CHAIN_HISTORY);
        if (chainHistory == null) {
            return false;
        } else {
            return chainHistory.contains(makeKey(namespace, actionName));
        }
    }
    private void addToHistory(String namespace, String actionName) {
        Set chainHistory = (Set) ActionContext.getContext().get(CHAIN_HISTORY);
        if (chainHistory == null) {
        	chainHistory = new HashSet();
		}
		ActionContext.getContext().put(CHAIN_HISTORY, chainHistory);
		
		chainHistory.add(makeKey(namespace, actionName));
    }
    
    private String makeKey(String namespace, String actionName) {
        return namespace + "/" + actionName;
    }
}
