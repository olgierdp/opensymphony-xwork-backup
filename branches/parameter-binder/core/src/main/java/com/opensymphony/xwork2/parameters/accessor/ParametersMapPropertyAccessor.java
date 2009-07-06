/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters.accessor;

import ognl.OgnlException;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.parameters.bytecode.AccessorBytecodeUtil;
import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Same code as XWorkMapPropertyAccessor, but all exceptions are catched from
 * result = super.getProperty(context, target, name);
 * because we didn't parse the expression as an ONGL expression, calling getProperty might fail in some
 * scenarios. This class is not intended to be used outside this package
 */
public class ParametersMapPropertyAccessor implements ParametersPropertyAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(ParametersMapPropertyAccessor.class);

    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;

    private final Map<Key, Class> keyClassCache = new WeakHashMap<Key, Class>();

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }

    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getProperty ("+context+","+target+","+name+")");
        }

        Object result = null;


        if (result == null) {
            //find the key class and convert the name to that class
            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);

            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

            Class keyClass = objectTypeDeterminer
                    .getKeyClass(lastClass, lastProperty);

            if (keyClass == null) {

                keyClass = String.class;
            }
            Object key = getKey(context, name);
            Map map = (Map) target;
            result = map.get(key);

            if (result == null &&
                    context.get(ReflectionContextState.CREATE_NULL_OBJECTS) != null
                    &&  objectTypeDeterminer.shouldCreateIfNew(lastClass,lastProperty,target,null,false)) {
                Class valueClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, key);

                try {
                    result = objectFactory.buildBean(valueClass, context);
                    map.put(key, result);
                } catch (Exception exc) {

                }

            }
        }
        return result;
    }

    /**
     * @param array
     * @param name
     */
    private boolean contains(String[] array, String name) {
        for (String anArray : array) {
            if (anArray.equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        if (LOG.isDebugEnabled()) {
     		LOG.debug("Entering setProperty("+context+","+target+","+name+","+value+")");
     	}

        Object key = getKey(context, name);
        Map map = (Map) target;
        map.put(key, getValue(context, value));
     }

     private Object getValue(Map context, Object value) {
         Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
         String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
         if (lastClass == null || lastProperty == null) {
             return value;
         }
         Class elementClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, null);
         if (elementClass == null) {
             return value; // nothing is specified, we assume it will be the value passed in.
         }
         return xworkConverter.convertValue(context, value, elementClass);
}

    private Object getKey(Map context, Object name) {
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        if (lastClass == null || lastProperty == null) {
            // return java.lang.String.class;
            // commented out the above -- it makes absolutely no sense for when setting basic maps!
            return name;
        }

        Key key = new Key(lastClass, lastProperty);
        //lookup in the cache first
        Class keyClass = keyClassCache.get(key);

        if (keyClass == null) {
            keyClass = objectTypeDeterminer.getKeyClass(lastClass, lastProperty);

            if (keyClass != null)
                keyClassCache.put(key, keyClass);    
        }

        if (keyClass == null) {
            keyClass = String.class;
            keyClassCache.put(key, String.class);    
        }

        return xworkConverter.convertValue(context, name, keyClass);

    }
}

class Key {
    Class clazz;
    String property;

    Key(Class clazz, String property) {
        this.clazz = clazz;
        this.property = property;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (clazz != null ? !clazz.equals(key.clazz) : key.clazz != null) return false;
        if (property != null ? !property.equals(key.property) : key.property != null) return false;

        return true;
    }

    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        return result;
    }
}
