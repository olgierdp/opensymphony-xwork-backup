/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.conversion.annotations;

/**
 * <code>ConversionType</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public enum ConversionType {


    APPLICATION, CLASS;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }

}
