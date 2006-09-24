/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Adds the ability to set a model, probably retrieved from a given state.
 */
public interface ScopedModelDriven<T> extends ModelDriven<T> {

    /**
     * Sets the model
     */
    void setModel(T model);
}
