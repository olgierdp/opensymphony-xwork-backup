/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.xwork.util;

/**
 * ObjectTypeDeterminer that implements the default XWork settings
 * 
 * @author Gabriel Zimmerman
 *
 */
public class DefaultObjectTypeDeterminer implements ObjectTypeDeterminer {

	public static final String KEY_PREFIX="Key_";
	public static final String ELEMENT_PREFIX="Element_";
	public static final String DEPRECATED_ELEMENT_PREFIX="Collection_";
	

	/**
	 * Determines the key class by looking for the
	 * value of Key_${property} in the properties
	 * file for the given class.
	 * 
	 * @param parentClass the Class which contains as a property the
	 *                    Map or Collection we are finding the key for.
	 * @param property    the property of the Map or Collection for
	 *                    the given parent class
	 * 
	 * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getKeyClass(java.lang.Class, java.lang.String)
	 */
	public Class getKeyClass(Class parentClass, String property) {
		
		return (Class) XWorkConverter.getInstance()
			.getConverter(parentClass, KEY_PREFIX + property);
	}

	/** 
	 * Determines the key class by looking for the
	 * value of Element_${property} in the properties
	 * file for the given class. Also looks for the 
	 * deprecated Collection_${property}
	 * 
	 * @param parentClass the Class which contains as a property the
	 *                    Map or Collection we are finding the key for.
	 * @param property    the property of the Map or Collection for
	 *                    the given parent class
	 * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getElementClass(Class, String, Object)
	 */
	public Class getElementClass(Class parentClass, String property, Object key) {
		Class clazz=(Class) XWorkConverter.getInstance()
			.getConverter(parentClass, ELEMENT_PREFIX + property);
		if (clazz==null){
			clazz=(Class) XWorkConverter.getInstance()
			.getConverter(parentClass, DEPRECATED_ELEMENT_PREFIX + property);
		}
		return clazz;
	}

}
