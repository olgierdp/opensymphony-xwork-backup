/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.Map;


/**
 * DelegatingHandler
 * @author Jason Carreira
 * Created May 15, 2003 10:30:57 PM
 */
public interface DelegatingHandler extends SubHandler {
    //~ Methods ////////////////////////////////////////////////////////////////

    // returns the root handler's exception, used by the above 3 methods
    DelegatingHandlerException getException();

    /**
    * Gets a property from the property map with the associated key
    */
    Object getProperty(Object key);

    OgnlValueStack getValueStack();

    /**
    * Add all of the properties in this map to the DefaultDelegatingHandler properties
    * @param props Map of properties to add
    */
    void addAllProperties(Map props);

    /**
    * Add a validation error to the validation error collection.
    * @param error The encountered error.
    */
    void addError(Exception error);

    /**
    * Add a validation error to the validation error collection.
    * @param fatal The encountered error.
    */
    void addFatal(Exception fatal);

    /**
    * Registers a handler with the given path to receive delegated SAX events received under that path
    *
    * @param path The Path to register as the root path to delegate messages to the SubHandler
    * @param handler The SubHandler to receive delegated SAX events
    */
    void addHandler(Path path, SubHandler handler);

    /**
    * Adds a property to the property map for use by SubHandlers while processing
    *
    * @param key
    * @param value
    */
    void addProperty(Object key, Object value);

    /**
    * Add a validation warning to the validation warning collection.
    * @param warning The encountered warning.
    */
    void addWarning(Exception warning);

    Object findValue(String expression);

    /**
    * Unregisters the given handler from the DefaultDelegatingHandler with the given path. <p>
    * NOTE: The handler will also stop receiving SAX events, EVEN IF IT IS RECEIVING MESSAGES BASED ON A DIFFERENT PATH
    * REGISTRATION, therefore, it is not recommended that RemoveHandler be called at runtime if a handler is registered
    * with multiple paths.
    *
    * @param path The Path to remove the handler from
    * @param handler The Handler to register to receive SAX events
    */
    void removeHandler(Path path, SubHandler handler);
}
