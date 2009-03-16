/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.interceptor.TimerInterceptor;
import com.opensymphony.xwork.spring.SpringObjectFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderInterceptorsSpringTest extends ConfigurationTestBase {

    InterceptorConfig timerInterceptor = new InterceptorConfig("timer", TimerInterceptor.class, new HashMap());
    ObjectFactory objectFactory = ObjectFactory.getObjectFactory();
    StaticApplicationContext sac;


    public void testInterceptorsLoadedFromSpringApplicationContext() throws ConfigurationException {
        sac.registerSingleton("timer-interceptor", TimerInterceptor.class, new MutablePropertyValues());

        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-interceptors-spring.xml";

        // Expect a ConfigurationException to be thrown if the interceptor reference
        // cannot be resolved
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map interceptorConfigs = pkg.getInterceptorConfigs();

        // assertions for size
        assertEquals(1, interceptorConfigs.size());

        // assertions for interceptors
        InterceptorConfig seen = (InterceptorConfig) interceptorConfigs.get("timer");
        assertEquals("timer-interceptor", seen.getClassName());
    }

    protected void setUp() throws Exception {
        super.setUp();

        sac = new StaticApplicationContext();

        SpringObjectFactory objFactory = new SpringObjectFactory();
        objFactory.setApplicationContext(sac);
        ObjectFactory.setObjectFactory(objFactory);

        ConfigurationManager.destroyConfiguration();
    }
}
