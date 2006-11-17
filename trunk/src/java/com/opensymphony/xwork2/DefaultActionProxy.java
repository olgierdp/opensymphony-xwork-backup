/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;


/**
 * The Default ActionProxy implementation
 *
 * @author Rainer Hermanns
 * @author Revised by <a href="mailto:hu_pengfei@yahoo.com.cn">Henry Hu</a>
 * @author tmjee
 * 
 * @version $Date$ $Id$
 * @since 2005-8-6
 */
public class DefaultActionProxy implements ActionProxy, Serializable {
	
	private static final long serialVersionUID = 3293074152487468527L;

	private static final Log LOG = LogFactory.getLog(DefaultActionProxy.class);

    protected Configuration configuration;
    protected ActionConfig config;
    protected ActionInvocation invocation;
    protected Map extraContext;
    protected String actionName;
    protected String namespace;
    protected String method;
    protected boolean executeResult;
    protected boolean cleanupContext;

    protected ObjectFactory objectFactory;

    /**
     * This constructor is private so the builder methods (create*) should be used to create an DefaultActionProxy.
     * <p/>
     * The reason for the builder methods is so that you can use a subclass to create your own DefaultActionProxy instance
     * (like a RMIActionProxy).
     */
    protected DefaultActionProxy(ObjectFactory objectFactory, Configuration cfg, String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
    	this.objectFactory = objectFactory;
        String profileKey = "create DefaultActionProxy: ";
    	try {
    		UtilTimerStack.push(profileKey);
    		
    		this.cleanupContext = cleanupContext;
    		if (LOG.isDebugEnabled()) {
    			LOG.debug("Creating an DefaultActionProxy for namespace " + namespace + " and action name " + actionName);
    		}

    		this.configuration = cfg;
    		this.actionName = actionName;
    		this.namespace = namespace;
    		this.executeResult = executeResult;
    		this.extraContext = extraContext;

    		config = configuration.getRuntimeConfiguration().getActionConfig(namespace, actionName);

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
    	finally {
    		UtilTimerStack.pop(profileKey);
    	}
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
        ActionContext nestedContext = ActionContext.getContext();
        ActionContext.setContext(invocation.getInvocationContext());

        String retCode = null;

        String profileKey = "execute: ";
        try {
        	UtilTimerStack.push(profileKey);
        	
            retCode = invocation.invoke();
        } finally {
            if (cleanupContext) {
                ActionContext.setContext(nestedContext);
            }
            UtilTimerStack.pop(profileKey);
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
        invocation = new DefaultActionInvocation(objectFactory, this, extraContext);
        resolveMethod();
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
}
