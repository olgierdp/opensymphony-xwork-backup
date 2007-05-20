/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;

import com.opensymphony.xwork.util.location.Located;


/**
 * Configuration for InterceptorStack.
 * <p/>
 * In the xml configuration file this is defined as the <code>interceptor-stack</code> tag.
 *
 * @author Mike
 * @author Rainer Hermanns
 * @author tmjee
 * 
 * @version $Date$ $Id$
 */
public class InterceptorStackConfig extends Located implements InterceptorListHolder, Serializable {

	private static final long serialVersionUID = 2897260918170270343L;
	
	private List interceptors;  // a list of InterceptorMapping object
    private String name;

    /**
     * Creates an InterceptorStackConfig object.
     */
    public InterceptorStackConfig() {
        this.interceptors = new ArrayList();
    }

    /**
     * Creates an InterceptorStackConfig object with a particular <code>name</code>.
     * @param name
     */
    public InterceptorStackConfig(String name) {
        this();
        this.name = name;
    }

    
    /**
     * Returns a <code>Collection</code> of InterceptorMapping objects.
     * @return
     */
    public Collection getInterceptors() {
        return interceptors;
    }

    /**
     * Set the name of this interceptor stack configuration.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of this interceptor stack configuration.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Add an <code>InterceptorMapping</code> object.
     */
    public void addInterceptor(InterceptorMapping interceptor) {
        this.interceptors.add(interceptor);
    }

    /**
     * Add a List of <code>InterceptorMapping</code> objects.
     */
    public void addInterceptors(List interceptors) {
        this.interceptors.addAll(interceptors);
    }

    /**
     * An InterceptorStackConfig object is equals with <code>o</code> only if
     * <ul>
     *  <li>o is an InterceptorStackConfig object</li>
     *  <li>both names are equals</li>
     *  <li>all of their <code>InterceptorMapping</code>s are equals</li>
     * </ul>
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }

        final InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig) o;

        if ((interceptors != null) ? (!interceptors.equals(interceptorStackConfig.interceptors)) : (interceptorStackConfig.interceptors != null))
        {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorStackConfig.name)) : (interceptorStackConfig.name != null)) {
            return false;
        }

        return true;
    }

    /**
     * Generate hashcode based on <code>InterceptorStackConfig</code>'s name and its 
     * <code>InterceptorMapping</code>s.
     */
    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((interceptors != null) ? interceptors.hashCode() : 0);

        return result;
    }
}
