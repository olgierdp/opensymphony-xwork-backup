/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.MethodFailedException;
import ognl.ObjectMethodAccessor;

import java.util.Map;


/**
 * User: plightbo
 * Date: Dec 28, 2003
 * Time: 8:34:20 PM
 */
public class XWorkMethodAccessor extends ObjectMethodAccessor {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String DENY_METHOD_EXECUTION = "xwork.MethodAccessor.denyMethodExecution";

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {
        Boolean create = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean c = ((create == null) ? false : create.booleanValue());

        if (!c) {
            return super.callMethod(context, object, string, objects);
        } else {
            return null;
        }
    }

    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        Boolean create = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean c = ((create == null) ? false : create.booleanValue());

        if (!c) {
            return super.callStaticMethod(context, aClass, string, objects);
        } else {
            return null;
        }
    }
}
