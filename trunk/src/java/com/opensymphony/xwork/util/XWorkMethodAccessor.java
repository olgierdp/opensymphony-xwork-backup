package com.opensymphony.xwork.util;

import ognl.ObjectMethodAccessor;
import ognl.MethodFailedException;

import java.util.Map;

/**
 * User: plightbo
 * Date: Dec 28, 2003
 * Time: 8:34:20 PM
 */
public class XWorkMethodAccessor extends ObjectMethodAccessor {
    private static ThreadLocal state = new ThreadLocal();

    public static void setState(boolean on) {
        if (on) {
            state.set(Boolean.TRUE);
        } else {
            state.set(null);
        }
    }

    public Object callMethod(Map map, Object object, String string, Object[] objects) throws MethodFailedException {
        if (state.get() == null) {
            return super.callMethod(map, object, string, objects);
        } else {
            return null;
        }
    }

    public Object callStaticMethod(Map map, Class aClass, String string, Object[] objects) throws MethodFailedException {
        if (state.get() == null) {
            return super.callStaticMethod(map, aClass, string, objects);
        } else {
            return null;
        }
    }
}
