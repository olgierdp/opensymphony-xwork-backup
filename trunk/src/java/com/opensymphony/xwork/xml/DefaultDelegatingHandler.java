/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import com.opensymphony.xwork.util.OgnlValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.*;

import java.util.*;


/**
 * DefaultDelegatingHandler enables SubHandlers to register themselves to receive SAX messages for a certain path in the XML
 * file. When that path is reached, the startingPath() method will be called on the registered SubHandler, followed by
 * the startElement() event. All SAX events will be forwarded to the registered SubHandler from the time the path is
 * reached to the time the closing tag for the path is reached, at which time the endingPath() event will be sent to the
 * registered SubHandler (immediately after the endElement() event) and no further SAX messages will be sent. SubHandlers
 * which wish to receive all SAX events, including startDocument and endDocument should register using
 * Path.getRootInstance(). Current SAX events which are delegated include startDocument, endDocument, startElement,
 * endElement, and characters.
 *
 * Created Oct 21, 2002 3:27:13 PM
 * @author Jason Carreira
 * @version 1.0
 */
public class DefaultDelegatingHandler extends DefaultSubHandler implements DelegatingHandler {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected static final Log LOG = LogFactory.getLog(DefaultDelegatingHandler.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    protected boolean startingPath = false;

    // one large exception to accumulate multiple exceptions, only thrown
    // if errors have occurred; only the root handler will have one
    private DelegatingHandlerException exception = null;

    /** The map of properties set into the DefaultDelegatingHandler to be accessed by SubHandlers at runtime */
    private Map properties;

    /**
    * The collection of registrations between Paths and SubHandlers. Multiple SubHandlers may be registered with the
    * same Path
    */
    private Map registry = new HashMap(1);

    /** The current path in the parsed XML document. This is updated by start and end element events */
    private Path path = Path.getRootInstance();

    /**
    * The set of SubHandlers whose path's have been reached but not closed yet. These SubHandlers will receive all SAX
    * events received by the DefaultDelegatingHandler
    */
    private Set activeHandlers = new HashSet(1);

    //~ Constructors ///////////////////////////////////////////////////////////

    public DefaultDelegatingHandler() {
        this(Path.getRootInstance());
    }

    public DefaultDelegatingHandler(Path path) {
        super(path);
        init();
    }

    public DefaultDelegatingHandler(String pathString) {
        super(pathString);
        init();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    // returns the root handler's exception, used by the above 3 methods
    public DelegatingHandlerException getException() {
        if (exception == null) {
            if ((rootHandler != null) && (rootHandler != this)) {
                exception = rootHandler.getException();
            } else {
                exception = new DelegatingHandlerException();
            }
        }

        return exception;
    }

    /**
    * Get the current working path
    * @return the current working path
    */
    public Path getPath() {
        return path;
    }

    public Map getProperties() {
        return properties;
    }

    /**
    * Gets a property from the property map with the associated key
    */
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public OgnlValueStack getValueStack() {
        if (rootHandler == null) {
            return (OgnlValueStack) properties.get(OgnlValueStack.VALUE_STACK);
        } else {
            return rootHandler.getValueStack();
        }
    }

    /**
    * Add all of the properties in this map to the DefaultDelegatingHandler properties
    * @param props Map of properties to add
    */
    public void addAllProperties(Map props) {
        properties.putAll(props);
    }

    /**
    * Add a validation error to the validation error collection.
    * @param error The encountered error.
    */
    public void addError(Exception error) {
        getException().addError(error);
    }

    /**
    * Add a validation error to the validation error collection.
    * @param fatal The encountered error.
    */
    public void addFatal(Exception fatal) {
        // clear out everything in the delgating handler to short-circuit the parse
        registry.clear();
        activeHandlers.clear();
        getException().addFatal(fatal);
    }

    /**
    * Registers a handler with the given path to receive delegated SAX events received under that path
    *
    * @param path The Path to register as the root path to delegate messages to the SubHandler
    * @param handler The SubHandler to receive delegated SAX events
    */
    public void addHandler(Path path, SubHandler handler) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding handler " + handler + " with path " + path);
        }

        List handlers = getHandlersForPath(path);

        // make sure we only have this handler for this path once
        handlers.remove(handler);
        handlers.add(handler);
    }

    /**
    * Adds a property to the property map for use by SubHandlers while processing
    *
    * @param key
    * @param value
    */
    public void addProperty(Object key, Object value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding property " + key + " with value " + value);
        }

