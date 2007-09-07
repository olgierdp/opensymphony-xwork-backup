/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion;

/**
 * Determines what the key and and element class of a Map or Collection should be. For Maps, the elements are the
 * values. For Collections, the elements are the elements of the collection.
 * <p/>
 * See the implementations for javadoc description for the methods as they are dependent on the concrete implementation.
 *
 * @author Gabriel Zimmerman
 */
public interface ObjectTypeDeterminer {

    public Class getKeyClass(Class parentClass, String property);

    public Class getElementClass(Class parentClass, String property, Object key);

    public String getKeyProperty(Class parentClass, String property);
    
    public boolean shouldCreateIfNew(Class parentClass,  String property,  Object target, String keyProperty, boolean isIndexAccessed);

}
