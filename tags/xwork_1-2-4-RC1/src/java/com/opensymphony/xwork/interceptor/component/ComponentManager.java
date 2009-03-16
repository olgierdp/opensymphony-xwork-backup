/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;


/**
 * @author joew@thoughtworks.com
 * @author $Author$
 * @version $Revision$
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-WebWork integration documentation for more info.
 */
public interface ComponentManager {

    String COMPONENT_MANAGER_KEY = "DefaultComponentManager";


    Object getComponent(Class enablerType);

    void setFallback(ComponentManager fallback);

    void addEnabler(Class component, Class enablerType);

    void dispose();

    void initializeObject(Object obj);

    void registerInstance(Class componentType, Object instance);

    Object getComponentInstance(Class componentType);

    ComponentConfiguration getConfig();

    void setConfig(ComponentConfiguration config);

    void setScope(String scope);

    void reset();
}
