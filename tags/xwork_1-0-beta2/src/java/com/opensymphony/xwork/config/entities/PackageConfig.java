/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import com.opensymphony.util.TextUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author $Author$
 * @version $Revision$
 */
public class PackageConfig {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log LOG = LogFactory.getLog(PackageConfig.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private Map actionConfigs = new HashMap();
    private Map globalResultConfigs = new HashMap();
    private Map interceptorConfigs = new HashMap();
    private Map resultTypeConfigs = new HashMap();
    private Set parents = new HashSet();
    private String defaultInterceptorRef;
    private String defaultResultType;
    private String name;
    private String namespace = "";
    private boolean isAbstract = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    public PackageConfig() {
    }

    public PackageConfig(String name) {
        this.name = name;
    }

    public PackageConfig(String name, String namespace, boolean isAbstract) {
        this(name);
        this.namespace = TextUtils.noNull(namespace);
        this.isAbstract = isAbstract;
    }

    public PackageConfig(String name, String namespace, boolean isAbstract, List parents) {
        this(name, namespace, isAbstract);

        for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
            PackageConfig parent = (PackageConfig) iterator.next();
            addParent(parent);
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public Map getActionConfigs() {
        return actionConfigs;
    }

    /**
    * returns the Map of all the ActionConfigs available in the current package.
    * ActionConfigs defined in ancestor packages will be included in this Map.
    * @return a Map of ActionConfig Objects with the action name as the key
    * @see com.opensymphony.xwork.config.entities.ActionConfig
    */
    public Map getAllActionConfigs() {
        Map retMap = new HashMap();

        if (!parents.isEmpty()) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = (PackageConfig) iterator.next();
                retMap.putAll(parentContext.getAllActionConfigs());
            }
        }

        retMap.putAll(getActionConfigs());

        return retMap;
    }

