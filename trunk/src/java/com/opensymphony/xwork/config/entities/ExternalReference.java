/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 12, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork.config.entities;


/**
 * @author Ross
 *         <p/>
 *         Encapsulates an external reference in the xwork configuration
 */
public class ExternalReference {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String externalRef;
    private String name;
    private boolean required = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * default constructor
     */
    public ExternalReference() {
    }

    /**
     * @param name        the name of the attribute the external reference refers to
     * @param externalRef the name used to query the external source
     * @param required    determines whether an exception should be thrown if the reference is not resolved
     */
    public ExternalReference(String name, String externalRef, boolean required) {
        this.name = name;
        this.externalRef = externalRef;
        this.required = required;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @param externalRef The externalRef to set.
     */
    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    /**
     * @return Returns the externalRef.
     */
    public String getExternalRef() {
        return externalRef;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param required The required to set.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return Returns the required.
     */
    public boolean isRequired() {
        return required;
    }
}
