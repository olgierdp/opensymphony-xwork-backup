package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProviderInterceptorsSpringTest;
import com.opensymphony.xwork2.spring.interceptor.ActionAutowiringInterceptorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collect all the unit tests for the Spring/XWork integration classes here.
 * 
 * @author Simon Stewart
 */
public class SpringTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.opensymphony.xwork2.spring");
		
		//$JUnit-BEGIN$
		suite.addTestSuite(SpringObjectFactoryTest.class);
		suite.addTestSuite(ActionsFromSpringTest.class);
		suite.addTestSuite(InjectSpringObjectFactoryTest.class);
		suite.addTestSuite(ActionAutowiringInterceptorTest.class);
		suite.addTestSuite(XmlConfigurationProviderInterceptorsSpringTest.class);
		//$JUnit-END$
		
		
		return suite;
	}
}
