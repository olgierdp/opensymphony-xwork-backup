/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.NullHandler;

import java.lang.reflect.Method;
import java.util.*;


/**
 *
 * @author $Id$
 * @version $Revision$
 */
public class InstantiatingNullHandler implements NullHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Map clazzMap = new HashMap();
    public static final String CREATE_NULL_OBJECTS = "xwork.NullHandler.createNullObjects";

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
        return null;
    }

    /**
     * @see NullHandler#nullPropertyValue(Map, Object, Object) for additional documentation
     * @param context
     * @param target
     * @param property
     * @return
     */
    public Object nullPropertyValue(Map context, Object target, Object property) {
        Boolean create = (Boolean) context.get(CREATE_NULL_OBJECTS);
        boolean c = (create == null ? false : create.booleanValue());
        if (!c) {
            return null;
        }

        if ((target == null) || (property == null)) {
            return null;
        }

        Map methodMap = getMethodMap(target);
        Method method = getMethod(methodMap, property.toString(), target);

        /**
         * if we didn't find any single parameter setters for this method, then there's nothing we can do.
         */
        if (method == null) {
            return null;
        }

        try {
            Class clazz = method.getParameterTypes()[0];
            Object param = createObject(clazz, context);
            method.invoke(target, new Object[]{param});

            return param;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Object createObject(Class clazz, Map context) throws InstantiationException, IllegalAccessException {
        if (clazz == Collection.class || clazz == List.class) {
            return createNewList(context);
        } else if (clazz == Set.class) {

        } else if (clazz == Map.class) {

        }

        return clazz.newInstance();
    }

    private XWorkList createNewList(Map context) {
        Class clazz = null;
        return new XWorkList(clazz);
    }

    /**
     * Attempt to find the setter associated with the provided instance and propertyName.  If we do find it, place that
     * Method into the methodMap keyed by property name
     * @param methodMap
     * @param propertyName the name of the property we're looking up
     * @param instance of instance of the Class we're attempting to find the setter for
     * @return
     */
    private Method getMethod(Map methodMap, String propertyName, Object instance) {
        synchronized (methodMap) {
            Method method = (Method) methodMap.get(propertyName);

            if (method == null) {
                String getter = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                Method[] methods = instance.getClass().getDeclaredMethods();

                for (int i = 0; i < methods.length; i++) {
                    String name = methods[i].getName();

                    if (!getter.equals(name) || (methods[i].getParameterTypes().length != 1)) {
                        continue;
                    } else {
                        method = methods[i];
                        methodMap.put(propertyName, method);

                        break;
                    }
                }
            }

            return method;
        }
    }

    /**
     * returns the Map associated with a given Class of Objects
     * @param instance an instance of the Class we're interested in
     * @return a Map of Method instances keyed by property name
     */
    private Map getMethodMap(Object instance) {
        synchronized (clazzMap) {
            Map methodMap = (Map) clazzMap.get(instance.getClass());

            if (methodMap == null) {
                methodMap = new HashMap();
                clazzMap.put(instance.getClass(), methodMap);
            }

            return methodMap;
        }
    }
}
