/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class DefaultActionInvocation implements ActionInvocation {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log LOG = LogFactory.getLog(DefaultActionInvocation.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    protected Action action;
    protected ActionProxy proxy;
    ActionContext nestedContext;
    Iterator interceptors;
    Map extraContext;
    OgnlValueStack stack;
    Result result;
    String resultCode;
    boolean executed = false;
    boolean pushAction = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    protected DefaultActionInvocation(ActionProxy proxy) throws Exception {
        this(proxy, null);
    }

    protected DefaultActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        this(proxy, extraContext, true);
    }

    protected DefaultActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        this.proxy = proxy;
        this.extraContext = extraContext;
        this.pushAction = pushAction;
        init();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Action getAction() {
        return action;
    }

    public boolean isExecuted() {
        return executed;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    /**
     * If the DefaultActionInvocation has been executed before and the Result is an instance of ActionChainResult, this method
     * will walk down the chain of ActionChainResults until it finds a non-chain result, which will be returned. If the
     * DefaultActionInvocation's result has not been executed before, the Result instance will be created and populated with
     * the result params.
     * @return a Result instance
     * @throws Exception
     */
    public Result getResult() throws Exception {
        if (result != null) {
            Result returnResult = result;

            // If we've chained to other Actions, we need to find the last result
            while (returnResult instanceof ActionChainResult) {
                ActionProxy aProxy = ((ActionChainResult) returnResult).getProxy();

                if (aProxy != null) {
                    Result proxyResult = aProxy.getInvocation().getResult();

                    if ((proxyResult != null) && (aProxy.getExecuteResult())) {
                        returnResult = proxyResult;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

            return returnResult;
        } else {
            Map results = proxy.getConfig().getResults();
            ResultConfig resultConfig = (ResultConfig) results.get(resultCode);

            if (resultConfig != null) {
                Class resultClass = resultConfig.getClazz();

                if (resultClass != null) {
                    try {
                        result = (Result) resultClass.newInstance();
                    } catch (Exception e) {
                        LOG.error("There was an exception while instantiating the result of type " + resultClass, e);
                        throw e;
                    }

                    OgnlUtil.setProperties(resultConfig.getParams(), result, ActionContext.getContext().getContextMap());
                }
            }

            return result;
        }
    }

    public String getResultCode() {
        return resultCode;
    }

    public OgnlValueStack getStack() {
        return stack;
    }

    public String invoke() throws Exception {
        if (executed) {
            throw new IllegalStateException("Action has already executed");
        }

        if (interceptors.hasNext()) {
            Interceptor interceptor = (Interceptor) interceptors.next();
            resultCode = interceptor.intercept(this);
        } else {
            if (proxy.getConfig().getMethodName() == null) {
                resultCode = getAction().execute();
            } else {
                resultCode = invokeAction(getAction(), proxy.getConfig());
            }
        }

        if (!executed) {
            // now execute the result, if we're supposed to
            if (proxy.getExecuteResult()) {
                executeResult();
            }

            executed = true;
        }

        return resultCode;
    }

    protected void createAction() {
        // load action
        try {
            action = (Action) proxy.getConfig().getClazz().newInstance();
        } catch (Exception e) {
            String gripe = "";
            if (proxy == null) {
                gripe = "Whoa!  No ActionProxy instance found in current ActionInvocation.  This is bad ... very bad";
            } else if (proxy.getConfig() == null) {
                gripe = "Sheesh.  Where'd that ActionProxy get to?  I can't find it in the current ActionInvocation!?";
            } else if (proxy.getConfig().getClazz() == null) {
                gripe = "No Action defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            } else {
                gripe = "Unable to instantiate Action, " + proxy.getConfig().getClazz().getName() +
                        ",  defined for '" + proxy.getActionName() +
                        "' in namespace '" + proxy.getNamespace() + "'";
            }

            gripe += " -- " + e.getMessage();
            throw new IllegalArgumentException(gripe);
        }
    }

    protected Map createContextMap() {
        Map contextMap;

        if ((extraContext != null) && (extraContext.containsKey(ActionContext.VALUE_STACK))) {
            // In case the ValueStack was passed in
            stack = (OgnlValueStack) extraContext.get(ActionContext.VALUE_STACK);

            if (stack == null) {
                throw new IllegalStateException("There was a null Stack set into the extra params.");
            }

            contextMap = stack.getContext();
        } else {
            // create the value stack
            stack = new OgnlValueStack();

            // create the action context
            contextMap = stack.getContext();
        }

        // put extraContext in
        if (extraContext != null) {
            contextMap.putAll(extraContext);
        }

        if (pushAction) {
            stack.push(action);
        }

        //put this DefaultActionInvocation into the context map
        contextMap.put(ActionContext.ACTION_INVOCATION, this);

        return contextMap;
    }

    /**
     * Uses getResult to get the final Result and executes it
     */
    private void executeResult() throws Exception {
        Result aResult = getResult();

        if (aResult != null) {
            aResult.execute(this);
        }
    }

    private void init() throws Exception {
        createAction();

        Map contextMap = createContextMap();
        ActionContext context = new ActionContext(contextMap);
        ActionContext.setContext(context);

        // get a new List so we don't get problems with the iterator if someone changes the list
        List interceptorList = new ArrayList(proxy.getConfig().getInterceptors());

        if (interceptorList != null) {
            interceptors = interceptorList.iterator();
        }
    }

    private String invokeAction(Action action, ActionConfig actionConfig) throws Exception {
        LOG.info("Executing action method = " + actionConfig.getMethodName());

        try {
            Method method = actionConfig.getMethod();

            if (action instanceof Proxy) {
                try {
                    return (String) Proxy.getInvocationHandler(action).invoke(action, method, new Object[0]);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    throw new Exception("Error invoking on proxy: " + throwable.getMessage());
                }
            } else {
                return (String) method.invoke(action, new Object[0]);
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method '" + actionConfig.getMethodName() + "()' is not defined in action '" + getAction().getClass() + "'");
        } catch (InvocationTargetException e) {
            // We try to return the source exception.
            Throwable t = e.getTargetException();

            if (t instanceof Exception) {
                throw (Exception) t;
            } else {
                throw e;
            }
        }
    }
}
