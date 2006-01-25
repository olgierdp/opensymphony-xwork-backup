package com.opensymphony.xwork.util;

import ognl.OgnlRuntime;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * User: plightbo
 * Date: Sep 16, 2005
 * Time: 9:58:55 AM
 */
public class GenericsObjectTypeDeterminer extends DefaultObjectTypeDeterminer {
    public Class getKeyClass(Class parentClass, String property) {
        Class clazz = getClass(parentClass, property, false);
        if (clazz != null) {
            return clazz;
        } else {
            return super.getKeyClass(parentClass, property);
        }
    }

    public Class getElementClass(Class parentClass, String property, Object key) {
        Class clazz = getClass(parentClass, property, true);
         if (clazz != null) {
             return clazz;
         } else {
            return super.getElementClass(parentClass, property, key);
        }
    }

    public String getKeyProperty(Class parentClass, String property) {
        Field field = OgnlRuntime.getField(parentClass, property);
        if (field == null) {
            return null;
        }
        KeyProperty annotation = field.getAnnotation(KeyProperty.class);
        if (annotation != null) {
            return annotation.value();
        }

        return super.getKeyProperty(parentClass, property);
    }

    private Class getClass(Class parentClass, String property, boolean element) {
        Field field = OgnlRuntime.getField(parentClass, property);
        if (field == null) {
            return null;
        }
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            int index = (element && Map.class.isAssignableFrom(field.getType())) ? 1 : 0;
            ParameterizedType type = (ParameterizedType) genericType;

            return (Class) type.getActualTypeArguments()[index];
        } else {
            return null;
        }
    }
}
