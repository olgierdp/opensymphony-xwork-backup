/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;


/**
 * @author joew@thoughtworks.com
 * @author $Author$
 * @version $Revision$
 */
public interface ComponentManager {
    //~ Instance fields ////////////////////////////////////////////////////////

    String COMPONENT_MANAGER_KEY = "DefaultComponentManager";

    //~ Methods ////////////////////////////////////////////////////////////////

    Object getComponent(Class enablerType);

    void setFallback(ComponentManager fallback);

    void addEnabler(Class component, Class enablerType);

    void dispose();

    void initializeObject(Object obj);
}
