/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import junit.framework.TestCase;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XmlConfigurationProviderInterceptorParamOverridingTest extends TestCase {

	public void testParamOveriding() throws Exception {
		DefaultConfiguration conf = new DefaultConfiguration();
		final XmlConfigurationProvider p = new XmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-param-overriding.xml");
		conf.reload(new ArrayList<ConfigurationProvider>(){
			{
				add(p);
			}
		});
		
		RuntimeConfiguration rtConf = conf.getRuntimeConfiguration();
		
		ActionConfig actionOne = rtConf.getActionConfig("", "actionOne");
		ActionConfig actionTwo = rtConf.getActionConfig("", "actionTwo");
		
		List<InterceptorMapping> actionOneInterceptors = actionOne.getInterceptors();
		List<InterceptorMapping> actionTwoInterceptors = actionTwo.getInterceptors();
		
		assertNotNull(actionOne);
		assertNotNull(actionTwo);
		assertNotNull(actionOneInterceptors);
		assertNotNull(actionTwoInterceptors);
		assertEquals(actionOneInterceptors.size(), 3);
		assertEquals(actionTwoInterceptors.size(), 3);
		
		InterceptorMapping actionOneInterceptorMapping1 = actionOneInterceptors.get(0);
		InterceptorMapping actionOneInterceptorMapping2 = actionOneInterceptors.get(1);
		InterceptorMapping actionOneInterceptorMapping3 = actionOneInterceptors.get(2);
		InterceptorMapping actionTwoInterceptorMapping1 = actionTwoInterceptors.get(0);
		InterceptorMapping actionTwoInterceptorMapping2 = actionTwoInterceptors.get(1);
		InterceptorMapping actionTwoInterceptorMapping3 = actionTwoInterceptors.get(2);
		
		assertNotNull(actionOneInterceptorMapping1);
		assertNotNull(actionOneInterceptorMapping2);
		assertNotNull(actionOneInterceptorMapping3);
		assertNotNull(actionTwoInterceptorMapping1);
		assertNotNull(actionTwoInterceptorMapping2);
		assertNotNull(actionTwoInterceptorMapping3);
		
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping1.getInterceptor()).getParamOne(), "i1p1");
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping1.getInterceptor()).getParamTwo(), "i1p2");
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping2.getInterceptor()).getParamOne(), "i2p1");
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping2.getInterceptor()).getParamTwo(), null);
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping3.getInterceptor()).getParamOne(), null);
		assertEquals(((InterceptorForTestPurpose)actionOneInterceptorMapping3.getInterceptor()).getParamTwo(), null);
		
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping1.getInterceptor()).getParamOne(), null);
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping1.getInterceptor()).getParamTwo(), null);
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping2.getInterceptor()).getParamOne(), null);
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping2.getInterceptor()).getParamTwo(), "i2p2");
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping3.getInterceptor()).getParamOne(), "i3p1");
		assertEquals(((InterceptorForTestPurpose)actionTwoInterceptorMapping3.getInterceptor()).getParamTwo(), "i3p2");
		
	}
}
