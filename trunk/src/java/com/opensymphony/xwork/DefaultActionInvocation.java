/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;


/**
 * The Default ActionInvocation implementation
 *
 * @author $Author$
 * @version $Revision$
 * @see com.opensymphony.xwork.DefaultActionProxy
 */
public class DefaultActionInvocation implements ActionInvocation {
    private static final Log LOG = LogFactory.getLog(DefaultActionInvocation.class);

    protected Object action;
    protected ActionProxy proxy;
    protected List preResultListeners;
    protected Map extraContext;
    protected ActionContext invocationContext;
    protected Iterator interceptors;
    protected OgnlValueStack stack;
    protected Result result;
    protected String resultCode;
    protected boolean executed = false;
    protected boolean pushAction = true;

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

    public Object getAction() {
        return action;
    }

    public boolean isExecuted() {
        return executed;
    }

    public ActionContext getInvocationContext() {
        return invocationContext;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    /**
     * If the DefaultActionInvocation has been executed before and the Result is an instance of ActionChainResult, this method
     * will walk down the chain of ActionChainResults until it finds a non-chain result, which will be returned. If the
     * DefaultActionInvocation's result has not been executed before, the Result instance will be created and populated with
     * the result params.
     *
     * @return a Result instance
     * @throws Exception
     */
    public Result getResult() throws Exception {
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
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        if (isExecuted())
            throw new IllegalStateException("Result has already been executed.");

        this.resultCode = resultCode;
    }


    public OgnlValueStack getStack() {
        return stack;
    }

    /**
     * Register a com.opensymphony.xwork.interceptor.PreResultListener to be notified after the Action is executed and before the
     * Result is executed. The ActionInvocation implementation must guarantee that listeners will be called in the order
     * in which they are registered. Listener registration and execution does not need to be thread-safe.
     *
     * @param listener
     */
    public void addPreResultListener(PreResultListener listener) {
        if (preResultListeners == null) {
            preResultListeners = new ArrayList(1);
        }

        preResultListeners.add(listener);
    }

    public Result createResult() throws Exception {
        ActionConfig config = proxy.getConfig();
        Map results = config.getResults();

        ResultConfig resultConfig;
        synchronized (config) {
            resultConfig = (ResultConfig) results.get(resultCode);
        }

        Result newResult = null;

        if (false) {
            // no result mapped -- that's OK. we'll just assume it is just short-hand notation
            // ie: redirect:foo.jsp or test.ftl
            PackageConfig pc = ConfigurationManager.getConfiguration().getPackageConfig(config.getPackageName());
            String resultType = pc.getFullDefaultResultType();

            Map params = Collections.EMPTY_MAP;
            int colon = resultCode.indexOf(':');
            if (colon != -1) {
                resultType = resultCode.substring(0, colon);
            }
            ResultTypeConfig rtc = (ResultTypeConfig) pc.getAllResultTypeConfigs().get(resultType);

            String defaultParam = resultCode.substring(colon + 1);
            if (defaultParam != null) {
                String paramName = (String) rtc.getClazz().getField("DEFAULT_PARAM").get(null);
                if (paramName != null) {
                    params = Collections.singletonMap(paramName, defaultParam);
                }
            }

            resultConfig = new ResultConfig(resultCode, rtc.getClazz(), params);

            synchronized (config) {
                config.addResultConfig(resultConfig);
            }
        }

        if (resultConfig != null) {
            try {
                return ObjectFactory.getObjectFactory().buildResult(resultConfig);
            } catch (Exception e) {
                LOG.error("There was an exception while instantiating the result of type " + resultConfig.getClassName(), e);
                throw e;
            }
        } else {
            return null;
        }
    }

    public String invoke() throws Exception {
        if (executed) {
            throw new IllegalStateException("Action has already executed");
        }

        if (interceptors.hasNext()) {
            Interceptor interceptor = (Interceptor) interceptors.next();
            resultCode = interceptor.intercept(this);
        } else {
            resultCode = invokeActionOnly();
        }

        // this is needed because the result will be executed, then control will return to the Interceptor, which will
        // return above and flow through again
        if (!executed) {
            if (preResultListeners != null) {
                for (Iterator iterator = preResultListeners.iterator();
                     iterator.hasNext();) {
                    PreResultListener listener = (PreResultListener) iterator.next();
                    listener.beforeResult(this, resultCode);
                }
            }

            // now execute the result, if we're supposed to
            if (proxy.getExecuteResult()) {
                executeResult();
            }

            executed = true;
        }

        return resultCode;
    }

    public String invokeActionOnly() throws Exception {
        return invokeAction(getAction(), proxy.getConfig());
    }

    protected void createAction() {
        // load action
        try {
            action = ObjectFactory.getObjectFactory().buildBean(proxy.getConfig().getClassName());
        } catch (InstantiationException e) {
            throw new XworkException("Unable to intantiate Action!", e);
        } catch (IllegalAccessException e) {
            throw new XworkException("Illegal access to constructor, is it public?", e);
        } catch (Exception e) {
            String gripe = "";

            if (proxy == null) {
                gripe = "Whoa!  No ActionProxy instance found in current ActionInvocation.  This is bad ... very bad";
            } else if (proxy.getConfig() == null) {
                gripe = "Sheesh.  Where'd that ActionProxy get to?  I can't find it in the current ActionInvocation!?";
            } else if (proxy.getConfig().getClassName() == null) {
                gripe = "No Action defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            } else {
                gripe = "Unable to instantiate Action, " + proxy.getConfig().getClassName() + ",  defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            }

            gripe += (((" -- " + e.getMessage()) != null) ? e.getMessage() : " [no message in exception]");
            throw new XworkException(gripe, e);
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
            // this also adds the ValueStack to its context
            stack = new OgnlValueStack();

            // create the action context
            contextMap = stack.getContext();
        }

        // put extraContext in
        if (extraContext != null) {
            contextMap.putAll(extraContext);
        }

        //put this DefaultActionInvocation into the context map
        contextMap.put(ActionContext.ACTION_INVOCATION, this);

        return contextMap;
    }

    /**
     * Uses getResult to get the final Result and executes it
     */
    private void executeResult() throws Exception {
        result = createResult();

        if (result != null) {
            result.execute(this);
        } else if (!Action.NONE.equals(resultCode)) {
            LOG.warn("No result defined for action " + getAction().getClass().getName() + " and result " + getResultCode());
        }
    }

    private void init() throws Exception {
        Map contextMap = createContextMap();

        createAction();

        if (pushAction) {
            stack.push(action);
        }

        invocationContext = new ActionContext(contextMap);
        invocationContext.setName(proxy.getActionName());

        // get a new List so we don't get problems with the iterator if someone changes the list
        List interceptorList = new ArrayList(proxy.getConfig().getInterceptors());
        interceptors = interceptorList.iterator();
    }

    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        String methodName = proxy.getMethod();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing action method = " + actionConfig.getMethodName());
        }

        try {
            Method method;
            try {
                method = getAction().getClass().getMethod(methodName, new Class[0]);
            } catch (NoSuchMethodException e) {
                // hmm -- OK, try doXxx instead
                try {
                    String altMethodName = "do" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                    method = getAction().getClass().getMethod(altMethodName, new Class[0]);
                } catch (NoSuchMethodException e1) {
                    // throw the original one
                    throw e;
                }
            }

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
            throw new IllegalArgumentException("Neither " + methodName + "() nor it's doXxx() equivalent is defined in action " + getAction().getClass() + "");
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
