/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;

import java.util.List;


/**
 * A mapping set is a collection of {@link Mapping Mapping} objects categorized by type, each type
 * corresponding to a different type of data to set on the action.
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public class MappingSet {
    //~ Instance fields ////////////////////////////////////////////////////////

    /** mappings for application data */
    private List applicationMappings;

    /** mappings for parameter data */
    private List parameterMappings;

    /** mappings for request data */
    private List requestMappings;

    /** mappings for results */
    private List resultMappings;

    /** mappings for session data */
    private List sessionMappings;

    /** name of this mapping set */
    private String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * default constructor
     * @param setName The name of this mapping set
     */
    public MappingSet(String setName) {
        name = setName;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Setter for property applicationMappings.
     * @param newApplicationMappings the new value for field applicationMappings
     */
    public void setApplicationMappings(List newApplicationMappings) {
        applicationMappings = newApplicationMappings;
    }

    /**
     * Getter for property applicationMappings.
     * @return the value of field applicationMappings
     */
    public List getApplicationMappings() {
        return applicationMappings;
    }

    /**
     * Getter for property name.
     * @return the value of field name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property parameterMappings.
     * @param newParameterMappings the new value for field parameterMappings
     */
    public void setParameterMappings(List newParameterMappings) {
        parameterMappings = newParameterMappings;
    }

    /**
     * Getter for property parameterMappings.
     * @return the value of field parameterMappings
     */
    public List getParameterMappings() {
        return parameterMappings;
    }

    /**
     * Setter for property requestMappings.
     * @param newRequestMappings the new value for field requestMappings
     */
    public void setRequestMappings(List newRequestMappings) {
        requestMappings = newRequestMappings;
    }

    /**
     * Getter for property requestMappings.
     * @return the value of field requestMappings
     */
    public List getRequestMappings() {
        return requestMappings;
    }

    /**
     * Setter for property resultMappings.
     * @param newResultMappings the new value for field resultMappings
     */
    public void setResultMappings(List newResultMappings) {
        resultMappings = newResultMappings;
    }

    /**
     * Getter for property resultMappings.
     * @return the value of field resultMappings
     */
    public List getResultMappings() {
        return resultMappings;
    }

    /**
     * Setter for property sessionMappings.
     * @param newSessionMappings the new value for field sessionMappings
     */
    public void setSessionMappings(List newSessionMappings) {
        sessionMappings = newSessionMappings;
    }

    /**
     * Getter for property sessionMappings.
     * @return the value of field sessionMappings
     */
    public List getSessionMappings() {
        return sessionMappings;
    }
}
