package com.opensymphony.xwork.util;

/**
 * User: plightbo
 * Date: Sep 20, 2005
 * Time: 7:41:50 PM
 */
public @interface KeyProperty {
    String value() default "id";
}
