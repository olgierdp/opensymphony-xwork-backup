/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.interceptor.TimerInterceptor;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DefaultDelegatingHandler;
import com.opensymphony.xwork.xml.DelegatingHandler;
import com.opensymphony.xwork.xml.Path;

import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * PackageHandlerTest
 * @author Jason Carreira
 * Created May 22, 2003 8:39:10 PM
 */
public class PackageHandlerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    Configuration configuration;
    Mock configurationMock;
    PackageHandler handler;
    Path fooPath = Path.getInstance("/foo");

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        configurationMock = new Mock(Configuration.class);
        configuration = (Configuration) configurationMock.proxy();
        handler = new PackageHandler(fooPath, configuration);
    }

    public void testInterceptorConfig() {
        DelegatingHandler parentHandler = new DefaultDelegatingHandler();
        handler.registerWith(parentHandler);

        try {
            Map params = new HashMap();
            final String name = "defaultPackage";
            params.put("name", name);

            final String namespace = "/namespace1";
            params.put("namespace", namespace);

            parentHandler.startElement(null, null, "foo", null);
            parentHandler.startElement(null, null, "interceptors", null);

            params = new HashMap();
            params.put("name", "timer");
            params.put("class", "com.opensymphony.xwork.interceptor.TimerInterceptor");

            Attributes attributes = new MockAttributes(params);
            parentHandler.startElement(null, null, "interceptor", attributes);

            final OgnlValueStack valueStack = handler.getValueStack();
            assertEquals(2, valueStack.size());

            Object interceptorConfig = valueStack.peek();
            assertTrue(interceptorConfig instanceof InterceptorConfig);
            parentHandler.endElement(null, null, "interceptor");
            parentHandler.endElement(null, null, "interceptors");
            assertEquals(1, valueStack.size());

            PackageConfig packageConfig = (PackageConfig) valueStack.peek();
            Map interceptorConfigs = packageConfig.getInterceptorConfigs();
            assertEquals(1, interceptorConfigs.size());
            assertTrue(interceptorConfigs.containsKey("timer"));

            InterceptorConfig config = (InterceptorConfig) interceptorConfigs.get("timer");
            assertEquals(TimerInterceptor.class, config.getClazz());
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPackageConfig() {
        List expectedParents = new ArrayList();
        PackageConfig parentConfig = new PackageConfig();
        expectedParents.add(parentConfig);

        String extendsString = "extendsString";
        configurationMock.expectAndReturn("getPackageConfig", extendsString, parentConfig);

        Map params = new HashMap();
        final String name = "defaultPackage";
        params.put("name", name);

        final String namespace = "/namespace1";
        params.put("namespace", namespace);
        params.put("extends", extendsString);
        params.put("abstract", "true");
        handler.startingPath(fooPath, params);

        PackageConfig config = handler.getPackageConfig();
        assertEquals(name, config.getName());
        assertEquals(config.getNamespace(), namespace);

        List parents = config.getParents();
        assertNotNull(parents);
        assertEquals(1, parents.size());
        assertEquals(parentConfig, parents.get(0));
        assertTrue(config.isAbstract());

        PackageConfig expectedConfig = new PackageConfig(name, namespace, true, expectedParents);
        configurationMock.expect("addPackageConfig", C.args(C.eq(name), C.eq(expectedConfig)));

        handler.endingPath(fooPath);

        configurationMock.verify();
    }
}
