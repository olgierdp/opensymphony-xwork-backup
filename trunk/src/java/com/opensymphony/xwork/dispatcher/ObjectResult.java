/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.util.OgnlValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;


/**
 * XWork result that writes data to a Plain Old Java Object (POJO)
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public class ObjectResult implements Result {
    //~ Static fields/initializers /////////////////////////////////////////////

    /** logger for this class */
    private static final Log log = LogFactory.getLog(ObjectResult.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    /** name of the mapping to use to map the results */
    private String mappingName;

    /** view name to retrieve from the result factory */
    private String viewName;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor
     */
    public ObjectResult() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Setter for property mappingName.
     * @param newMappingName the new value for field mappingName
     */
    public void setMappingName(String newMappingName) {
        mappingName = newMappingName;
    }

    /**
     * Getter for property mappingName.
     * @return the value of field mappingName
     */
    public String getMappingName() {
        return mappingName;
    }

    /**
     * Setter for property viewName.
     * @param newViewName the new value for field viewName
     */
    public void setViewName(String newViewName) {
        viewName = newViewName;
    }

    /**
     * Getter for property viewName.
     * @return the value of field viewName
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Write the data to the result object (or objects) based on a given mapping. Result objects
     * and mappings are obtained from factories set in the action context.
     *
     * @param invocation The invocation object to process the result for
     * @throws Exception if something goes wrong
     */
    public void execute(ActionInvocation invocation) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("executing result");
        }

        List mappings = getMappings(invocation);

        if (mappings == null) {
            log.warn("No result mappings for mapping set with name " + getMappingName());
        } else {
            Object resultObject = getResultObject(invocation);

            if (resultObject == null) {
                log.warn("No result object found with name " + getViewName());
            } else {
                copyResultData(invocation, mappings, resultObject);
            }
        }
    }

    /**
     * Get the mappings
     * @param invocation The invocation we are processing the result for
     * @throws IllegalArgumentException
     * @return List
     */
    private List getMappings(ActionInvocation invocation) throws IllegalArgumentException {
        List mappings = null;

        ActionContext context = ActionContext.getContext();
        MappingFactory mappingFactory = (MappingFactory) context.get(ObjectDispatcher.MAPPING_FACTORY);

        if (mappingFactory == null) {
            String message = "Mapping factory not set in action context";
            log.error(message);
        } else {
            MappingSet mappingSet = mappingFactory.getMappingSet(getMappingName());

            if (mappingSet == null) {
                String message = "Mapping with name " + getMappingName() + " not found.";
                log.error(message);
                throw new IllegalArgumentException(message);
            }

            mappings = mappingSet.getResultMappings();
        }

        return mappings;
    }

    /**
     * Get the result object
     * @param invocation The invocation we are processing the result for
     * @return The object to set the data on
     */
    private Object getResultObject(ActionInvocation invocation) throws IllegalArgumentException {
        Object resultObject = null;
        ActionContext context = ActionContext.getContext();
        ResultObjectFactory resultFactory = (ResultObjectFactory) context.get(ObjectDispatcher.RESULT_FACTORY);

        if (resultFactory == null) {
            String message = "Result factory not set in action context";
            log.error(message);
        } else {
            resultObject = resultFactory.getResultObject(getViewName());

            if (resultObject == null) {
                String message = "Result view with name " + getViewName() + " not found.";
                log.error(message);
                throw new IllegalArgumentException(message);
            }
        }

        return resultObject;
    }

    /**
     * Copy the data to the result object
     * @param invocation The invocation we are processing the result for
     * @param mappings The collection of mappings to use to map the data
     * @param resultObject The object to set the data on
     */
    private void copyResultData(ActionInvocation invocation, List mappings, Object resultObject) {
        //Copy the data to the result object
        OgnlValueStack sourceStack = invocation.getStack();
        OgnlValueStack resultStack = new OgnlValueStack();
        resultStack.push(resultObject);

        Iterator iterator = mappings.iterator();

        while (iterator.hasNext()) {
            Mapping mapping = (Mapping) iterator.next();
            Object dataValue = sourceStack.findValue(mapping.getActionExpression());
            resultStack.setValue(mapping.getPojoExpression(), dataValue);
        }
    }
}