        properties.put(key, value);
    }

    /**
    * Add a validation warning to the validation warning collection.
    * @param warning The encountered warning.
    */
    public void addWarning(Exception warning) {
        getException().addWarning(warning);
    }

    /**
    * Delegates character event calls to the active SubHandlers
    *
    * @param chars
    * @param start
    * @param end
    * @throws org.xml.sax.SAXException
    */
    public void characters(char[] chars, int start, int end) throws SAXException {
        try {
            for (Iterator iterator = activeHandlers.iterator();
                    iterator.hasNext();) {
                ContentHandler handler = (ContentHandler) iterator.next();
                handler.characters(chars, start, end);
            }
        } catch (SAXException e) {
            addError(e);
            throw e;
        }
    }

    /**
    * Delegates the endDocument event to SubHandlers registered with the root path and deactivates them
    *
    * @throws org.xml.sax.SAXException
    */
    public void endDocument() throws SAXException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ending Document.");
        }

        try {
            for (Iterator iterator = activeHandlers.iterator();
                    iterator.hasNext();) {
                ContentHandler handler = (ContentHandler) iterator.next();
                handler.endDocument();
            }
        } catch (SAXException e) {
            addError(e);
            throw e;
        }

        Collection c = (Collection) registry.get(path);

        if (c != null) {
            notifyEndingHandlers(c);
            activeHandlers.removeAll(c);
        }
    }

    /**
    * Updates the current Path, delegates to the active SubHandlers, and deactivates the SubHandlers registered with this path
    *
    * @param uri
    * @param localName
    * @param qName
    * @throws org.xml.sax.SAXException
    */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            for (Iterator iterator = activeHandlers.iterator();
                    iterator.hasNext();) {
                ContentHandler handler = (ContentHandler) iterator.next();
                handler.endElement(uri, localName, qName);
            }
        } catch (SAXException e) {
            addError(e);
            throw e;
        }

        Collection c = (Collection) registry.get(path);

        if (c != null) {
            notifyEndingHandlers(c);
            activeHandlers.removeAll(c);
        }

        path.remove();
    }

    /**
    * required handler for SAX errors; logs the error, adds it to a list,
    * and continues processing. once processing completes, an exception
    * containing all accumulated warnings and errors will be thrown
    */
    public void error(SAXParseException e) throws SAXException {
        LOG.warn("Caught Error while parsing XML.", e);
        this.addError(e);
    }

    /**
    * required handler for fatal SAX errors; logs the error, adds it to a list,
    * and throws the exception with all accumulated warnings and errors
    * (including this one)
    */
    public void fatalError(SAXParseException e) throws SAXException {
        LOG.error("Caught FATAL Error while parsing XML.", e);
        this.addFatal(e);
    }

    public Object findValue(String expression) {
        OgnlValueStack stack = getValueStack();

        if (stack != null) {
            return stack.findValue(expression);
        }

        return null;
    }

    /**
    * Unregisters the given handler from the DefaultDelegatingHandler with the given path. <p>
    * NOTE: The handler will also stop receiving SAX events, EVEN IF IT IS RECEIVING MESSAGES BASED ON A DIFFERENT PATH
    * REGISTRATION, therefore, it is not recommended that RemoveHandler be called at runtime if a handler is registered
    * with multiple paths.
    *
    * @param path The Path to remove the handler from
    * @param handler The Handler to register to receive SAX events
    */
    public void removeHandler(Path path, SubHandler handler) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing handler " + handler + " with path " + path);
        }

        List handlers = getHandlersForPath(path);
        handlers.remove(handler);
        activeHandlers.remove(handler);
    }

    /**
    * Activates SubHandlers registered with the Root Path and delegates the startDocument event to them
    *
    * @throws org.xml.sax.SAXException
    */
    public void startDocument() throws SAXException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting document.");
        }

        Collection c = (Collection) registry.get(path);

        if (c != null) {
            activeHandlers.addAll(c);
            notifyStartingHandlers(c, null);
        }

        try {
            for (Iterator iterator = activeHandlers.iterator();
                    iterator.hasNext();) {
                ContentHandler handler = (ContentHandler) iterator.next();
                handler.startDocument();
            }
        } catch (SAXException e) {
            addError(e);
            throw e;
        }
    }

    /**
    * Updates the current Path, activates the SubHandlers registered with this path, and delegates to the
    * active SubHandlers.
    *
    * @param uri
    * @param localName
    * @param qName
    * @param attributes
    * @throws org.xml.sax.SAXException
    */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // only add this to the path if this is not the path we are registered to listen under.
        if (startingPath) {
            startingPath = false;
        } else {
            path.add(qName);
        }

        Collection c = (Collection) registry.get(path);

        if (c != null) {
            activeHandlers.addAll(c);
            notifyStartingHandlers(c, attributes);
        }

        try {
            for (Iterator iterator = activeHandlers.iterator();
                    iterator.hasNext();) {
                ContentHandler handler = (ContentHandler) iterator.next();
                handler.startElement(uri, localName, qName, attributes);
            }
        } catch (SAXException e) {
            addError(e);
            throw e;
        }
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        super.startingPath(path, attributeMap);

        // set the starting path flag
        startingPath = true;
    }

    /**
    * required handler for SAX warnings; logs the warning, adds it to a list,
    * and continues processing
    */
    public void warning(SAXParseException e) throws SAXException {
        LOG.warn("Caught Warning while parsing XML.", e);
        this.addWarning(e);
    }

    protected List getHandlersForPath(Path path) {
        List handlers = (List) registry.get(path);

        if (handlers == null) {
            handlers = new ArrayList();
            registry.put(path, handlers);
        }

        return handlers;
    }

    protected void init() {
        OgnlValueStack valueStack = new OgnlValueStack();
        properties = valueStack.getContext();
    }

    Map buildAttributeMap(Attributes attributes) {
        Map returnMap = new HashMap();

        if (attributes != null) {
            for (int i = 0, c = attributes.getLength(); i < c; i++) {
                returnMap.put(attributes.getQName(i), attributes.getValue(i));
            }
        }

        return returnMap;
    }

    /**
    * Calls endingPath() on each of the SubHandlers in the Collection
    *
    * @param c Collection of SubHandler objects
    */
    private void notifyEndingHandlers(Collection c) {
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            SubHandler handler = (SubHandler) iterator.next();
            handler.endingPath(path);
        }
    }

    /**
    * Calls startingPath() on each of the SubHandlers in the Collection
    *
    * @param c Collection of SubHandler objects
    */
    private void notifyStartingHandlers(Collection c, Attributes attributes) {
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            SubHandler handler = (SubHandler) iterator.next();
            handler.startingPath(path, buildAttributeMap(attributes));
        }
    }
}