    /**
    * returns the Map of all the global ResultConfigs available in the current package.
    * Global ResultConfigs defined in ancestor packages will be included in this Map.
    * @return a Map of Result Objects with the result name as the key
    * @see com.opensymphony.xwork.config.entities.ResultConfig
    */
    public Map getAllGlobalResults() {
        Map retMap = new HashMap();

        if (!parents.isEmpty()) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = (PackageConfig) iterator.next();
                retMap.putAll(parentContext.getAllGlobalResults());
            }
        }

        retMap.putAll(getGlobalResultConfigs());

        return retMap;
    }

    /**
    * returns the Map of all InterceptorConfigs and InterceptorStackConfigs available in the current package.
    * InterceptorConfigs defined in ancestor packages will be included in this Map.
    * @return a Map of InterceptorConfig and InterceptorStackConfig Objects with the ref-name as the key
    * @see com.opensymphony.xwork.config.entities.InterceptorConfig
    * @see com.opensymphony.xwork.config.entities.InterceptorStackConfig
    */
    public Map getAllInterceptorConfigs() {
        Map retMap = new HashMap();

        if (!parents.isEmpty()) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = (PackageConfig) iterator.next();
                retMap.putAll(parentContext.getAllInterceptorConfigs());
            }
        }

        retMap.putAll(getInterceptorConfigs());

        return retMap;
    }

    /**
    * returns the Map of all the ResultTypeConfigs available in the current package.
    * ResultTypeConfigs defined in ancestor packages will be included in this Map.
    * @return a Map of ResultTypeConfig Objects with the result type name as the key
    * @see com.opensymphony.xwork.config.entities.ResultTypeConfig
    */
    public Map getAllResultTypeConfigs() {
        Map retMap = new HashMap();

        if (!parents.isEmpty()) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parentContext = (PackageConfig) iterator.next();
                retMap.putAll(parentContext.getAllResultTypeConfigs());
            }
        }

        retMap.putAll(getResultTypeConfigs());

        return retMap;
    }

    public void setDefaultInterceptorRef(String name) {
        defaultInterceptorRef = name;
    }

    public String getDefaultInterceptorRef() {
        return defaultInterceptorRef;
    }

    /**
    * sets the default Result type for this package
    * @param defaultResultType
    */
    public void setDefaultResultType(String defaultResultType) {
        this.defaultResultType = defaultResultType;
    }

    /**
    * Returns the default result type for this package.
    */
    public String getDefaultResultType() {
        return defaultResultType;
    }

    /**
    * gets the default interceptor-ref name. If this is not set on this PackageConfig, it searches the parent
    * PackageConfigs in order until it finds one.
    * @return
    */
    public String getFullDefaultInterceptorRef() {
        if ((defaultInterceptorRef == null) && !parents.isEmpty()) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parent = (PackageConfig) iterator.next();
                String parentDefault = parent.getFullDefaultInterceptorRef();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }

        return defaultInterceptorRef;
    }

    /**
    * Returns the default result type for this package.
    *
    * If there is no default result type, but this package has parents - we will try to
    * look up the default result type of a parent.
    */
    public String getFullDefaultResultType() {
        if ((defaultResultType == null) && !parents.isEmpty()) {
            for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                PackageConfig parent = (PackageConfig) iterator.next();
                String parentDefault = parent.getFullDefaultResultType();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }

        return defaultResultType;
    }

    /**
    * gets the global ResultConfigs local to this package
    * @return a Map of ResultConfig objects keyed by result name
    * @see com.opensymphony.xwork.config.entities.ResultConfig
    */
    public Map getGlobalResultConfigs() {
        return globalResultConfigs;
    }

    /**
    * gets the InterceptorConfigs and InterceptorStackConfigs local to this package
    * @return a Map of InterceptorConfig and InterceptorStackConfig objects keyed by ref-name
    * @see com.opensymphony.xwork.config.entities.InterceptorConfig
    * @see com.opensymphony.xwork.config.entities.InterceptorStackConfig
    */
    public Map getInterceptorConfigs() {
        return interceptorConfigs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNamespace(String namespace) {
        if (namespace == null) {
            this.namespace = "";
        } else {
            this.namespace = namespace;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public List getParents() {
        return new ArrayList(parents);
    }

    /**
    * gets the ResultTypeConfigs local to this package
    * @return a Map of ResultTypeConfig objects keyed by result name
    * @see com.opensymphony.xwork.config.entities.ResultTypeConfig
    */
    public Map getResultTypeConfigs() {
        return resultTypeConfigs;
    }

    public void addActionConfig(String name, ActionConfig action) {
        actionConfigs.put(name, action);
    }

    public void addAllParents(List parents) {
        for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
            PackageConfig config = (PackageConfig) iterator.next();
            addParent(config);
        }
    }

    public void addGlobalResultConfig(ResultConfig resultConfig) {
        globalResultConfigs.put(resultConfig.getName(), resultConfig);
    }

    public void addGlobalResultConfigs(Map resultConfigs) {
        globalResultConfigs.putAll(resultConfigs);
    }

    public void addInterceptorConfig(InterceptorConfig config) {
        interceptorConfigs.put(config.getName(), config);
    }

    public void addInterceptorStackConfig(InterceptorStackConfig config) {
        interceptorConfigs.put(config.getName(), config);
    }

    public void addParent(PackageConfig parent) {
        if (this.equals(parent)) {
            LOG.error("A package cannot extend itself: " + name);
        }

        parents.add(parent);
    }

    public void addResultTypeConfig(ResultTypeConfig config) {
        resultTypeConfigs.put(config.getName(), config);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PackageConfig)) {
            return false;
        }

        final PackageConfig packageConfig = (PackageConfig) o;

        if (isAbstract != packageConfig.isAbstract) {
            return false;
        }

        if ((actionConfigs != null) ? (!actionConfigs.equals(packageConfig.actionConfigs)) : (packageConfig.actionConfigs != null)) {
            return false;
        }

        if ((defaultResultType != null) ? (!defaultResultType.equals(packageConfig.defaultResultType)) : (packageConfig.defaultResultType != null)) {
            return false;
        }

        if ((globalResultConfigs != null) ? (!globalResultConfigs.equals(packageConfig.globalResultConfigs)) : (packageConfig.globalResultConfigs != null)) {
            return false;
        }

        if ((interceptorConfigs != null) ? (!interceptorConfigs.equals(packageConfig.interceptorConfigs)) : (packageConfig.interceptorConfigs != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(packageConfig.name)) : (packageConfig.name != null)) {
            return false;
        }

        if ((namespace != null) ? (!namespace.equals(packageConfig.namespace)) : (packageConfig.namespace != null)) {
            return false;
        }

        if ((parents != null) ? (!parents.equals(packageConfig.parents)) : (packageConfig.parents != null)) {
            return false;
        }

        if ((resultTypeConfigs != null) ? (!resultTypeConfigs.equals(packageConfig.resultTypeConfigs)) : (packageConfig.resultTypeConfigs != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        // System.out.println("hashCode() + {Name:"+name+" abstract:"+isAbstract+" namespace:"+namespace+" parents: "+parents+"}");
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((parents != null) ? parents.hashCode() : 0);
        result = (29 * result) + ((actionConfigs != null) ? actionConfigs.hashCode() : 0);
        result = (29 * result) + ((globalResultConfigs != null) ? globalResultConfigs.hashCode() : 0);
        result = (29 * result) + ((interceptorConfigs != null) ? interceptorConfigs.hashCode() : 0);
        result = (29 * result) + ((resultTypeConfigs != null) ? resultTypeConfigs.hashCode() : 0);
        result = (29 * result) + ((defaultResultType != null) ? defaultResultType.hashCode() : 0);
        result = (29 * result) + ((namespace != null) ? namespace.hashCode() : 0);
        result = (29 * result) + (isAbstract ? 1 : 0);

        return result;
    }

    public void removeParent(PackageConfig parent) {
        parents.remove(parent);
    }

    public String toString() {
        return "{" + super.toString() + " Name:" + name + " abstract:" + isAbstract + " namespace:" + namespace + " parents:" + parents + "}";
    }
}
