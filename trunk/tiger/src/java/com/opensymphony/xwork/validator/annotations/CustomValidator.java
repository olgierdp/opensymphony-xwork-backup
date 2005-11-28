/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.*;

/**
 * <code>CustomValidator</code>
 *
 * @author jepjep
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValidator {


    /**
     * The aliasNames are the names of the Action aliases as defined in the xwork.xml configuration for the particular Action.
     */
    String[] aliasNames() default {};    

    String type();

    /**
     * The optional fieldName for SIMPLE validator types.
     */
    String fieldName() default "";

    String message() default "";

    String key() default "";

    public ValidationParameter[] parameters() default {};

    boolean shortCircuit() default false;

}
