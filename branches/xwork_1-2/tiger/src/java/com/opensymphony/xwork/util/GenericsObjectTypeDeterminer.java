/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.util;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import ognl.OgnlException;
import ognl.OgnlRuntime;

/**
 * GenericsObjectTypeDeterminer use the following algorithm for determining the types: looks for 
 * a field annotation, than for the corresponding setter annotation, and for special cases falls
 * back to generic types.
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
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getKeyClass(Class, String)
     */
    public Class getKeyClass(Class parentClass, String property) {
        Key annotation = getAnnotation(parentClass, property, Key.class);

        if (annotation != null) {
            return annotation.value();
        }

        Class clazz = getClass(OgnlRuntime.getField(parentClass, property), false);

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
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getElementClass(Class, String, Object)
     */
    public Class getElementClass(Class parentClass, String property, Object key) {
        Element annotation = getAnnotation(parentClass, property, Element.class);

        if (annotation != null) {
            return annotation.value();
        }

        Class clazz = getClass(OgnlRuntime.getField(parentClass, property), true);

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
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getKeyProperty(Class, String)
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
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @param target
     * @param keyProperty
     * @param isIndexAccessed <tt>true</tt>, if the collection or map is accessed via index, <tt>false</tt> otherwise.
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getKeyProperty(Class, String)
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
     * Retrieves an annotation for the specified field of setter.
     * 
     * @param <T> the annotation type to be retrieved
     * @param parentClass the class
     * @param property the property
     * @param annotationClass the annotation 
     * @return the field or setter annotation or <code>null</code> if not found
     */
    protected <T extends Annotation> T getAnnotation(Class parentClass, String property, Class<T> annotationClass) {
        T annotation = null;
        Field field = OgnlRuntime.getField(parentClass, property);

        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
        }
        if (annotation == null) { // HINT: try with setter
            try {
                Method setter = OgnlRuntime.getSetMethod(null, parentClass, property);
                
                if (setter != null) {
                    annotation = setter.getAnnotation(annotationClass);                    
                }
            }
            catch(OgnlException ognle) {
                ; // ignore
            }
            catch(IntrospectionException ie) {
                ; // ignore
            }
        }
        
        return annotation;
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
