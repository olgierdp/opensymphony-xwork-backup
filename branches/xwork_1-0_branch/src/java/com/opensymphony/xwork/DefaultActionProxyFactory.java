/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.ActionConfig;

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
        setupConfigIfActionIsCommand(namespace, actionName);

        return new DefaultActionProxy(namespace, actionName, extraContext, true);
    }

    /**
    * Use this method to build an DefaultActionProxy instance.
    */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult) throws Exception {
        setupConfigIfActionIsCommand(namespace, actionName);

        return new DefaultActionProxy(namespace, actionName, extraContext, executeResult);
    }

    private void setupConfigIfActionIsCommand(String namespace, String actionName) {
        if (ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfig(namespace, actionName) != null) {
            return;
        }

        int bang = actionName.indexOf('!');

        if (bang != -1) {
            String realAction = actionName.substring(0, bang);
            String command = actionName.substring(bang + 1);

            ActionConfig actionConfig = ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfig(namespace, realAction);
            ActionConfig newConfig = new ActionConfig(command, actionConfig.getClassName(), actionConfig.getParams(), actionConfig.getResults(), actionConfig.getInterceptors(), actionConfig.getExternalRefs(), actionConfig.getPackageName());

            ConfigurationManager.getConfiguration().getPackageConfig(newConfig.getPackageName()).addActionConfig(actionName, newConfig);
            ConfigurationManager.getConfiguration().rebuildRuntimeConfiguration();
        }
    }
}
