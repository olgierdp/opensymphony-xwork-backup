/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

import ognl.OgnlRuntime;
import ognl.OgnlException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.beans.IntrospectionException;

/**
 * GenericsObjectTypeDeterminer
 *
 * @author Patrick Lightbody
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
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
        Key annotation = getAnnotation(parentClass, property, Key.class);

        if (annotation != null) {
            return annotation.value();
        }

        Class clazz = getClass(parentClass, property, false);

        if (clazz != null) {
            return clazz;
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
        Element annotation = getAnnotation(parentClass, property, Element.class);

        if (annotation != null) {
            return annotation.value();
        }

        Class clazz = getClass(parentClass, property, true);

        if (clazz != null) {
            return clazz;
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
        KeyProperty annotation = getAnnotation(parentClass, property, KeyProperty.class);

        if (annotation != null) {
            return annotation.value();
        }

        return super.getKeyProperty(parentClass, property);
    }

    /**
     * Determines the createIfNull property for a Collection or Map by getting it from the @CreateIfNull annotation.
     *
     * @param parentClass     the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property        the property of the Map or Collection for the given parent class
     * @param target
     * @param keyProperty
     * @param isIndexAccessed <tt>true</tt>, if the collection or map is accessed via index, <tt>false</tt> otherwise.
     * @see ObjectTypeDeterminer#getKeyProperty(Class, String)
     */
    public boolean shouldCreateIfNew(Class parentClass,
                                     String property,
                                     Object target,
                                     String keyProperty,
                                     boolean isIndexAccessed) {

        CreateIfNull annotation = getAnnotation(parentClass, property, CreateIfNull.class);

        if (annotation != null) {
            return annotation.value();
        }

        return super.shouldCreateIfNew(parentClass, property, target, keyProperty, isIndexAccessed);
    }

    /**
     * Retrieves an annotation for the specified property of field, setter or getter.
     *
     * @param <T>             the annotation type to be retrieved
     * @param parentClass     the class
     * @param property        the property
     * @param annotationClass the annotation
     * @return the field or setter/getter annotation or <code>null</code> if not found
     */
    protected <T extends Annotation> T getAnnotation(Class parentClass, String property, Class<T> annotationClass) {
        T annotation = null;
        Field field = OgnlRuntime.getField(parentClass, property);

        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
        }
        if (annotation == null) { // HINT: try with setter
            annotation = getAnnotationFromSetter(parentClass, property, annotationClass);
        }
        if (annotation == null) { // HINT: try with getter
            annotation = getAnnotationFromGetter(parentClass, property, annotationClass);
        }

        return annotation;
    }

    /**
     * Retrieves an annotation for the specified field of getter.
     *
     * @param parentClass     the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property        the property of the Map or Collection for the given parent class
     * @param annotationClass The annotation
     * @return concrete Annotation instance or <tt>null</tt> if none could be retrieved.
     */
    private <T extends Annotation>T getAnnotationFromGetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method getter = OgnlRuntime.getGetMethod(null, parentClass, property);

            if (getter != null) {
                return getter.getAnnotation(annotationClass);
            }
        }
        catch (OgnlException ognle) {
            ; // ignore
        }
        catch (IntrospectionException ie) {
            ; // ignore
        }
        return null;
    }

    /**
     * Retrieves an annotation for the specified field of setter.
     *
     * @param parentClass     the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property        the property of the Map or Collection for the given parent class
     * @param annotationClass The annotation
     * @return concrete Annotation instance or <tt>null</tt> if none could be retrieved.
     */
    private <T extends Annotation>T getAnnotationFromSetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method setter = OgnlRuntime.getSetMethod(null, parentClass, property);

            if (setter != null) {
                return setter.getAnnotation(annotationClass);
            }
        }
        catch (OgnlException ognle) {
            ; // ignore
        }
        catch (IntrospectionException ie) {
            ; // ignore
        }
        return null;
    }

    /**
     * Returns the class for the given field via generic type check.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @param element     <tt>true</tt> for indexed types and Maps.
     * @return Class of the specified field.
     */
    private Class getClass(Class parentClass, String property, boolean element) {


        try {

            Field field = OgnlRuntime.getField(parentClass, property);

            Type genericType = null;

            // Check fields first
            if (field != null) {
                genericType = field.getGenericType();
            }

            // Try to get ParameterType from setter method
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method setter = OgnlRuntime.getSetMethod(null, parentClass, property);
                    genericType = setter.getGenericParameterTypes()[0];
                }
                catch (OgnlException ognle) {
                    ; // ignore
                }
                catch (IntrospectionException ie) {
                    ; // ignore
                }
            }

            // Try to get ReturnType from getter method
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method getter = OgnlRuntime.getGetMethod(null, parentClass, property);
                    genericType = getter.getGenericReturnType();
                }
                catch (OgnlException ognle) {
                    ; // ignore
                }
                catch (IntrospectionException ie) {
                    ; // ignore
                }
            }

            if (genericType instanceof ParameterizedType) {


                ParameterizedType type = (ParameterizedType) genericType;

                int index = (element && type.getRawType().toString().contains(Map.class.getName())) ? 1 : 0;

                Type resultType = type.getActualTypeArguments()[index];

                if ( resultType instanceof ParameterizedType) {
                    return resultType.getClass();
                }
                return (Class) resultType;

            }
        } catch (Exception e) {
            if ( LOG.isDebugEnabled()) {
                LOG.debug("Error while retrieving generic property class for property=" + property, e);
            }
        }
        return null;
    }
}
