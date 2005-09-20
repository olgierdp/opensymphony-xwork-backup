/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.util.TextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;


/**
 * The Default ActionProxy implementation
 *
 * @author $Author$
 * @author Revised by <a href="mailto:hu_pengfei@yahoo.com.cn">Henry Hu</a>
 * @version $Revision$
 * @since 2005-8-6
 */
public class DefaultActionProxy implements ActionProxy, Serializable {
    private static final Log LOG = LogFactory.getLog(DefaultActionProxy.class);

    protected ActionConfig config;
    protected ActionInvocation invocation;
    protected Map extraContext;
    protected String actionName;
    protected String namespace;
    protected String method;
    protected boolean executeResult;

    /**
     * This constructor is private so the builder methods (create*) should be used to create an DefaultActionProxy.
     * <p/>
     * The reason for the builder methods is so that you can use a subclass to create your own DefaultActionProxy instance
     * (like a RMIActionProxy).
     */
    protected DefaultActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating an DefaultActionProxy for namespace " + namespace + " and action name " + actionName);
        }

        this.actionName = actionName;
        this.namespace = namespace;
        this.executeResult = executeResult;
        this.extraContext = extraContext;

        config = ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfig(namespace, actionName);

        if (config == null) {
            String message;

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_PACKAGE_ACTION_EXCEPTION, Locale.getDefault(), new String[]{
                        namespace, actionName
                });
            } else {
                message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_ACTION_EXCEPTION, Locale.getDefault(), new String[]{
                        actionName
                });
            }

            throw new ConfigurationException(message);
        }

        prepare();
    }

    public Object getAction() {
        return invocation.getAction();
    }

    public String getActionName() {
        return actionName;
    }

    public ActionConfig getConfig() {
        return config;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public boolean getExecuteResult() {
        return executeResult;
    }

    public ActionInvocation getInvocation() {
        return invocation;
    }

    public String getNamespace() {
        return namespace;
    }

    public String execute() throws Exception {
        ActionContext nestedContext = null;

        String executed = ActionGlobalContext.getContext().getActionExecuted();
        if (executed != null)
            nestedContext = ActionContext.getContext();

        ActionContext.setContext(invocation.getInvocationContext());

        String retCode = null;

        try {
            retCode = invocation.invoke();
            ActionGlobalContext.getContext().setActionExecuted("Executed");
        } finally {
            if (executed != null)
                ActionContext.setContext(nestedContext);
        }

        return retCode;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
        resolveMethod();
    }

    private void resolveMethod() {
        // if the method is set to null, use the one from the configuration
        // if the one from the configuration is also null, use "execute"
        if (!TextUtils.stringSet(this.method)) {
            this.method = config.getMethodName();
            if (!TextUtils.stringSet(this.method)) {
                this.method = "execute";
            }
        }
    }

    protected void prepare() throws Exception {
        invocation = ActionProxyFactory.getFactory().createActionInvocation(this, extraContext);
        resolveMethod();
    }
}
