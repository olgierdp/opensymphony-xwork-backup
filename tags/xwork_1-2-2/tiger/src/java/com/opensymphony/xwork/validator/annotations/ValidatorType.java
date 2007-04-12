/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.validator.annotations;

/**
 * <code>ValidatorType</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public enum ValidatorType {

    FIELD, SIMPLE;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
    
}
