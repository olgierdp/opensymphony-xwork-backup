/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.annotations;

import java.lang.annotation.*;

/**
 * Marks a action method that needs to be executed before the main action method.
 *
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Before {

}