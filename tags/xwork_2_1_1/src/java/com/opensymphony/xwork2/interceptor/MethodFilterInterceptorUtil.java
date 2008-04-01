/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardHelper;

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
     * 	<code>includeMethods</code> takes precedence over <code>excludeMethods</code>
     * </li>
     * </ul>
     * <b>Note:</b> Supports wildcard listings in includeMethods/excludeMethods
     *
     * @param excludeMethods  list of methods to exclude.
     * @param includeMethods  list of methods to include.
     * @param method the specified method to check
     * @return <tt>true</tt> if the method should be applied.
     */
    public static boolean applyMethod(Set excludeMethods, Set includeMethods, String method) {
        
        // quick check to see if any actual pattern matching is needed
        boolean needsPatternMatch = false;
        Iterator quickIter = includeMethods.iterator();
        while (quickIter.hasNext()) {
            String incMeth = (String) quickIter.next();
            if (! incMeth.equals("*") && incMeth.contains("*")) {
                needsPatternMatch = true;
            }
        }
        
        quickIter = excludeMethods.iterator();
        while (quickIter.hasNext()) {
            String incMeth = (String) quickIter.next();
            if (! incMeth.equals("*") && incMeth.contains("*")) {
                needsPatternMatch = true;
            }
        }
        
        // this section will try to honor the original logic, while 
        // still allowing for wildcards later
        if (!needsPatternMatch && (includeMethods.contains("*") || includeMethods.size() == 0) ) {
            if (excludeMethods != null 
                    && excludeMethods.contains(method) 
                    && !includeMethods.contains(method) ) {
                return false;
            }
        }
        
        // test the methods using pattern matching
        WildcardHelper wildcard = new WildcardHelper();
        Iterator iter = includeMethods.iterator();
        String methodCopy ;
        if (method == null ) { // no method specified
            methodCopy = "";
        }
        else {
            methodCopy = new String(method);
        }
        while(iter.hasNext()) {
            String pattern = (String) iter.next();
            if (pattern.contains("*")) {
                int[] compiledPattern = wildcard.compilePattern(pattern);
                HashMap<String,String> matchedPatterns = new HashMap<String, String>();
                boolean matches = wildcard.match(matchedPatterns, methodCopy, compiledPattern);
                if (matches) {
                    return true; // run it, includeMethods takes precedence
                }
            }
            else {
                if (pattern.equals(methodCopy)) {
                    return true; // run it, includeMethods takes precedence
                }
            }
        }
        if (excludeMethods.contains("*") ) {
            return false;
        }

        while(iter.hasNext()) {
            String pattern = (String) iter.next();
            if (pattern.contains("*")) {
                int[] compiledPattern = wildcard.compilePattern(pattern);
                HashMap<String,String> matchedPatterns = new HashMap<String, String>();
                boolean matches = wildcard.match(matchedPatterns, methodCopy, compiledPattern);
                if (matches) {
                    // if found, and wasn't included earlier, don't run it
                    return false; 
                }
            }
            else {
                if (pattern.equals(methodCopy)) {
                    // if found, and wasn't included earlier, don't run it
                    return false; 
                }
            }
        }
    

        // default fall-back from before changes
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
