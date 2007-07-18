/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;


/**
 * Provides an interface in which a call for a validation check can be done.
 *
 * @author Jason Carreira
 * @see ActionSupport
 * @see com.opensymphony.xwork.interceptor.DefaultWorkflowInterceptor
 */
public interface Validateable {

    /**
     * Performs validation.
     */
    void validate();

}
