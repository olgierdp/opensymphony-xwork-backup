package com.opensymphony.xwork.util;

import ognl.OgnlRuntime;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * User: plightbo
 * Date: Sep 16, 2005
 * Time: 9:58:55 AM
 */
public class GenericsObjectTypeDeterminer implements ObjectTypeDeterminer {
    public Class getKeyClass(Class parentClass, String property) {
        return getClass(parentClass, property, 0);
    }

    public Class getElementClass(Class parentClass, String property, Object key) {
        int index = 0;
        if (Map.class.isAssignableFrom(parentClass)) {
            index = 1;
        }

        return getClass(parentClass, property, index);
    }

    public String getKeyProperty(Class parentClass, String property) {
        Field field = OgnlRuntime.getField(parentClass, property);
        KeyProperty annotation = field.getAnnotation(KeyProperty.class);
        if (annotation != null) {
            return annotation.value();
        }

        return null;
    }

    private Class getClass(Class parentClass, String property, int index) {
        Field field = OgnlRuntime.getField(parentClass, property);
        ParameterizedType type = (ParameterizedType) field.getGenericType();

        return (Class) type.getActualTypeArguments()[index];
    }
}
