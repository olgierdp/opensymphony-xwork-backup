/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;


/**
 * Utility class for text parsing.
 *
 * @author Jason Carreira
 * Created Feb 10, 2003 8:55:11 PM
 */
public class TextParseUtil {
    //~ Methods ////////////////////////////////////////////////////////////////

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
        return translateVariables('$', expression, stack);
    }

    public static String translateVariables(char open, String expression, OgnlValueStack stack) {
        return translateVariables(open, expression, stack, String.class).toString();
    }

    public static Object translateVariables(char open, String expression, OgnlValueStack stack, Class asType) {
        // deal with the "pure" expressions first!
        expression = expression.trim();
        if (expression.startsWith(open + "{") && expression.endsWith("}")) {
            return stack.findValue(expression.substring(2, expression.length() - 1), asType);
        }

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

                if (o != null) {
                    expression = expression.substring(0, start) + o + expression.substring(end + 1);
                } else {
                    // the variable doesn't exist, so don't display anything
                    expression = expression.substring(0, start) + expression.substring(end + 1);
                }
            } else {
                break;
            }
        }

        return XWorkConverter.getInstance().convertValue(stack.getContext(), expression, asType);
    }
}
