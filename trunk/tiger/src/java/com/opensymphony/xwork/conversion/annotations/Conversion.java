/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.conversion.annotations;

import java.lang.annotation.*;

/**
 * <code>Conversion</code>
 *
 * A marker annotations for conversions at type level.
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conversion {

    /**
     * Allow Type Conversions being applied at Type level.
     */
    TypeConversion[] conversions() default {};
}
