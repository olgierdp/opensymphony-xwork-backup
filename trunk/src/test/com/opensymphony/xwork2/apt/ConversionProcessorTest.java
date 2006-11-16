/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.apt;

import java.io.File;
import java.util.Properties;

/**
 * @author Rainer Hermanns
 */
public class ConversionProcessorTest extends AbstractAptTestCase {
	
	public ConversionProcessorTest(String name) {
		super(name);
	}

	public void test_generate() throws Exception {
		AptRunnerResult result = runApt(new File(getDefaultSourceDir(), "com/opensymphony/xwork2/conversion"));
		assertTrue("ERRORS: " + result, result.succeeded());
		assertFalse("ERRORS: " + result, result.containsErrors());
		
		Properties generatedProperties = new Properties();
		generatedProperties.load(readFromDestFolder("xwork-conversion.properties"));
		Properties expectedProperties = new Properties();
		expectedProperties.load(readFromSourceFolder("com/opensymphony/xwork2/apt/expected/" + "xwork-conversion.properties"));
		assertEquals(expectedProperties, generatedProperties);
		
		Properties generatedActionProperties = new Properties();
		generatedActionProperties.load(
				readFromDestFolder("com/opensymphony/xwork2/conversion/ConversionTestAction-conversion.properties"));
		
		Properties expectedActionProperties = new Properties();
		expectedActionProperties.load(readFromSourceFolder(
				getPackageDirectoryName() + File.separator + "expected" + File.separator  + "ConversionTestAction-conversion.properties"));
		assertEquals(expectedActionProperties, generatedActionProperties);
		
	}
	
}
