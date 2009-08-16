/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;


/**
 * ModelDriven Actions provide a model object to be pushed onto the ValueStack
 * in addition to the Action itself, allowing a FormBean type approach like Struts.
 *
 * @author Jason Carreira
 */
public interface ModelDriven<T> {

    /**
     * Gets the model to be pushed onto the ValueStack instead of the Action itself.
     *
     * @return the model
     */
    T getModel();

}
