/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Actions implementing Parameterizable will receive a map of the static parameters defined in the action
 * configuration.
 *
 * <p/> The {@link com.opensymphony.xwork.interceptor.StaticParametersInterceptor} must be in the action's interceptor
 * queue for this to work.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Jason Carreira
 */
public interface Parameterizable {

    public void addParam(String name, Object value);

    void setParams(Map params);

    Map getParams();
}
