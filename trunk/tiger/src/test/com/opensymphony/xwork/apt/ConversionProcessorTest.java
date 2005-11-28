/* com.opensymphony.xwork.apt.ComponentProcessorTest, created Aug 13, 2005 */
package com.opensymphony.xwork.apt;

import java.io.File;
import java.util.Properties;

public class ConversionProcessorTest extends AbstractAptTestCase {
	
	public ConversionProcessorTest(String name) {
		super(name);
	}

	public void test_generate() throws Exception {
		AptRunnerResult result = runApt(new File(getDefaultSourceDir(),
		"com/opensymphony/xwork/conversion"));
		assertTrue(result.succeeded());
//		assertFalse(result.containsErrors());
		
		Properties generatedProperties = new Properties();
		generatedProperties.load(readFromDestFolder("xwork-conversion.properties"));
		Properties expectedProperties = new Properties();
		expectedProperties.load(readFromSourceFolder
			(getPackageDirectoryName() + "/expected/" + "xwork-conversion.properties"));
		assertEquals(expectedProperties, generatedProperties);
		
		Properties generatedActionProperties = new Properties();
		generatedActionProperties.load(
				readFromDestFolder("com/opensymphony/xwork/conversion/ConversionTestAction-conversion.properties"));
		
		Properties expectedActionProperties = new Properties();
		expectedActionProperties.load(readFromSourceFolder(
				getPackageDirectoryName() + File.separator + "expected" + File.separator  + "ConversionTestAction-conversion.properties"));
		assertEquals(expectedActionProperties, generatedActionProperties);
		
	}
	
}
