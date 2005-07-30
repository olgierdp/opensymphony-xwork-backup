/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import java.util.Map;


/**
 * Actions implementing Parameterizable will receive a map of the static
 * parameters defined in the Action configuration.
 * <p/>
 * The {@link com.opensymphony.xwork.interceptor.StaticParametersInterceptor}
 * must be in the Action's interceptor queue for this to work.
 *
 * @author Jason Carreira
 *         Created Jun 2, 2003 9:56:10 PM
 */
public interface Parameterizable {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void addParam(String name, Object value);

    void setParams(Map params);

    Map getParams();
}
