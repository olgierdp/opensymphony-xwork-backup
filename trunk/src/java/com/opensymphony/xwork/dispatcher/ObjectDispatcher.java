/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.util.OgnlValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class dispatches XWork actions based on data contained in POJO objects
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public class ObjectDispatcher {
    //~ Static fields/initializers /////////////////////////////////////////////

    /** Logger for this class. */
    protected static final Log LOG = LogFactory.getLog(ObjectDispatcher.class);

    /** key for the mapping factory in the context */
    public static final String MAPPING_FACTORY = "com.opensymphony.xwork.ActionContext.mappings";

    /** key for the result factory in the context */
    public static final String RESULT_FACTORY = "com.opensymphony.xwork.ActionContext.results";

    //~ Instance fields ////////////////////////////////////////////////////////

    /** mappings to use with this dispatcher */
    private MappingFactory mappings;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor
     * @param mappingFactory The factory to use to get the mappings between POJO's and actions
     */
    public ObjectDispatcher(MappingFactory mappingFactory) {
        mappings = mappingFactory;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Merges all application attributes into a single <tt>HashMap</tt> to represent the
     * entire <tt>Action</tt> context.
     *
     * @param requestMap a Map of all request attributes.
     * @param parameterMap a Map of all request parameters.
     * @param sessionMap a Map of all session attributes.
     * @param applicationMap a Map of all servlet context attributes.
     * @return a HashMap representing the <tt>Action</tt> context.
     */
    public static HashMap createContextMap(Map requestMap, Map parameterMap, Map sessionMap, Map applicationMap) {
        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, parameterMap);
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);

        // helpers to get access to request/session/application scope
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameterMap);

        return extraContext;
    }

    /**
     * Dispatch the action invocation
     *
     * @param source The object to copy the data from
     * @param namespace The namespace of the action to execute (may be null)
     * @param action The name of the action to execute
     * @param mappingName The name of the mapping to use to copy the data
     * @param resultObjectFactory The factory to use to set values on the result
     * @throws Exception If there is a problem finding or executing the action
     * @return The value stack that is the result of the execution. Note that if there is a result
     * of type <code>object</code> then values will already have been set on objects obtained from
     * <code>resultObjectFactory</code>.
     */
    public OgnlValueStack dispatch(Object source, String namespace, String action, String mappingName, ResultObjectFactory resultObjectFactory) throws Exception {
        //Get the mappings
        MappingSet mappingSet = mappings.getMappingSet(mappingName);

        if (mappingSet == null) {
            throw new ConfigurationException("Mapping with name " + mappingName + " does not exist");
        }

        Map sessionMap = getAttributeMap(mappingSet.getSessionMappings(), source);
        Map requestMap = getAttributeMap(mappingSet.getRequestMappings(), source);
        Map parameterMap = getAttributeMap(mappingSet.getParameterMappings(), source);
        Map applicationMap = getAttributeMap(mappingSet.getApplicationMappings(), source);

        Map extraContext = createContextMap(requestMap, parameterMap, sessionMap, applicationMap);

        extraContext.put(MAPPING_FACTORY, mappings);
        extraContext.put(RESULT_FACTORY, resultObjectFactory);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(namespace, action, extraContext);
            proxy.execute();

            return proxy.getInvocation().getStack();
        } catch (ConfigurationException e) {
            LOG.error("Could not find action", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Could not execute action", e);
            throw e;
        }
    }

    /**
     * Get a map containing data values from an object given a set of mappings
     * @param mappingList <tt>List</tt> containing {@link Mapping Mapping} objects
     * @param pojo The java object to use to get the values
     * @return A <tt>map</tt> containing the data values specified by the pojoExpression properties
     * of the mappings and keyed by the corresponding actionExpression properties
     */
    private Map getAttributeMap(List mappingList, Object pojo) {
        Map result = new HashMap();

        if (mappingList == null) {
            // Nothing to do
            return result;
        } else {
            OgnlValueStack sourceStack = new OgnlValueStack();
            sourceStack.push(pojo);

            Iterator iterator = mappingList.iterator();

            while (iterator.hasNext()) {
                Mapping mapping = (Mapping) iterator.next();
                result.put(mapping.getActionExpression(), sourceStack.findValue(mapping.getPojoExpression()));
            }

            return result;
        }
    }
}
