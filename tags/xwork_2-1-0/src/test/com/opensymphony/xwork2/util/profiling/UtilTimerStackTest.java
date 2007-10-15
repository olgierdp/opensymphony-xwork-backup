/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.profiling;

import junit.framework.TestCase;

/**
 * 
 * @author tmjee
 * @version $Date$ $Id$
 */
public class UtilTimerStackTest extends TestCase {

	protected String activateProp;
	protected String minTimeProp;
	
	
	public void testActivateInactivate() throws Exception {
		UtilTimerStack.setActive(true);
		assertTrue(UtilTimerStack.isActive());
		UtilTimerStack.setActive(false);
		assertFalse(UtilTimerStack.isActive());
	}
	
	
	public void testPushPop() throws Exception {
		UtilTimerStack.push("p1");
		Thread.sleep(1050);
		ProfilingTimerBean bean = UtilTimerStack.current.get();
		assertTrue(bean.startTime > 0);
		UtilTimerStack.pop("p1");
		assertTrue(bean.totalTime > 1000);
	}
	
	
	
	
	public void testProfileCallback() throws Exception {
		
			MockProfilingBlock<String> block = new MockProfilingBlock<String>() {
				public String performProfiling() throws Exception {
					Thread.sleep(1050);
					return "OK";
				}
			};
			String result = UtilTimerStack.profile("p1", block);
			assertEquals(result, "OK");
			assertNotNull(block.getProfilingTimerBean());
			assertTrue(block.getProfilingTimerBean().totalTime >= 1000);
		
	}
	
	
	public void testProfileCallbackThrowsException() throws Exception {
		try {
			UtilTimerStack.profile("p1", 
					new UtilTimerStack.ProfilingBlock<String>() {
						public String doProfiling() throws Exception {
							throw new RuntimeException("test");
						}
					});
			fail("exception should have been thrown");
		}
		catch(Exception e) {
			assertTrue(true);
		}
	}
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		activateProp = System.getProperty(UtilTimerStack.ACTIVATE_PROPERTY);
		minTimeProp = System.getProperty(UtilTimerStack.MIN_TIME);
		
		System.setProperty(UtilTimerStack.ACTIVATE_PROPERTY, "true");
		System.setProperty(UtilTimerStack.MIN_TIME, "0");
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		
		if (activateProp != null) {
			System.setProperty(UtilTimerStack.ACTIVATE_PROPERTY, activateProp);
		}
		else {
			System.clearProperty(UtilTimerStack.ACTIVATE_PROPERTY);
		}
		if (minTimeProp != null) {
			System.setProperty(UtilTimerStack.MIN_TIME, minTimeProp);
		}
		else {
			System.clearProperty(UtilTimerStack.ACTIVATE_PROPERTY);
		}
		
		
		activateProp = null;
		minTimeProp = null;
		
		super.tearDown();
	}
	
	
	
	public abstract class MockProfilingBlock<T> implements UtilTimerStack.ProfilingBlock<T> {
		
		private ProfilingTimerBean bean;
		
		public T doProfiling() throws Exception {
			bean = UtilTimerStack.current.get();
			return performProfiling();
		}
		
		public ProfilingTimerBean getProfilingTimerBean() {
			return bean;
		}
		
		public abstract T performProfiling() throws Exception;
	}
}


