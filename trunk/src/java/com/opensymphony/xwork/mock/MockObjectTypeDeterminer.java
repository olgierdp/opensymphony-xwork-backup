/*
 * Created on Dec 12, 2005
 *
 */
package com.opensymphony.xwork.mock;

import com.opensymphony.xwork.util.ObjectTypeDeterminer;

/**
 * @author Gabe
 *
 * Mocks the function of an ObjectTypeDeterminer for testing purposes
 */
public class MockObjectTypeDeterminer implements ObjectTypeDeterminer {

    private Class keyClass;
    private Class elementClass;
    private String keyProperty;
    private boolean shouldCreateIfNew;

    public MockObjectTypeDeterminer() {}


    /**
     * @param keyClass
     * @param elementClass
     * @param keyProperty
     * @param shouldCreateIfNew
     */
    public MockObjectTypeDeterminer(Class keyClass, Class elementClass,
                                    String keyProperty, boolean shouldCreateIfNew) {
        super();
        this.keyClass = keyClass;
        this.elementClass = elementClass;
        this.keyProperty = keyProperty;
        this.shouldCreateIfNew = shouldCreateIfNew;
    }

    /* (non-Javadoc)
    * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getKeyClass(java.lang.Class, java.lang.String)
    */
    public Class getKeyClass(Class parentClass, String property) {

        return getKeyClass();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getElementClass(java.lang.Class, java.lang.String, java.lang.Object)
     */
    public Class getElementClass(Class parentClass, String property, Object key) {

        return getElementClass();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#getKeyProperty(java.lang.Class, java.lang.String)
     */
    public String getKeyProperty(Class parentClass, String property) {

        return getKeyProperty();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.util.ObjectTypeDeterminer#shouldCreateIfNew(java.lang.Class, java.lang.String, java.lang.Object, java.lang.String, boolean)
     */
    public boolean shouldCreateIfNew(Class parentClass, String property,
                                     Object target, String keyProperty, boolean isIndexAccessed) {

        return isShouldCreateIfNew();
    }

    /**
     * @return Returns the elementClass.
     */
    public Class getElementClass() {
        return elementClass;
    }
    /**
     * @param elementClass The elementClass to set.
     */
    public void setElementClass(Class elementClass) {
        this.elementClass = elementClass;
    }
    /**
     * @return Returns the keyClass.
     */
    public Class getKeyClass() {
        return keyClass;
    }
    /**
     * @param keyClass The keyClass to set.
     */
    public void setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
    }
    /**
     * @return Returns the keyProperty.
     */
    public String getKeyProperty() {
        return keyProperty;
    }
    /**
     * @param keyProperty The keyProperty to set.
     */
    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }
    /**
     * @return Returns the shouldCreateIfNew.
     */
    public boolean isShouldCreateIfNew() {
        return shouldCreateIfNew;
    }
    /**
     * @param shouldCreateIfNew The shouldCreateIfNew to set.
     */
    public void setShouldCreateIfNew(boolean shouldCreateIfNew) {
        this.shouldCreateIfNew = shouldCreateIfNew;
    }
}
