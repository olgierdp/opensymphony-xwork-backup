/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.*;

/**
 * <code>StringLengthFieldValidator</code>
 *
 * This validator checks that a String field is of the right length. It assumes that the field is a String.
 * If neither minLength nor maxLength is set, nothing will be done.
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringLengthFieldValidator {

    /**
     * The aliasNames are the names of the Action aliases as defined in the xwork.xml configuration for the particular Action.
     */
    String[] aliasNames() default {};

    /**
     *  Boolean property. Determines whether the String is trimmed before performing the length check.
     */
    boolean trim() default true;

    /**
     *  Integer property. The minimum length the String must be.
     */
    String minLength() default "";

    /**
     *  Integer property. The maximum length the String can be.
     */
    String maxLength() default "";

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
    ValidatorType[] type() default {ValidatorType.FIELD};

}
