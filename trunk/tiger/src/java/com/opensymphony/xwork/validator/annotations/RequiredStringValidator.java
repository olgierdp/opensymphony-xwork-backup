/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.*;

/**
 * <code>RequiredStringValidator</code>
 *
 * This validator checks that a String field is not empty (i.e. non-null with a length > 0).
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredStringValidator {

    /**
     * The aliasNames are the names of the Action aliases as defined in the xwork.xml configuration for the particular Action.
     */
    String[] aliasNames() default {};

    /**
     *  Boolean property. Determines whether the String is trimmed before performing the length check.
     */
    boolean trim() default true;

    /**
     * The default error message for this validator.
     */
    String message();

    /**
     * The message key to lookup for i18n.
     */
    String key() default "";

    /**
     * The optional fieldName for SIMPLE validator types.
     */
    String fieldName() default "";

    /**
     * If this is activated, the validator will be used as short-circuit.
     *
     * Adds the short-circuit="true" attribute value if <tt>true</tt>.
     *
     */
    boolean shortCircuit() default false;

    /**
     * The validation type for this field/method.
     */
    ValidatorType type() default ValidatorType.FIELD;

}
