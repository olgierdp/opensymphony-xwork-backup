package com.opensymphony.xwork.util;

import java.util.HashMap;

/**
 * User: plightbo
 * Date: Jan 13, 2004
 * Time: 9:21:03 PM
 */
public class XWorkMap extends HashMap {
    private Class clazz;

    public XWorkMap(Class clazz) {
        this.clazz = clazz;
    }

    public Object get(Object key) {
        Object o = super.get(key);
        if (o == null) {
            try {
                o = clazz.newInstance();
                this.put(key, o);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return o;
    }

}
