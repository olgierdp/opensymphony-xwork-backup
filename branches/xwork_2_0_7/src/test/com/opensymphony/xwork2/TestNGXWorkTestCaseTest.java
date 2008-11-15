/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import com.opensymphony.xwork2.TestNGXWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationManager;

import junit.framework.TestCase;

public class TestNGXWorkTestCaseTest extends TestCase {

    public void testSimpleTest() throws Exception {
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { RunTest.class });
        testng.addListener(tla);
        try {
            testng.run();
            assertEquals(1, tla.getPassedTests().size());
            assertEquals(0, tla.getFailedTests().size());
            assertTrue(RunTest.ran);
            assertNotNull(RunTest.mgr);
        } finally {
            RunTest.mgr = null;
        }
    }
    
    @Test
    public static class RunTest extends TestNGXWorkTestCase {
        public static boolean ran = false;
        public static ConfigurationManager mgr;
        
        public void testRun() {
            ran = true;
            mgr = this.configurationManager;
        }
    }
}
