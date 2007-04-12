package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.config.annotations.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.config.providers.InterceptorBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of XWork's ConfigurationProvider which read configuration from annotation.
 *
 * @see com.opensymphony.xwork.config.ConfigurationProvider
 */
public class XWorkAnnotationConfigurationProvider implements ConfigurationProvider {
    public static String DEFAULT_FILENAME = "xwork-actions.conf";

    private String filename;

    private Log log = LogFactory.getLog(XWorkAnnotationConfigurationProvider.class);

    /**
     * Creates a new XWorkAnnotationConfigurationProvider object.
     */
    public XWorkAnnotationConfigurationProvider() {
        this(DEFAULT_FILENAME);
    }

    /**
     * Creates a new XWorkAnnotationConfigurationProvider object.
     *
     * @param filename name of resource to read for list of Action class
     */
    public XWorkAnnotationConfigurationProvider(String filename) {
        this.filename = filename;
    }

    /**
     * @see com.opensymphony.xwork.config.ConfigurationProvider#destroy()
     */
    public void destroy() {
    }

    /**
     * @see com.opensymphony.xwork.config.ConfigurationProvider#init(com.opensymphony.xwork.config.Configuration)
     */
    public void init(Configuration config) throws ConfigurationException {
        InputStream in = getResourceAsStream(filename);

        if (in == null) {
            throw new ConfigurationException("no resource with named '" + filename
                    + "' found in the classpath");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                try {
                    Class actionClass = Class.forName(line);

                    if ((actionClass.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
                        log.debug("Skipped. Class '" + line + "' is abstract");

                        continue;
                    }

                    if (!com.opensymphony.xwork.Action.class
                            .isAssignableFrom(actionClass)) {
                        log.debug("Skipped. Class '" + line
                                + "' does not implement 'com.opensymphony.xwork.Action'");

                        continue;
                    }

                    Action action = (Action) actionClass.getAnnotation(Action.class);

                    if ((action != null) && !isEmpty(action.name())) {
                        processActionInType(actionClass, config);
                    } else {
                        processActionInMethod(actionClass, config);
                    }
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("No class named '" + line
                            + "' found.'");
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param resourceName DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private InputStream getResourceAsStream(String resourceName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        return loader.getResourceAsStream(resourceName);
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private boolean isEmpty(String string) {
        return ((string == null) || "".equals(string.trim()));
    }

    /**
     * process annotation which exist in class level
     *
     * @param actionClass class instance to process
     * @param config      configuration instance
     */
    private void processActionInType(Class actionClass, Configuration config) {
        Action action = (Action) actionClass.getAnnotation(Action.class);

        PackageConfig pkgConfig = buildPackageConfig(config, action);

        Results results = (Results) actionClass.getAnnotation(Results.class);
        Map<String, ResultConfig> resultConfigs = buildResults(results, pkgConfig);

        InterceptorRefs interceptorRefs = (InterceptorRefs) actionClass.getAnnotation(InterceptorRefs.class);
        List<InterceptorConfig> interceptorRefConfigs = buildInterceptorRefs(interceptorRefs, pkgConfig);

        try {
            checkActionClassConstructable(actionClass);
        } catch (Exception e) {
            log.error("Action class [" + actionClass.getName()
                    + "] not found, skipping action [" + action.name() + "]", e);

            return;
        }

        ActionConfig actionConfig = new ActionConfig(null, actionClass, null, resultConfigs, interceptorRefConfigs);
        pkgConfig.addActionConfig(action.name(), actionConfig);
    }

    /**
     * build package config for the action.
     *
     * @param config           the configuration instance
     * @param action           action annotation containing information about the action to create
     * @param defaultNamespace default namespace if the action does not specify it
     * @return package config instance
     */
    private PackageConfig buildPackageConfig(Configuration config, Action action,
                                             String defaultNamespace) {
        String namespace = isEmpty(action.namespace()) ? defaultNamespace : action.namespace();
        PackageConfig pkgConfig = config.getPackageConfig(namespace);

        if (pkgConfig == null) {
            pkgConfig = new PackageConfig(namespace, namespace, false, null);
            config.addPackageConfig(pkgConfig.getName(), pkgConfig);
        }

        PackageConfig defaultPackageConfig = config.getPackageConfig("default");

        if (defaultPackageConfig != null) {
            pkgConfig.addParent(defaultPackageConfig);
        }

        return pkgConfig;
    }

    /**
     * calls buildPackageConfig(config, action, "").
     *
     * @param config the configuration instance
     * @param action action annotation containing information about the action to create
     * @return package config instance
     * @see #buildPackageConfig(Configuration,Action,String)
     */
    private PackageConfig buildPackageConfig(Configuration config, Action action) {
        return buildPackageConfig(config, action, "");
    }

    /**
     * build interceptor refs for the specified package
     *
     * @param interceptorRefs interceptorRefs annotation containing information of the interceptorRef to create
     * @param pkgConfig       the package config instance
     * @return list of interceptor config
     */
    private List<InterceptorConfig> buildInterceptorRefs(InterceptorRefs interceptorRefs, PackageConfig pkgConfig) {
        List<InterceptorConfig> list = null;

        if (interceptorRefs == null) {
            return Collections.EMPTY_LIST;
        }

        list = new ArrayList<InterceptorConfig>();

        for (InterceptorRef interceptorRef : interceptorRefs.value()) {
            List list2 = InterceptorBuilder.constructInterceptorReference(pkgConfig, interceptorRef.value(), null);
            list.addAll(list2);
        }

        return list;
    }

    /**
     * process annotation which exist in method level
     *
     * @param actionClass class instance to process
     * @param config      configuration instance
     */
    private void processActionInMethod(Class actionClass, Configuration config) {
        for (Method method : actionClass.getMethods()) {
            if (!method.isAnnotationPresent(Action.class)
                    || !checkMethodSignature(method)) {
                continue;
            }

            Action classAction = (Action) actionClass.getAnnotation(Action.class);
            Action action = method.getAnnotation(Action.class);
            PackageConfig pkgConfig = buildPackageConfig(config, action, (classAction == null) ? "" : classAction.namespace());

            Results defaultResults = (Results) actionClass.getAnnotation(Results.class);
            Map<String, ResultConfig> defaultResultConfigs = buildResults( defaultResults, pkgConfig);

            Results results = (Results) method.getAnnotation(Results.class);
            Map<String, ResultConfig> resultConfigs = buildResults(results, pkgConfig);

            // add all results which are declared in class level
            for (Entry<String, ResultConfig> entry : defaultResultConfigs.entrySet()) {
                if (resultConfigs.get(entry.getKey()) == null) {
                    resultConfigs.put(entry.getKey(), entry.getValue());
                }
            }

            InterceptorRefs interceptorRefs = (InterceptorRefs) method.getAnnotation(InterceptorRefs.class);
            List<InterceptorConfig> interceptorRefConfigs = buildInterceptorRefs(interceptorRefs, pkgConfig);

            // use interceptorRef which are declared in class level
            if (interceptorRefConfigs.size() == 0) {
                InterceptorRefs defaultInterceptorRefs = (InterceptorRefs) actionClass.getAnnotation(InterceptorRefs.class);
                interceptorRefConfigs = buildInterceptorRefs(defaultInterceptorRefs, pkgConfig);
            }

            try {
                checkActionClassConstructable(actionClass);
            } catch (Exception e) {
                log.error("Action class [" + actionClass.getName()
                        + "] not found, skipping action [" + action.name() + "]", e);

                return;
            }

            ActionConfig actionConfig = new ActionConfig(null, actionClass, null, resultConfigs, interceptorRefConfigs);
            pkgConfig.addActionConfig(action.name(), actionConfig);
        }
    }

    /**
     * Check if this action is constructable. Not very sure about this. The guys in xwork team do this too :)
     *
     * @param actionClass
     * @throws Exception
     */
    private void checkActionClassConstructable(Class actionClass)
            throws Exception {
        if (ObjectFactory.getObjectFactory().isNoArgConstructorRequired()) {
            ActionConfig actionConfig = new ActionConfig(null, actionClass, null, null, null);
            // use this for xwork 1.0
            // ObjectFactory.getObjectFactory().buildAction(actionConfig);
            ObjectFactory.getObjectFactory().buildAction(null, null, actionConfig, null);
        }
    }

    /**
     * Check whether this method is a valid xwork's "execute" method
     *
     * @param method method to check
     * @return <code>true</code> if the return type is String and have no parameter
     */
    private boolean checkMethodSignature(Method method) {
        return (String.class.equals(method.getReturnType()) && (method.getParameterTypes().length == 0));
    }

    /**
     * @return always return <code>false</code>
     * @see ConfigurationProvider#needsReload()
     */
    public boolean needsReload() {
        return false;
    }

    /**
     * build map of result configs for this package
     *
     * @param results   list result annotation containing information of the result config
     * @param pkgConfig the PackageConfig instance
     * @return map of result configs. empty map if no result created
     */
    private Map<String, ResultConfig> buildResults(Results results, PackageConfig pkgConfig) {
        Map<String, ResultConfig> resultMap = new HashMap<String, ResultConfig>();

        if (results == null) {
            return resultMap;
        }

        for (Result result : results.value()) {
            ResultTypeConfig resultTypeConfig = (ResultTypeConfig) pkgConfig.getAllResultTypeConfigs().get(result.type());

            if (resultTypeConfig == null) {
                throw new ConfigurationException(
                        "There is no result type defined for type '" + result.type()
                                + "' mapped with name '" + result.name() + "'");
            }

            Map<String, String> params = new HashMap<String, String>();

            if (result.params().length == 0) {
                try {
                    String paramName = (String) resultTypeConfig.getDefaultResultParam(); // getClazz().getField("DEFAULT_PARAM").get(null);
                    String paramValue = result.value();

                    if (paramValue != null) {
                        paramValue = paramValue.trim();
                    }

                    params.put(paramName, paramValue);
                } catch (Throwable t) {
                }
            } else {
                for (Param param : result.params()) {
                    params.put(param.name(), param.value());
                }
            }

            resultMap.put(result.name(), new ResultConfig(result.name(), resultTypeConfig.getClazz(), params));
        }

        return resultMap;
    }
}
