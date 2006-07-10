/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

import ognl.OgnlRuntime;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * GenericsObjectTypeDeterminer
 *
 * @author Patrick Lightbody
 * @author Rainer Hermanns
 */
public class GenericsObjectTypeDeterminer extends DefaultObjectTypeDeterminer {

    /**
     * Determines the key class by looking for the value of @Key annotation for the given class.
     * If no annotation is found, the key class is determined by using the generic parametrics.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see com.opensymphony.xwork2.util.ObjectTypeDeterminer#getKeyClass(Class, String)
     */
    public Class getKeyClass(Class parentClass, String property) {

        Field field = OgnlRuntime.getField(parentClass, property);

        if (field != null) {
            Key annotation = field.getAnnotation(Key.class);
            if (annotation != null) {
                return annotation.value();
            }

            Class clazz = getClass(field, false);

            if (clazz != null) {
                return clazz;
            }
        }

        return super.getKeyClass(parentClass, property);
    }

    /**
     * Determines the element class by looking for the value of @Element annotation for the given
     * class.
     * If no annotation is found, the element class is determined by using the generic parametrics.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see com.opensymphony.xwork2.util.ObjectTypeDeterminer#getElementClass(Class, String, Object)
     */
    public Class getElementClass(Class parentClass, String property, Object key) {

        Field field = OgnlRuntime.getField(parentClass, property);

        if (field != null) {
            Element annotation = field.getAnnotation(Element.class);
            if (annotation != null) {
                return annotation.value();
            }

            Class clazz = getClass(field, true);

            if (clazz != null) {
                return clazz;
            }
        }

        return super.getElementClass(parentClass, property, key);
    }

    /**
     * Determines the key property for a Collection by getting it from the @KeyProperty annotation.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see com.opensymphony.xwork2.util.ObjectTypeDeterminer#getKeyProperty(Class, String)
     */
    public String getKeyProperty(Class parentClass, String property) {

        Field field = OgnlRuntime.getField(parentClass, property);

        if (field != null) {
            KeyProperty annotation = field.getAnnotation(KeyProperty.class);

            if (annotation != null) {
                return annotation.value();
            }
        }


        return super.getKeyProperty(parentClass, property);
    }

    /**
     * Determines the createIfNull property for a Collection or Map by getting it from the @CreateIfNull annotation.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @param target
     * @param keyProperty
     * @param isIndexAccessed <tt>true</tt>, if the collection or map is accessed via index, <tt>false</tt> otherwise.
     * @see com.opensymphony.xwork2.util.ObjectTypeDeterminer#getKeyProperty(Class, String)
     */
    public boolean shouldCreateIfNew(Class parentClass,
                                     String property,
                                     Object target,
                                     String keyProperty,
                                     boolean isIndexAccessed) {

        Field field = OgnlRuntime.getField(parentClass, property);

        if (field != null) {
            CreateIfNull annotation = field.getAnnotation(CreateIfNull.class);
            if (annotation != null) {
                return annotation.value();
            }
        }

        return super.shouldCreateIfNew(parentClass, property, target, keyProperty, isIndexAccessed);

    }

    /**
     * Returns the class for the given field via generic type check.
     *
     * @param field The field to check for generic types.
     * @param element <tt>true</tt> for indexed types and Maps.
     * @return Class of the specified field.
     */
    private Class getClass(Field field, boolean element) {
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
