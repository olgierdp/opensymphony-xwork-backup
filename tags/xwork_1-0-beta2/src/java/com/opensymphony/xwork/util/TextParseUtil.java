/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;


/**
 * TextParseUtil
 * @author Jason Carreira
 * Created Feb 10, 2003 8:55:11 PM
 */
public class TextParseUtil {
    //~ Methods ////////////////////////////////////////////////////////////////

    public static String translateVariables(String expression, OgnlValueStack stack) {
        while (true) {
            int x = expression.indexOf("${");
            int y = expression.indexOf("}", x);

            if ((x != -1) && (y != -1)) {
                String var = expression.substring(x + 2, y);

                Object o = stack.findValue(var);

                if (o != null) {
                    expression = expression.substring(0, x) + o + expression.substring(y + 1);
                } else {
                    // the variable doesn't exist, so don't display anything
                    expression = expression.substring(0, x) + expression.substring(y + 1);
                }
            } else {
                break;
            }
        }

        return expression;
    }
}
