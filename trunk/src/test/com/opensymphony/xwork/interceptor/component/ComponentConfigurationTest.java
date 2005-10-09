/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author $Author$
 * @version $Revision$
 */
public class ComponentConfigurationTest extends TestCase {

    public void testLoadFromXml() throws IOException, SAXException {
        ComponentConfiguration cc = new ComponentConfiguration();
        String configText = "<components>\n" + "<component>\n" + "<class>" + Foo.class.getName() + "</class>\n" + "<scope>foo</scope>\n" + "<enabler>" + FooAware.class.getName() + "</enabler>\n" + "</component>\n" + "<component>\n" + "<class>" + Bar.class.getName() + "</class>\n" + "<scope>bar</scope>\n" + "<enabler>" + BarAware.class.getName() + "</enabler>\n" + "</component>\n" + "</components>\n";

        cc.loadFromXml(new ByteArrayInputStream(configText.getBytes()));

        DumbComponentManager dcm1 = new DumbComponentManager();
        DumbComponentManager dcm2 = new DumbComponentManager();
        cc.configure(dcm1, "foo");
        cc.configure(dcm2, "bar");

        assertEquals(1, dcm1.enablers.size());
        assertEquals(1, dcm1.resources.size());
        assertEquals(1, dcm2.enablers.size());
        assertEquals(1, dcm2.resources.size());
    }


    public class DumbComponentManager implements ComponentManager {
        ArrayList enablers = new ArrayList();
        ArrayList initialized = new ArrayList();
        ArrayList resources = new ArrayList();

        public Object getComponent(Class enablerType) {
            return null;
        }

        public void setFallback(ComponentManager fallback) {
        }

        public void addEnabler(Class resource, Class enablerType) {
            resources.add(resource);
            enablers.add(enablerType);
        }

        public void dispose() {
        }

        public void initializeObject(Object component) {
            initialized.add(component);
        }

        public void registerInstance(Class componentType, Object instance) {

        }

        public Object getComponentInstance(Class componentType) {
            return null;
        }

        public ComponentConfiguration getConfig() {
            return null;
        }

        public void setConfig(ComponentConfiguration config) {
        }

        public void setScope(String scope) {
        }

        public void reset() {
        }
    }
}
