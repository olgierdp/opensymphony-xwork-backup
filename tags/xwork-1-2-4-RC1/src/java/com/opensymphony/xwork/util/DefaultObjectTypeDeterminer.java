/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * This {@link ObjectTypeDeterminer} looks at the <b>Class-conversion.properties</b> for entries that indicated what
 * objects are contained within Maps and Collections. For Collections, such as Lists, the element is specified using the
 * pattern <b>Element_xxx</b>, where xxx is the field name of the collection property in your action or object. For
 * Maps, both the key and the value may be specified by using the pattern <b>Key_xxx</b> and <b>Element_xxx</b>,
 * respectively.
 *
 * <p/> From WebWork 2.1.x, the <b>Collection_xxx</b> format is still supported and honored, although it is deprecated
 * and will be removed eventually.
 *
 * <!-- END SNIPPET: javadoc -->
 * 
 *
 * @author Gabriel Zimmerman
 * @see XWorkListPropertyAccessor
 * @see XWorkCollectionPropertyAccessor
 * @see XWorkMapPropertyAccessor
 */
public class DefaultObjectTypeDeterminer implements ObjectTypeDeterminer {
    protected static final Log LOG = LogFactory.getLog(DefaultObjectTypeDeterminer.class);

    public static final String KEY_PREFIX = "Key_";
    public static final String ELEMENT_PREFIX = "Element_";
    public static final String KEY_PROPERTY_PREFIX = "KeyProperty_";
    public static final String CREATE_IF_NULL_PREFIX = "CreateIfNull_";
    public static final String DEPRECATED_ELEMENT_PREFIX = "Collection_";


    /**
     * Determines the key class by looking for the value of Key_${property} in the properties file for the given class.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see ObjectTypeDeterminer#getKeyClass(Class, String)
     */
    public Class getKeyClass(Class parentClass, String property) {
        return (Class) XWorkConverter.getInstance()
                .getConverter(parentClass, KEY_PREFIX + property);
    }

    /**
     * Determines the key class by looking for the value of Element_${property} in the properties file for the given
     * class. Also looks for the deprecated Collection_${property}
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @see ObjectTypeDeterminer#getElementClass(Class, String, Object)
     */
    public Class getElementClass(Class parentClass, String property, Object key) {
        Class clazz = (Class) XWorkConverter.getInstance()
                .getConverter(parentClass, ELEMENT_PREFIX + property);
        if (clazz == null) {
            clazz = (Class) XWorkConverter.getInstance()
                    .getConverter(parentClass, DEPRECATED_ELEMENT_PREFIX + property);

            if (clazz != null) {
                LOG.info("The Collection_xxx pattern for collection type conversion is deprecated. Please use Element_xxx!");
            }
        }
        return clazz;
    }

    /**
     * Determines the String key property for a Collection by getting it from the conversion properties file using the
     * KeyProperty_ prefix. KeyProperty_${property}=somePropertyOfBeansInTheSet
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the KeyProperty for.
     * @param property    the property of the Map or Collection for the given parent class
     */
    public String getKeyProperty(Class parentClass, String property) {
        return (String) XWorkConverter.getInstance()
                .getConverter(parentClass, KEY_PROPERTY_PREFIX + property);
    }
    
    /**
     * Determines the boolean CreateIfNull property for a Collection or Map by getting it from the conversion properties
     * file using the CreateIfNull_ prefix. CreateIfNull_${property}=true|false
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the CreateIfNull for.
     * @param property    the property of the Map or Collection for the given parent class
     * @param target
     * @param keyProperty
     * @param isIndexAccessed
     * @return <tt>true</tt>, if the Collection or Map should be created, <tt>false</tt> otherwise.
     */
    public boolean shouldCreateIfNew(Class parentClass,
            String property, 
            Object target,
            String keyProperty,
            boolean isIndexAccessed) {
        
        	String configValue = (String) XWorkConverter.getInstance()
                    .getConverter(parentClass, CREATE_IF_NULL_PREFIX + property);
        	//check if a value is in the config
        	if (configValue!=null) {
        	    if (configValue.equals("true")) {
        	        return true;
        	    }
        	    if (configValue.equals("false")) {
        	        return false;
        	    }        	      
        	}
        	
        	//default values depend on target type 
        	//and whether this is accessed by an index
        	//in the case of List
        	if ((target instanceof Map) || isIndexAccessed) {
        	    return true;
        	}	else {
        	    return false;
        	}
    }
}
