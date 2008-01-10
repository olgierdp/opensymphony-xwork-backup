/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import java.util.LinkedHashMap;
import java.util.List;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class InterceptorBuilderTest extends XWorkTestCase {

	/**
	 * Try to test this
	 * <interceptor-ref name="interceptorStack1">
	 * 	<param name="interceptor1.param1">interceptor1_value1</param>
	 *     <param name="interceptor1.param2">interceptor1_value2</param>
	 *     <param name="interceptor2.param1">interceptor2_value1</param>
	 *     <param name="interceptor2.param2">interceptor2_value2</param>
	 * </interceptor-ref>
	 *
	 * @throws Exception
	 */
	public void testBuildInterceptor_1() throws Exception {
		InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig();
		interceptorStackConfig1.setName("interceptorStack1");
		
		InterceptorConfig interceptorConfig1 = new InterceptorConfig();
		interceptorConfig1.setClassName("com.opensymphony.xwork.config.providers.InterceptorBuilderTest$MockInterceptor1");
		interceptorConfig1.setName("interceptor1");
		
		InterceptorConfig interceptorConfig2 = new InterceptorConfig();
		interceptorConfig2.setClassName("com.opensymphony.xwork.config.providers.InterceptorBuilderTest$MockInterceptor2");
		interceptorConfig2.setName("interceptor2");
		
		
		PackageConfig packageConfig = new PackageConfig();
		packageConfig.setName("package1");
		packageConfig.setNamespace("/namespace");
		packageConfig.addInterceptorConfig(interceptorConfig1);
		packageConfig.addInterceptorConfig(interceptorConfig2);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig1);

		
		
		List interceptorMappings = 
			InterceptorBuilder.constructInterceptorReference(packageConfig, "interceptorStack1", 
				new LinkedHashMap() {
					private static final long serialVersionUID = -1358620486812957895L;
					{
						put("interceptor1.param1", "interceptor1_value1");
						put("interceptor1.param2", "interceptor1_value2");
						put("interceptor2.param1", "interceptor2_value1");
						put("interceptor2.param2", "interceptor2_value2");
					}
				});
		
		assertEquals(interceptorMappings.size(), 2);
		
		assertEquals(((InterceptorMapping)interceptorMappings.get(0)).getName(), "interceptor1");
		assertNotNull(((InterceptorMapping)interceptorMappings.get(0)).getInterceptor());
		assertEquals(((InterceptorMapping)interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
		assertEquals(((MockInterceptor1)((InterceptorMapping)interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
		assertEquals(((MockInterceptor1)((InterceptorMapping)interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");
		
		assertEquals(((InterceptorMapping)interceptorMappings.get(1)).getName(), "interceptor2");
		assertNotNull(((InterceptorMapping)interceptorMappings.get(1)).getInterceptor());
		assertEquals(((InterceptorMapping)interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);
		assertEquals(((MockInterceptor2)((InterceptorMapping)interceptorMappings.get(1)).getInterceptor()).getParam1(), "interceptor2_value1");
		assertEquals(((MockInterceptor2)((InterceptorMapping)interceptorMappings.get(1)).getInterceptor()).getParam2(), "interceptor2_value2");
	}
	
	/**
	 * Try to test this
	 * <interceptor-ref name="interceptorStack1">
	 * 	<param name="interceptorStack2.interceptor1.param1">interceptor1_value1</param>
	 *     <param name="interceptorStack2.interceptor1.param2">interceptor1_value2</param>
	 *     <param name="interceptorStack3.interceptor2.param1">interceptor2_value1</param>
	 *     <param name="interceptorStack3.interceptor2.param2">interceptor2_value2</param>
	 * </interceptor-ref>
	 * 
	 * @throws Exception
	 */
	public void testBuildInterceptor_2() throws Exception {
		InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig();
		interceptorStackConfig1.setName("interceptorStack1");
		
		InterceptorStackConfig interceptorStackConfig2 = new InterceptorStackConfig();
		interceptorStackConfig2.setName("interceptorStack2");
		
		InterceptorStackConfig interceptorStackConfig3 = new InterceptorStackConfig();
		interceptorStackConfig3.setName("interceptorStack3");
		
		InterceptorConfig interceptorConfig1 = new InterceptorConfig();
		interceptorConfig1.setClassName("com.opensymphony.xwork.config.providers.InterceptorBuilderTest$MockInterceptor1");
		interceptorConfig1.setName("interceptor1");
		
		InterceptorConfig interceptorConfig2 = new InterceptorConfig();
		interceptorConfig2.setClassName("com.opensymphony.xwork.config.providers.InterceptorBuilderTest$MockInterceptor2");
		interceptorConfig2.setName("interceptor2");
		
		PackageConfig packageConfig = new PackageConfig();
		packageConfig.setName("package1");
		packageConfig.setNamespace("/namspace");
		packageConfig.addInterceptorConfig(interceptorConfig1);
		packageConfig.addInterceptorConfig(interceptorConfig2);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig1);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig2);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig3);
		
		List interceptorMappings = InterceptorBuilder.constructInterceptorReference(packageConfig, "interceptorStack1", 
				new LinkedHashMap() {
					private static final long serialVersionUID = -5819935102242042570L;
					{
						put("interceptorStack2.interceptor1.param1", "interceptor1_value1");
						put("interceptorStack2.interceptor1.param2", "interceptor1_value2");
						put("interceptorStack3.interceptor2.param1", "interceptor2_value1");
						put("interceptorStack3.interceptor2.param2", "interceptor2_value2");
					}
				});
		
		assertEquals(interceptorMappings.size(), 2);
		
		assertEquals(((InterceptorMapping)interceptorMappings.get(0)).getName(), "interceptor1");
		assertNotNull(((InterceptorMapping)interceptorMappings.get(0)).getInterceptor());
		assertEquals(((InterceptorMapping)interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
		assertEquals(((MockInterceptor1)((InterceptorMapping)interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
		assertEquals(((MockInterceptor1)((InterceptorMapping)interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");
		
		assertEquals(((InterceptorMapping)interceptorMappings.get(1)).getName(), "interceptor2");
		assertNotNull(((InterceptorMapping)interceptorMappings.get(1)).getInterceptor());
		assertEquals(((InterceptorMapping)interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);
		assertEquals(((MockInterceptor2)((InterceptorMapping)interceptorMappings.get(1)).getInterceptor()).getParam1(), "interceptor2_value1");
		assertEquals(((MockInterceptor2)((InterceptorMapping)interceptorMappings.get(1)).getInterceptor()).getParam2(), "interceptor2_value2");
	}
	
	/**
	 * Try to test this
	 * <interceptor-ref name="interceptorStack1">
	 * 	<param name="interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param1">interceptor1_value1</param>
	 *     <param name="interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param2">interceptor1_value2</param>
	 *     <param name="interceptorStack5.interceptor2.param1">interceptor2_value1</param>
	 *     <param name="interceptorStack5.interceptor2.param2">interceptor2_value2</param>
	 * </interceptor-ref>
	 * 
	 * @throws Exception
	 */
	public void testBuildInterceptor_3() throws Exception {
		InterceptorConfig interceptorConfig1 = new InterceptorConfig();
		interceptorConfig1.setClassName("com.opensymphony.xwork.config.providers.InterceptorBuilderTest$MockInterceptor1");
		interceptorConfig1.setName("interceptor1");
		
		InterceptorConfig interceptorConfig2 = new InterceptorConfig();
		interceptorConfig2.setClassName("com.opensymphony.xwork.config.providers.InterceptorBuilderTest$MockInterceptor2");
		interceptorConfig2.setName("interceptor2");
		
		InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig();
		interceptorStackConfig1.setName("interceptorStack1");
		
		InterceptorStackConfig interceptorStackConfig2 = new InterceptorStackConfig();
		interceptorStackConfig2.setName("interceptorStack2");
		
		InterceptorStackConfig interceptorStackConfig3 = new InterceptorStackConfig();
		interceptorStackConfig3.setName("interceptorStack3");
		
		InterceptorStackConfig interceptorStackConfig4  = new InterceptorStackConfig();
		interceptorStackConfig4.setName("interceptorStack4");
		
		InterceptorStackConfig interceptorStackConfig5 = new InterceptorStackConfig();
		interceptorStackConfig5.setName("interceptorStack5");
		
		
		PackageConfig packageConfig = new PackageConfig();
		packageConfig.setName("package1");
		packageConfig.addInterceptorConfig(interceptorConfig1);
		packageConfig.addInterceptorConfig(interceptorConfig2);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig1);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig2);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig3);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig4);
		packageConfig.addInterceptorStackConfig(interceptorStackConfig5);
		
		List interceptorMappings = InterceptorBuilder.constructInterceptorReference(
				packageConfig, "interceptorStack1", 
				new LinkedHashMap() {
					private static final long serialVersionUID = 4675809753780875525L;
					{
						put("interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param1", "interceptor1_value1");
						put("interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param2", "interceptor1_value2");
						put("interceptorStack5.interceptor2.param1", "interceptor2_value1");
						put("interceptorStack5.interceptor2.param2", "interceptor2_value2");
					}
				});
		
		assertEquals(interceptorMappings.size(), 2);
		
		assertEquals(((InterceptorMapping)interceptorMappings.get(0)).getName(), "interceptor1");
		assertNotNull(((InterceptorMapping)interceptorMappings.get(0)).getInterceptor());
		assertEquals(((InterceptorMapping)interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
		assertEquals(((MockInterceptor1)((InterceptorMapping)interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
		assertEquals(((MockInterceptor1)((InterceptorMapping)interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");
		
		assertEquals(((InterceptorMapping)interceptorMappings.get(1)).getName(), "interceptor2");
		assertNotNull(((InterceptorMapping)interceptorMappings.get(1)).getInterceptor());
		assertEquals(((InterceptorMapping)interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);
		assertEquals(((MockInterceptor2)((InterceptorMapping)interceptorMappings.get(1)).getInterceptor()).getParam1(), "interceptor2_value1");
		assertEquals(((MockInterceptor2)((InterceptorMapping)interceptorMappings.get(1)).getInterceptor()).getParam2(), "interceptor2_value2");
	}
	
	
	public static class MockInterceptor1 implements Interceptor {
		private static final long serialVersionUID = 2939902550126175874L;
		private String param1;
		private String param2;
		public void setParam1(String param1) { this.param1 = param1; }
		public String getParam1() { return this.param1; }
		public void setParam2(String param2) { this.param2 = param2; }
		public String getParam2() { return this.param2; }
		public void destroy() {
		}
		public void init() {
		}
		public String intercept(ActionInvocation invocation) throws Exception {
			return invocation.invoke();
		}
	}
	
	public static class MockInterceptor2 implements Interceptor {
		private static final long serialVersionUID = 267427973306989618L;
		private String param1;
		private String param2;
		public void setParam1(String param1) { this.param1 = param1; }
		public String getParam1() { return this.param1; }
		public void setParam2(String param2) { this.param2 = param2; }
		public String getParam2() { return this.param2; }
		public void destroy() {}
		public void init() {}
		public String intercept(ActionInvocation invocation) throws Exception {
			return invocation.invoke();
		}
	}
}
