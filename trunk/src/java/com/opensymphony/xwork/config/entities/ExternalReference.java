/*
 * Created on Nov 12, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork.config.entities;

/**
 * @author Ross
 *
 * Encapsulates an external reference in the xwork configuration
 */
public class ExternalReference {
	
	private String name;
	private String externalRef;
	private boolean required = true;
	
	/**
	 * default constructor
	 */
	public ExternalReference(){}
	
	/**
	 * @param name the name of the attribute the external reference refers to
	 * @param externalRef the name used to query the external source
	 * @param required determines whether an exception should be thrown if the reference is not resolved
	 */
	public ExternalReference(String name, String externalRef, boolean required) 
	{
		this.name = name;
		this.externalRef = externalRef;
		this.required = required;
	}
	/**
	 * @return Returns the externalRef.
	 */
	public String getExternalRef() {
		return externalRef;
	}

	/**
	 * @param externalRef The externalRef to set.
	 */
	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required The required to set.
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

}
