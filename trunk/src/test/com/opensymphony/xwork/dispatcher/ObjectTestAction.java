/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;

import com.opensymphony.xwork.ActionSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Test action for object dispatching
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public class ObjectTestAction extends ActionSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    /** logger for this class */
    protected Log log = LogFactory.getLog(ObjectTestAction.class);

    /** blah text property */
    private String blah;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ObjectTestAction() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
 * Setter for property blah.
 * @param newBlah the new value for field blah
 */
    public void setBlah(String newBlah) {
        blah = newBlah;
    }

    /**
 * Getter for property blah.
 * @return the value of field blah
 */
    public String getBlah() {
        return blah;
    }

    /**
 * This methodName is where the logic of the action is executed.
 *
 * @return a string representing the logical result of the execution. See constants in this
 *   interface for a list of standard result values.
 * @throws Exception thrown if a system level exception occurs. Application level exceptions
 *   should be handled by returning an error value, such as Action.ERROR.
 * @todo Implement this com.opensymphony.xwork.Action method
 */
    public String execute() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Blah was " + getBlah());
        }

        return SUCCESS;
    }
}
