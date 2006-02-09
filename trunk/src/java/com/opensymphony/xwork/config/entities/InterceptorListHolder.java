/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import com.opensymphony.xwork.interceptor.Interceptor;

import java.util.List;
import java.io.Serializable;


/**
 * InterceptorListHolder
 *
 * @author Jason Carreira
 *         Created Jun 1, 2003 1:02:48 AM
 */
public interface InterceptorListHolder {

    void addInterceptor(Interceptor interceptor);

    void addInterceptors(List interceptors);
}
