/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.conversion.annotations;

/**
 * <code>ConversionRule</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public enum ConversionRule {

    PROPERTY, COLLECTION, MAP, KEY, KEY_PROPERTY, ELEMENT;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}

