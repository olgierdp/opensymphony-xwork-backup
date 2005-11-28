/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jepjep
 * @author Rainer Hermanns
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationParameter {

	String name();

	String value();

}
