/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import java.util.Map;


/**
 * Parameterizable
 * @author Jason Carreira
 * Created Jun 2, 2003 9:56:10 PM
 */
public interface Parameterizable {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void addParam(String name, Object value);

    void setParams(Map params);

    Map getParams();
}
