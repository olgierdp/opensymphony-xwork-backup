/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.*;

import java.lang.reflect.Method;
import java.util.*;
import java.io.InputStream;

import com.opensymphony.util.FileManager;


/**
 *
 * @author $Id$
 * @version $Revision$
 */
public class InstantiatingNullHandler implements NullHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Map clazzMap = new HashMap();
    public static final String CREATE_NULL_OBJECTS = "xwork.NullHandler.createNullObjects";

    HashMap mappings = new HashMap();
    HashSet noMapping = new HashSet();

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
            e.printStackTrace();
        }

        return null;
    }

    private Object createObject(Map context, Class clazz, Object target, String property) throws InstantiationException, IllegalAccessException {
        if (clazz == Collection.class || clazz == List.class) {
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

        return clazz.newInstance();
    }


    private Map buildConverterMapping(Class clazz) throws Exception {
        Map mapping = new HashMap();

        String resource = XWorkConverter.buildConverterFilename(clazz);
        InputStream is = FileManager.loadFile(resource, clazz);

        if (is != null) {
            Properties props = new Properties();
            props.load(is);
            mapping.putAll(props);

            for (Iterator iterator = mapping.entrySet().iterator();
                    iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String propName = (String) entry.getKey();
                String className = (String) entry.getValue();
                if (propName.startsWith("Collection_")) {
                    entry.setValue(Class.forName(className));
                }
            }

            mappings.put(clazz, mapping);
        } else {
            noMapping.add(clazz);
        }

        return mapping;
    }

    private Class getCollectionType(Class clazz, String property) {
        Class propClass = null;
        if (!noMapping.contains(clazz)) {
            try {
                Map mapping = (Map) mappings.get(clazz);

                if (mapping == null) {
                    mapping = buildConverterMapping(clazz);
                } else {
                    mapping = conditionalReload(clazz, mapping);
                }

                propClass = (Class) mapping.get("Collection_" + property);
            } catch (Throwable t) {
                noMapping.add(clazz);
            }
        }

        return propClass;
    }

    private Map conditionalReload(Class clazz, Map oldValues) throws Exception {
        Map mapping = oldValues;

        if (FileManager.isReloadingConfigs()) {
            if (FileManager.fileNeedsReloading(XWorkConverter.buildConverterFilename(clazz))) {
                mapping = buildConverterMapping(clazz);
            }
        }

        return mapping;
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
