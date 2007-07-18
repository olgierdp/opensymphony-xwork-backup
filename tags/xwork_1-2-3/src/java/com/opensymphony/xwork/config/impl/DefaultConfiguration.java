/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.impl;

import com.opensymphony.xwork.config.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.config.providers.InterceptorBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * DefaultConfiguration
 *
 * @author Jason Carreira
 *         Created Feb 24, 2003 7:38:06 AM
 */
public class DefaultConfiguration implements Configuration {

    protected static final Log LOG = LogFactory.getLog(DefaultConfiguration.class);


    // Programmatic Action Conifigurations
    private Map packageContexts = new LinkedHashMap();
    protected RuntimeConfiguration runtimeConfiguration;


    public DefaultConfiguration() {
    }


    public PackageConfig getPackageConfig(String name) {
        return (PackageConfig) packageContexts.get(name);
    }

    public Set getPackageConfigNames() {
        return packageContexts.keySet();
    }

    public Map getPackageConfigs() {
        return packageContexts;
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    public void addPackageConfig(String name, PackageConfig packageContext) {
      PackageConfig check = (PackageConfig) packageContexts.get(name);
        if (check != null) {
             LOG.error("The package name '" + name + "' is already been used by another package: " + check);
            // would be better to throw ConfigurationException("name already used");
        }
        packageContexts.put(name, packageContext);
    }

    /**
     * Allows the configuration to clean up any resources used
     */
    public void destroy() {
    }

    public void rebuildRuntimeConfiguration() {
        runtimeConfiguration = buildRuntimeConfiguration();
    }

    /**
     * Calls the ConfigurationProviderFactory.getConfig() to tell it to reload the configuration and then calls
     * buildRuntimeConfiguration().
     *
     * @throws ConfigurationException
     */
    public synchronized void reload() throws ConfigurationException {
        packageContexts.clear();

        for (Iterator iterator = ConfigurationManager.getConfigurationProviders().iterator();
             iterator.hasNext();) {
            ConfigurationProvider provider = (ConfigurationProvider) iterator.next();
            provider.init(this);
        }

        rebuildRuntimeConfiguration();
    }

    public void removePackageConfig(String name) {
        PackageConfig toBeRemoved = (PackageConfig) packageContexts.get(name);

        if (toBeRemoved != null) {
            for (Iterator iterator = packageContexts.values().iterator();
                 iterator.hasNext();) {
                PackageConfig packageContext = (PackageConfig) iterator.next();
                packageContext.removeParent(toBeRemoved);
            }
        }
    }

    /**
     * This methodName builds the internal runtime configuration used by Xwork for finding and configuring Actions from the
     * programmatic configuration data structures. All of the old runtime configuration will be discarded and rebuilt.
     */
    protected synchronized RuntimeConfiguration buildRuntimeConfiguration() throws ConfigurationException {
        Map namespaceActionConfigs = new LinkedHashMap();
        Map namespaceConfigs = new LinkedHashMap();

        for (Iterator iterator = packageContexts.values().iterator();
             iterator.hasNext();) {
            PackageConfig packageContext = (PackageConfig) iterator.next();

            if (!packageContext.isAbstract()) {
                String namespace = packageContext.getNamespace();                
                Map configs = (Map) namespaceActionConfigs.get(namespace);

                if (configs == null) {
                    configs = new LinkedHashMap();
                }

                Map actionConfigs = packageContext.getAllActionConfigs();

                for (Iterator actionIterator = actionConfigs.keySet().iterator();
                     actionIterator.hasNext();) {
                    String actionName = (String) actionIterator.next();
                    ActionConfig baseConfig = (ActionConfig) actionConfigs.get(actionName);
                    configs.put(actionName, buildFullActionConfig(packageContext, baseConfig));
                }

                namespaceActionConfigs.put(namespace, configs);
                if (packageContext.getFullDefaultActionRef() != null) {
                	namespaceConfigs.put(namespace, packageContext.getFullDefaultActionRef());
                }
            }
        }

        return new RuntimeConfigurationImpl(namespaceActionConfigs, namespaceConfigs);
    }

    private void setDefaultResults(Map results, PackageConfig packageContext) {
        String defaultResult = packageContext.getFullDefaultResultType();

        for (Iterator iterator = results.entrySet().iterator();
             iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();

            if (entry.getValue() == null) {
                ResultTypeConfig resultTypeConfig = (ResultTypeConfig) packageContext.getAllResultTypeConfigs().get(defaultResult);
                entry.setValue(resultTypeConfig.getClazz());
            }
        }
    }

    /**
     * Builds the full runtime actionconfig with all of the defaults and inheritance
     *
     * @param packageContext the PackageConfig which holds the base config we're building from
     * @param baseConfig     the ActionConfig which holds only the configuration specific to itself, without the defaults
     *                       and inheritance
     * @return a full ActionConfig for runtime configuration with all of the inherited and default params
     */
    private ActionConfig buildFullActionConfig(PackageConfig packageContext, ActionConfig baseConfig) throws ConfigurationException {
        Map params = new TreeMap(baseConfig.getParams());
        
        Map results = new TreeMap();
        if (baseConfig.getPackageName().equals(packageContext.getName())) {
        	results.putAll(packageContext.getAllGlobalResults());
        	results.putAll(baseConfig.getResults());
        }
        else {
            PackageConfig baseConfigPackageConfig = (PackageConfig) packageContexts.get(baseConfig.getPackageName());
            if ( baseConfigPackageConfig != null) {
                results.putAll(baseConfigPackageConfig.getAllGlobalResults());
            }
            results.putAll(baseConfig.getResults());
        }

        setDefaultResults(results, packageContext);

        List interceptors = new ArrayList(baseConfig.getInterceptors());
        
        if (interceptors.size() <= 0) {
            String defaultInterceptorRefName = packageContext.getFullDefaultInterceptorRef();

            if (defaultInterceptorRefName != null) {
                interceptors.addAll(InterceptorBuilder.constructInterceptorReference(packageContext, defaultInterceptorRefName, new LinkedHashMap()));
            }
        }

        List externalRefs = baseConfig.getExternalRefs();

        List exceptionMappings = baseConfig.getExceptionMappings();
        exceptionMappings.addAll(packageContext.getAllExceptionMappingConfigs());

        ActionConfig config = new ActionConfig(baseConfig.getMethodName(), baseConfig.getClassName(), params, results, interceptors, externalRefs, exceptionMappings, packageContext.getName());

        return config;
    }


    private class RuntimeConfigurationImpl implements RuntimeConfiguration {
        private Map namespaceActionConfigs;
        private Map namespaceConfigs;

        public RuntimeConfigurationImpl(Map namespaceActionConfigs, Map namespaceConfigs) {
            this.namespaceActionConfigs = namespaceActionConfigs;
            this.namespaceConfigs = namespaceConfigs;
        }
        
        

        /**
         * Gets the configuration information for an action name, or returns null if the
         * name is not recognized.
         *
         * @param name      the name of the action
         * @param namespace the namespace for the action or null for the empty namespace, ""
         * @return the configuration information for action requested
         */
        public synchronized ActionConfig getActionConfig(String namespace, String name) {
            ActionConfig config = null;
            Map actions = (Map) namespaceActionConfigs.get((namespace == null) ? "" : namespace);

            if (actions != null) {
                config = (ActionConfig) actions.get(name);
                // fail over to default action
                if (config == null) {
                	String defaultActionRef = (String) namespaceConfigs.get((namespace == null) ? "" : namespace);
                	if (defaultActionRef != null) {
                		config = (ActionConfig) actions.get(defaultActionRef);
                	}
                }
            }

            // fail over to empty namespace
            if ((config == null) && (namespace != null) && (!namespace.trim().equals(""))) {
                actions = (Map) namespaceActionConfigs.get("");

                if (actions != null) {
                    config = (ActionConfig) actions.get(name);
                    // fail over to default action
                    if (config == null) {
                    	String defaultActionRef = (String) namespaceConfigs.get("");
                    	if (defaultActionRef != null) {
                    		config = (ActionConfig) actions.get(defaultActionRef);
                    	}
                    }
                }
            }
            

            return config;
        }

        /**
         * Gets the configuration settings for every action.
         *
         * @return a Map of namespace - > Map of ActionConfig objects, with the key being the action name
         */
        public synchronized Map getActionConfigs() {
            return namespaceActionConfigs;
        }

        public String toString() {
            StringBuffer buff = new StringBuffer("RuntimeConfiguration - actions are\n");

            for (Iterator iterator = namespaceActionConfigs.keySet().iterator();
                 iterator.hasNext();) {
                String namespace = (String) iterator.next();
                Map actionConfigs = (Map) namespaceActionConfigs.get(namespace);

                for (Iterator iterator2 = actionConfigs.keySet().iterator();
                     iterator2.hasNext();) {
                    buff.append(namespace + "/" + iterator2.next() + "\n");
                }
            }

            return buff.toString();
        }
    }
}
