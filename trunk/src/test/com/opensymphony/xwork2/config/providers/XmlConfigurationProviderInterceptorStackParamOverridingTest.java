package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * <code>XmlConfigurationProviderInterceptorStackParamOverridingTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: $
 */
public class XmlConfigurationProviderInterceptorStackParamOverridingTest extends XWorkTestCase {
    
    public void testInterceptorStackParamOveriding() throws Exception {
    	DefaultConfiguration conf = new DefaultConfiguration();
    	final XmlConfigurationProvider p = new XmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-stack-param-overriding.xml");
    	configurationManager.addContainerProvider(p);
        conf.reload(new ArrayList<ConfigurationProvider>(){
            {
                add(new XWorkConfigurationProvider());
                add(p);
            }
        });


    	RuntimeConfiguration rtConf = conf.getRuntimeConfiguration();

    	ActionConfig actionOne = rtConf.getActionConfig("", "actionOne");
    	ActionConfig actionTwo = rtConf.getActionConfig("", "actionTwo");

    	List actionOneInterceptors = actionOne.getInterceptors();
    	List actionTwoInterceptors = actionTwo.getInterceptors();

    	assertNotNull(actionOne);
    	assertNotNull(actionTwo);
    	assertNotNull(actionOneInterceptors);
    	assertNotNull(actionTwoInterceptors);
    	assertEquals(actionOneInterceptors.size(), 3);
    	assertEquals(actionTwoInterceptors.size(), 3);

    	InterceptorMapping actionOneInterceptorMapping1 = (InterceptorMapping) actionOneInterceptors.get(0);
    	InterceptorMapping actionOneInterceptorMapping2 = (InterceptorMapping) actionOneInterceptors.get(1);
    	InterceptorMapping actionOneInterceptorMapping3 = (InterceptorMapping) actionOneInterceptors.get(2);
    	InterceptorMapping actionTwoInterceptorMapping1 = (InterceptorMapping) actionTwoInterceptors.get(0);
    	InterceptorMapping actionTwoInterceptorMapping2 = (InterceptorMapping) actionTwoInterceptors.get(1);
    	InterceptorMapping actionTwoInterceptorMapping3 = (InterceptorMapping) actionTwoInterceptors.get(2);

    	assertNotNull(actionOneInterceptorMapping1);
    	assertNotNull(actionOneInterceptorMapping2);
    	assertNotNull(actionOneInterceptorMapping3);
    	assertNotNull(actionTwoInterceptorMapping1);
    	assertNotNull(actionTwoInterceptorMapping2);
    	assertNotNull(actionTwoInterceptorMapping3);


    	assertInterceptorParamOneEquals(actionOne, "interceptorOne", "i1p1");
    	assertInterceptorParamTwoEquals(actionOne, "interceptorOne", "i1p2");
    	assertInterceptorParamOneEquals(actionOne, "interceptorTwo", "i2p1" );
    	assertInterceptorParamTwoEquals(actionOne, "interceptorTwo", null);
    	assertInterceptorParamOneEquals(actionOne, "interceptorThree", null);
    	assertInterceptorParamTwoEquals(actionOne, "interceptorThree", null);

    	assertInterceptorParamOneEquals(actionTwo, "interceptorOne", null);
    	assertInterceptorParamTwoEquals(actionTwo, "interceptorOne", null);
    	assertInterceptorParamOneEquals(actionTwo, "interceptorTwo", null);
    	assertInterceptorParamTwoEquals(actionTwo, "interceptorTwo", "i2p2");
    	assertInterceptorParamOneEquals(actionTwo, "interceptorThree", "i3p1");
    	assertInterceptorParamTwoEquals(actionTwo, "interceptorThree", "i3p2");

    }

    protected void assertInterceptorParamOneEquals(ActionConfig actionConfig,String interceptorName,  String paramValue) {
    	List interceptorMappings = actionConfig.getInterceptors();
    	for (Iterator i = interceptorMappings.iterator(); i.hasNext();) {
    		InterceptorMapping interceptorMapping = (InterceptorMapping) i.next();
    		assertNotNull(interceptorMapping.getInterceptor());
    		if (interceptorMapping.getName().equals(interceptorName)) {
    			assertEquals(((InterceptorForTestPurpose)interceptorMapping.getInterceptor()).getParamOne(), paramValue);
    			return;
    		}
    	}
    	fail();
    }

    protected void assertInterceptorParamTwoEquals(ActionConfig actionConfig, String interceptorName, String paramValue) {
    	List interceptorMappings = actionConfig.getInterceptors();
    	for (Iterator i = interceptorMappings.iterator(); i.hasNext();) {
    		InterceptorMapping interceptorMapping = (InterceptorMapping) i.next();
    		assertNotNull(interceptorMapping.getInterceptor());
    		if (interceptorMapping.getName().equals(interceptorName)) {
    			assertEquals(((InterceptorForTestPurpose)interceptorMapping.getInterceptor()).getParamTwo(), paramValue);
    			return;
    		}
    	}
    	fail();
    }

    protected void tearDown() throws Exception {
    	configurationManager.clearContainerProviders();
    }
}
