/*
 * Created on Aug 13, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.entities;

import java.util.Set;

/**
 * TODO Describe View
 */
public interface View {

    /**
     * Name of view file
     *
     * @return
     */
    public String getName();

    /**
     * Returns Set of Commands linked to by this view
     *
     * @return
     */
    public Set getTargets();
}