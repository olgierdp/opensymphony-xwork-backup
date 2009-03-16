/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.interceptor.component.ComponentInterceptor;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * <!-- START SNIPPET: description -->
 *
 * This result invokes an entire other action, complete with it's own interceptor stack and result.
 *
 * <!-- END SNIPPET: description -->
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>actionName (default)</b> - the name of the action that will be chained to</li>
 *
 * <li><b>namespace</b> - used to determine which namespace the Action is in that we're chaining. If namespace is null,
 * this defaults to the current namespace</li>
 *
 * <li><b>method</b> - used to specify another method on target action to be invoked. 
 * If null, this defaults to execute method</li>
 * 
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;package name="public" extends="webwork-default"&gt;
 *     &lt;!-- Chain creatAccount to login, using the default parameter --&gt;
 *     &lt;action name="createAccount" class="..."&gt;
 *         &lt;result type="chain"&gt;login&lt;/result&gt;
 *     &lt;/action&gt;
 *
 *     &lt;action name="login" class="..."&gt;
 *         &lt;!-- Chain to another namespace --&gt;
 *         &lt;result type="chain"&gt;
 *             &lt;param name="actionName"&gt;dashboard&lt;/param&gt;
 *             &lt;param name="namespace"&gt;/secure&lt;/param&gt;
 *         &lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 *
 * &lt;package name="secure" extends="webwork-default" namespace="/secure"&gt;
 *     &lt;action name="dashboard" class="..."&gt;
 *         &lt;result&gt;dashboard.jsp&lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 * <!-- END SNIPPET: example --></pre>
 * 
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class ActionChainResult implements Result {

    private static final Log log = LogFactory.getLog(ActionChainResult.class);
    public static final String DEFAULT_PARAM = "actionName";
    private static final String CHAIN_HISTORY = "CHAIN_HISTORY";


    private ActionProxy proxy;
    private String actionName;

    private String namespace;
    
    private String methodName;


    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMethod(String method) {
        this.methodName = method;
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

        if ((actionName != null) ? (!actionName.equals(actionChainResult.actionName)) : (actionChainResult.actionName != null))
        {
            return false;
        }

        return true;
    }

    /**
     * @param invocation the DefaultActionInvocation calling the action call stack
     */
    public void execute(ActionInvocation invocation) throws Exception {
        // if the finalNamespace wasn't explicitly defined, assume the current one
        if (this.namespace == null) {
            this.namespace = invocation.getProxy().getNamespace();
        }

        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        String finalNamespace = TextParseUtil.translateVariables(namespace, stack);
        String finalActionName = TextParseUtil.translateVariables(actionName, stack);
        String finalMethodName = this.methodName != null 
                ? TextParseUtil.translateVariables(this.methodName, stack)
                : null;

        if (isInChainHistory(finalNamespace, finalActionName, finalMethodName)) {
            throw new XworkException("infinite recursion detected");
        }

        addToHistory(finalNamespace, finalActionName, finalMethodName);

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.VALUE_STACK, ActionContext.getContext().getValueStack());
        extraContext.put(ActionContext.PARAMETERS, ActionContext.getContext().getParameters());
        extraContext.put(ComponentInterceptor.COMPONENT_MANAGER, ActionContext.getContext().get(ComponentInterceptor.COMPONENT_MANAGER));
        extraContext.put(CHAIN_HISTORY, ActionContext.getContext().get(CHAIN_HISTORY));

        if (log.isDebugEnabled()) {
            log.debug("Chaining to action " + finalActionName);
        }

        proxy = ActionProxyFactory.getFactory().createActionProxy(finalNamespace, finalActionName, extraContext);
        if (null != finalMethodName) {
            proxy.setMethod(finalMethodName);
        }
        proxy.execute();
    }

    public int hashCode() {
        return ((actionName != null) ? actionName.hashCode() : 0);
    }

    private boolean isInChainHistory(String namespace, String actionName, String methodName) {
        Set chainHistory = (Set) ActionContext.getContext().get(CHAIN_HISTORY);

        if (chainHistory == null) {
            return false;
        } else {
            return chainHistory.contains(makeKey(namespace, actionName, methodName));
        }
    }

    private void addToHistory(String namespace, String actionName, String methodName) {
        Set chainHistory = (Set) ActionContext.getContext().get(CHAIN_HISTORY);

        if (chainHistory == null) {
            chainHistory = new HashSet();
        }

        ActionContext.getContext().put(CHAIN_HISTORY, chainHistory);

        chainHistory.add(makeKey(namespace, actionName, methodName));
    }

    private String makeKey(String namespace, String actionName, String methodName) {
        if (null == methodName) {
            return namespace + "/" + actionName;
        }
        
        return namespace + "/" + actionName + "!" + methodName;
    }
}
