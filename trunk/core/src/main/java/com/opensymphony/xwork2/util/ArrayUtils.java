/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

/**
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 */
public class ArrayUtils {

    public static boolean isEmpty(Object[] array) {
        return null == array || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

}
