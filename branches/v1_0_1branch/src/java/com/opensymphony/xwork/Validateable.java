/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;


/**
 * Validateable provides an Interface for Actions which can be validated.
 * @author Jason Carreira
 * @see ActionSupport
 * @see com.opensymphony.xwork.interceptor.DefaultWorkflowInterceptor
 * Created Aug 29, 2003 1:42:21 PM
 */
public interface Validateable {
    //~ Methods ////////////////////////////////////////////////////////////////

    void validate();
}
