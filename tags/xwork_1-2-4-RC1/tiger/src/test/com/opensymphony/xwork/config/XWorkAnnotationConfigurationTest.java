package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.mock.MockResult;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.impl.DefaultConfiguration;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;

import junit.framework.TestCase;

import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * $Id: $
 *
 * @author $Author: $
 * @version $Revision: $
 */
public class XWorkAnnotationConfigurationTest extends TestCase {
    /**
     * DOCUMENT ME!
     */
    public void testAnnotationLoading() {
        DefaultConfiguration config = new DefaultConfiguration();

        XmlConfigurationProvider providerXml = new XmlConfigurationProvider("xwork-annotations.xml");
        providerXml.init(config);

        XWorkAnnotationConfigurationProvider provider = new XWorkAnnotationConfigurationProvider();
        provider.init(config);

        PackageConfig pkgConfig = config.getPackageConfig("/abc/def");
        assertNotNull(pkgConfig);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testCreateAction() throws Exception {
        XmlConfigurationProvider providerXml = new XmlConfigurationProvider("xwork-annotations.xml");
        XWorkAnnotationConfigurationProvider provider = new XWorkAnnotationConfigurationProvider();
        ConfigurationManager.addConfigurationProvider(providerXml);
        ConfigurationManager.addConfigurationProvider(provider);

        ActionProxy actionProxy = ActionProxyFactory.getFactory().createActionProxy("/abc/def", "TestOneAction", null);

        assertNotNull(actionProxy);
        assertEquals("/abc/def", actionProxy.getConfig().getPackageName());
        assertEquals("TestOneAction", actionProxy.getActionName());
        assertNotNull(actionProxy.getInvocation().getAction());
        assertTrue(actionProxy.getInvocation().getAction() instanceof TestOneAction);

        try {
            ActionProxyFactory.getFactory().createActionProxy("/abc/d", "TestOneAction", null);
            fail("should not go here");
        } catch (Exception e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void testConfiguration2() throws Exception {
        DefaultConfiguration config = new DefaultConfiguration();

        XmlConfigurationProvider providerXml = new XmlConfigurationProvider("xwork-annotations.xml");
        providerXml.init(config);

        XWorkAnnotationConfigurationProvider provider = new XWorkAnnotationConfigurationProvider();
        provider.init(config);

        PackageConfig pkgConfig = config.getPackageConfig("/abc/def");
        ActionConfig actionConfig = (ActionConfig) pkgConfig.getActionConfigs().get("TestTwoAction");
        Map results = actionConfig.getResults();
        assertEquals(3, results.size());

        ResultConfig resultConfig1 = (ResultConfig) results.get("success");
        assertEquals(MockResult.class.getName(), resultConfig1.getClassName());
        assertEquals("/test/one/action1.vm", resultConfig1.getParams().get(MockResult.DEFAULT_PARAM));

        ResultConfig resultConfig2 = (ResultConfig) results.get("input");
        assertEquals(MockResult.class.getName(), resultConfig2.getClassName());
        assertEquals("/test/one/action2.vm", resultConfig2.getParams().get(MockResult.DEFAULT_PARAM));

        pkgConfig = config.getPackageConfig("/abc/defg");

        ActionConfig actionConfig2 = (ActionConfig) pkgConfig.getActionConfigs().get("TestTwoAction2");
        Map results2 = actionConfig2.getResults();
        assertEquals(3, results2.size());

        ResultConfig resultConfig3 = (ResultConfig) results2.get("success");
        assertEquals(MockResult.class.getName(), resultConfig3.getClassName());
        assertEquals("TestTwoAction", resultConfig3.getParams().get(MockResult.DEFAULT_PARAM));

        ResultConfig resultConfig4 = (ResultConfig) results2.get("error");
        assertEquals(MockResult.class.getName(), resultConfig4.getClassName());
        assertEquals("/test/one/action3.vm", resultConfig4.getParams().get(MockResult.DEFAULT_PARAM));
    }
}
