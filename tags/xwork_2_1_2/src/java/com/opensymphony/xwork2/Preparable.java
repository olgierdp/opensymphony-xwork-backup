/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;


/**
 * Preparable Actions will have their <code>prepare()</code> method called if the {@link com.opensymphony.xwork2.interceptor.PrepareInterceptor}
 * is applied to the ActionConfig.
 *
 * @author Jason Carreira
 * @see com.opensymphony.xwork2.interceptor.PrepareInterceptor
 */
public interface Preparable {

    /**
     * This method is called to allow the action to prepare itself.
     *
     * @throws Exception thrown if a system level exception occurs.
     */
    void prepare() throws Exception;
    
}
