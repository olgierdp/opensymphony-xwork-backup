/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;

import com.opensymphony.xwork.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;


/**
 * @author joew@thoughtworks.com
 * @author $Author$
 * @version $Revision$
 */
public class ComponentConfiguration implements Serializable {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(ComponentConfiguration.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private Map componentsByScope = new HashMap();

    //~ Methods ////////////////////////////////////////////////////////////////

    public void addComponentDefinition(String className, String scope, String enablerClass) {
        getComponents(scope).add(new ComponentDefinition(className, enablerClass));
    }

    /**
     * Configure a newly instantiated component manager by initializing all of the required components for the
     * current configuration and setting up the component enablers.
     *
     * @param componentManager
     */
    public void configure(ComponentManager componentManager, String scope) {
        componentManager.reset();
        for (Iterator iterator = getComponents(scope).iterator();
             iterator.hasNext();) {
            ComponentDefinition componentDefinition = (ComponentDefinition) iterator.next();

            Class resource = loadClass(componentDefinition.className);
            Class enabler = loadClass(componentDefinition.enablerClass);
            componentManager.addEnabler(resource, enabler);
        }
        componentManager.setConfig(this);
        componentManager.setScope(scope);
    }

    public boolean hasComponents(String scope) {
        return componentsByScope.containsKey(scope);
    }

    public void loadFromXml(InputStream is) throws IOException, SAXException {
        DocumentBuilder db = null;

        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("ParserConfigurationException occured", e);
        } catch (FactoryConfigurationError factoryConfigurationError) {
            log.error("FactoryConfigurationError occured", factoryConfigurationError);
        }

        Element componentsElement = db.parse(is).getDocumentElement();
        NodeList components = componentsElement.getChildNodes();

        for (int i = 0; i < components.getLength(); i++) {
            Node componentNode = components.item(i);

            if (componentNode instanceof Element) {
                Element componentElement = (Element) componentNode;
                NodeList componentElementChildren = componentElement.getChildNodes();

                String className = null;
                String scope = null;
                String enabler = null;

                for (int j = 0; j < componentElementChildren.getLength();
                     j++) {
                    Node elementChildNode = componentElementChildren.item(j);

                    if (elementChildNode instanceof Element) {
                        Element childElement = (Element) elementChildNode;

                        if ("class".equals(childElement.getNodeName())) {
                            className = childElement.getChildNodes().item(0).getNodeValue();
                        } else if ("scope".equals(childElement.getNodeName())) {
                            scope = childElement.getChildNodes().item(0).getNodeValue();
                        } else if ("enabler".equals(childElement.getNodeName())) {
                            enabler = childElement.getChildNodes().item(0).getNodeValue();
                        }
                    }
                }

                if ((className != null) && (scope != null) && (enabler != null)) {
                    addComponentDefinition(className.trim(), scope.trim(), enabler.trim());
                }
            }
        }
    }

    private List getComponents(String scope) {
        if (!componentsByScope.containsKey(scope)) {
            componentsByScope.put(scope, new ArrayList(10));
        }

        return (List) componentsByScope.get(scope);
    }

    private Class loadClass(String enablerClass) {
        try {
            return ObjectFactory.getObjectFactory().getClassInstance(enablerClass);
        } catch (ClassNotFoundException e) {
            log.fatal("Cannot load class : " + enablerClass, e);
            throw new RuntimeException("Cannot load class : " + enablerClass);
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private class ComponentDefinition implements Serializable {
        private String className;
        private String enablerClass;

        public ComponentDefinition(String className, String enablerClass) {
            this.enablerClass = enablerClass;
            this.className = className;
        }
    }
}
