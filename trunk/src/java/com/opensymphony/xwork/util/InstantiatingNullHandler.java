/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;
import ognl.NullHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.*;


/**
 * Normally does nothing, but if {@link #CREATE_NULL_OBJECTS} is in the action context
 * with a value of true, then this class will attempt to create null objects when Ognl
 * requests null objects be created.
 * <p/>
 * The following rules are used:
 * <ul>
 * <li>If the null property is a simple bean with a no-arg constructor, it will simply be
 * created using ObjectFactory's {@link ObjectFactory#buildBean(java.lang.Class) buildBean} method.</li>
 * <li>If the property is declared <i>exactly</i> as a {@link Collection} or {@link List}, then this class
 * will look in the conversion property file (see {@link XWorkConverter}) for an entry
 * with a key of the form "Collection_[propertyName]". Using the value of this key as
 * the class type in which the collection will be holding, an {@link XWorkList} will be
 * created, allowing simple dynamic insertion.</li>
 * <li>If the property is declared as a {@link Map}, then the same rules are applied for
 * list, except that an {@link XWorkMap} will be created instead.</li>
 * </ul>
 *
 * @author Matt Ho
 * @author Patrick Lightbody
 */
public class InstantiatingNullHandler implements NullHandler {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String CREATE_NULL_OBJECTS = "xwork.NullHandler.createNullObjects";
    private static final Log LOG = LogFactory.getLog(InstantiatingNullHandler.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    HashMap mappings = new HashMap();
    HashSet noMapping = new HashSet();
    private Map clazzMap = new HashMap();

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
        return null;
    }

    public Object nullPropertyValue(Map context, Object target, Object property) {
        Boolean create = (Boolean) context.get(CREATE_NULL_OBJECTS);
        boolean c = ((create == null) ? false : create.booleanValue());

        if (!c) {
            return null;
        }

        if ((target == null) || (property == null)) {
            return null;
        }

        Method method = null;

        if (target instanceof CompoundRoot) {
            // sometimes the null object may be at the root, so we need to check all of them
            CompoundRoot root = (CompoundRoot) target;

            for (Iterator iterator = root.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                Map methodMap = getMethodMap(o);
                method = getMethod(methodMap, property.toString(), o);

                if (method != null) {
                    target = o;

                    break;
                }
            }
        } else {
            Map methodMap = getMethodMap(target);
            method = getMethod(methodMap, property.toString(), target);
        }

        /**
         * if we didn't find any single parameter setters for this method, then there's nothing we can do.
         */
        if (method == null) {
            return null;
        }

        try {
            Class clazz = method.getParameterTypes()[0];
            Object param = createObject(context, clazz, target, property.toString());
            method.invoke(target, new Object[]{param});

            return param;
        } catch (Exception e) {
            LOG.error("Could not create and/or set value back on to object", e);
        }

        return null;
    }

    protected Class getCollectionType(Class clazz, String property) {
        return (Class) XWorkConverter.getInstance().getConverter(clazz, "Collection_" + property);
    }

    /**
     * Attempt to find the setter associated with the provided instance and propertyName.  If we do find it, place that
     * Method into the methodMap keyed by property name
     *
     * @param propertyName the name of the property we're looking up
     * @param instance     of instance of the Class we're attempting to find the setter for
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
     *
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

    private Object createObject(Map context, Class clazz, Object target, String property) throws Exception {
        if ((clazz == Collection.class) || (clazz == List.class)) {
            Class collectionType = getCollectionType(target.getClass(), property);

            if (collectionType == null) {
                return null;
            }

            return new XWorkList(collectionType);
        } else if (clazz == Map.class) {
            Class collectionType = getCollectionType(target.getClass(), property);

            if (collectionType == null) {
                return null;
            }

            return new XWorkMap(collectionType);
        }

        return ObjectFactory.getObjectFactory().buildBean(clazz);
    }
}
