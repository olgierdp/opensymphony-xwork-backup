/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.util.Set;

import com.opensymphony.xwork2.util.TextParseUtil;

/**
 * Utility class contains common methods used by 
 * {@link com.opensymphony.xwork2.interceptor.MethodFilterInterceptor}.
 * 
 * @author tm_jee
 */
public class MethodFilterInterceptorUtil {

	/**
     * Static method to decide if the specified <code>method</code> should be
     * apply (not filtered) depending on the set of <code>excludeMethods</code> and 
     * <code>includeMethods</code>. 
     *
     * <ul>
     * <li>
     * 	<code>excludeMethods</code> takes precedence over <code>includeMethods</code>
     * </li>
     * </ul>
     * <b>Note:</b> Asterict '*' could be used to indicate all methods.
     *
     * @param excludeMethods  list of methods to exclude.
     * @param includeMethods  list of methods to include.
     * @param method the specified method to check
     * @return <tt>true</tt> if the method should be applied.
     */
    public static boolean applyMethod(Set excludeMethods, Set includeMethods, String method) {
    	if (((excludeMethods.contains("*") && !includeMethods.contains("*"))
                || excludeMethods.contains(method))
                && !includeMethods.contains(method)) {
            return false;
        }
        return includeMethods.size() == 0 || includeMethods.contains(method) || includeMethods.contains("*");
    }
    
    /**
     * Same as {@link #applyMethod(Set, Set, String)}, except that <code>excludeMethods</code>
     * and <code>includeMethods</code> are supplied as comma separated string.
     * 
     * @param excludeMethods  comma seperated string of methods to exclude.
     * @param includeMethods  comma seperated string of methods to include.
     * @param method the specified method to check
     * @return <tt>true</tt> if the method should be applied.
     */
    public static boolean applyMethod(String excludeMethods, String includeMethods, String method) {
    	Set includeMethodsSet = TextParseUtil.commaDelimitedStringToSet(includeMethods == null? "" : includeMethods);
    	Set excludeMethodsSet = TextParseUtil.commaDelimitedStringToSet(excludeMethods == null? "" : excludeMethods);
    	
    	return applyMethod(excludeMethodsSet, includeMethodsSet, method);
    }

}
