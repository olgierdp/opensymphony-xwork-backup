/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ActionChainResult;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.ParametersInterceptor;
import junit.framework.Assert;


/**
 * Verify that Interceptor inheritance is happy for multi-level package derivations
 *
 * @author $Author$
 * @version $Revision$
 */
public class XmlConfigurationProviderMultilevelTest extends ConfigurationTestBase {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * attempt to load an xwork.xml file that has multilevel levels of inheritance and verify that the interceptors are
     * correctly propagated through.
     *
     * @throws Exception
     */
    public void testMultiLevelInheritance() throws Exception {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-multilevel.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);

        /**
         * for this test, we expect the action named, action3, in the namespace, namespace3, to have a single
         * ParameterInterceptor.  The ParameterInterceptor, param, has been defined far up namespace3's parentage ...
         * namespace3 -> namespace2 -> namespace1 -> default
         */
        PackageConfig packageConfig = configuration.getPackageConfig("namespace3");
        Assert.assertNotNull(packageConfig);
        assertEquals(2, packageConfig.getAllInterceptorConfigs().size());

        ActionConfig actionConfig = (ActionConfig) packageConfig.getActionConfigs().get("action3");

        assertNotNull(actionConfig);
        assertNotNull(actionConfig.getInterceptors());
        assertEquals(2, actionConfig.getInterceptors().size());
        assertEquals(ParametersInterceptor.class, actionConfig.getInterceptors().get(0).getClass());
        assertNotNull(actionConfig.getResults());
        assertEquals(1, actionConfig.getResults().size());
        assertTrue(actionConfig.getResults().containsKey("success"));

        ResultConfig resultConfig = (ResultConfig) actionConfig.getResults().get("success");
        assertEquals(ActionChainResult.class.getName(), resultConfig.getClassName());
    }
}
