/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.util.TextUtils;

import java.util.HashSet;
import java.util.Set;


/**
 * Utility class for text parsing.
 *
 * @author Jason Carreira
 *         Created Feb 10, 2003 8:55:11 PM
 */
public class TextParseUtil {

    /**
     * Converts all instances of ${...} in <code>expression</code> to the value returned
     * by a call to {@link OgnlValueStack#findValue(java.lang.String)}. If an item cannot
     * be found on the stack (null is returned), then the entire variable ${...} is not
     * displayed, just as if the item was on the stack but returned an empty string.
     *
     * @param expression an expression that hasn't yet been translated
     * @return the parsed expression
     */
    public static String translateVariables(String expression, OgnlValueStack stack) {
        return translateVariables('$', expression, stack, String.class).toString();
    }

    public static String translateVariables(char open, String expression, OgnlValueStack stack) {
        return translateVariables(open, expression, stack, String.class).toString();
    }

    public static Object translateVariables(char open, String expression, OgnlValueStack stack, Class asType) {
        // deal with the "pure" expressions first!
        //expression = expression.trim();
        Object result = expression;

        while (true) {
            int start = expression.indexOf(open + "{");
            int length = expression.length();
            int x = start + 2;
            int end;
            char c;
            int count = 1;
            while (start != -1 && x < length && count != 0) {
                c = expression.charAt(x++);
                if (c == '{') {
                    count++;
                } else if (c == '}') {
                    count--;
                }
            }
            end = x - 1;

            if ((start != -1) && (end != -1) && (count == 0)) {
                String var = expression.substring(start + 2, end);

                Object o = stack.findValue(var, asType);

                String left = expression.substring(0, start);
                String right = expression.substring(end + 1);
                if (o != null) {
                    if (TextUtils.stringSet(left)) {
                        result = left + o;
                    } else {
                        result = o;
                    }

                    if (TextUtils.stringSet(right)) {
                        result = result + right;
                    }

                    expression = left + o + right;
                } else {
                    // the variable doesn't exist, so don't display anything
                    result = left + right;
                    expression = left + right;
                }
            } else {
                break;
            }
        }

        return XWorkConverter.getInstance().convertValue(stack.getContext(), result, asType);
    }

    public static Set commaDelimitedStringToSet(String s) {
        Set set = new HashSet();
        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            String trimmed = split[i].trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }
}
