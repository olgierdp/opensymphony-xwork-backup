/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.MethodFailedException;
import ognl.ObjectMethodAccessor;

import java.util.Map;


/**
 * Allows methods to be executed under normal cirumstances, except when {@link #DENY_METHOD_EXECUTION}
 * is in the action context with a value of true.
 *
 * @author Patrick Lightbody
 */
public class XWorkMethodAccessor extends ObjectMethodAccessor {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String DENY_METHOD_EXECUTION = "xwork.MethodAccessor.denyMethodExecution";

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {
        Boolean exec = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return super.callMethod(context, object, string, objects);
        } else {
            return null;
        }
    }

    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        Boolean exec = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return super.callStaticMethod(context, aClass, string, objects);
        } else {
            return null;
        }
    }
}
