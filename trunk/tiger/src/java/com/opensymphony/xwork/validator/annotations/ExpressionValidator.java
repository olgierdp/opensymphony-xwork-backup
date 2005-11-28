/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.*;

/**
 * <code>ExpressionValidator</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface ExpressionValidator {

    /**
     * The aliasNames are the names of the Action aliases as defined in the xwork.xml configuration for the particular Action.
     */
    String[] aliasNames() default {};

    /**
     * The expressions to validate.
     * An OGNL expression that returns a boolean value.
     */
    String expression();

    /**
     * The default error messages for this validator.
     */
    String message();

    /**
     * The message key to lookup for i18n.
     */
    String key() default "";

    /**
     * If this is activated, the validator will be used as short-circuit.
     *
     * Adds the short-circuit="true" attribute value if <tt>true</tt>.
     *
     */
    boolean shortCircuit() default false;

}
