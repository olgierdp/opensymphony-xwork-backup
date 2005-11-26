/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import junit.framework.TestCase;

import java.io.InputStream;
import java.util.List;


/**
 * ValidatorFileParserTest
 * <p/>
 * Created : Jan 20, 2003 3:41:26 PM
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 */
public class ValidatorFileParserTest extends TestCase {

    private static final String testFileName = "com/opensymphony/xwork/validator/validator-parser-test.xml";
    private static final String testFileName2 = "com/opensymphony/xwork/validator/validator-parser-test2.xml";

    public void testParserActionLevelValidatorsShouldBeBeforeFieldLevelValidators() throws Exception {
    	InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName2, this.getClass());
    	
    	List configs = ValidatorFileParser.parseActionValidatorConfigs(is, testFileName2);
    	
    	ValidatorConfig valCfg0 = (ValidatorConfig) configs.get(0);
    	ValidatorConfig valCfg1 = (ValidatorConfig) configs.get(1);
    	
    	assertNotNull(configs);
    	assertEquals(configs.size(), 2);
    	
    	assertEquals("expression", valCfg0.getType());
    	assertFalse(valCfg0.isShortCircuit());
    	assertEquals(valCfg0.getDefaultMessage(), "an expression error message");
    	assertEquals(valCfg0.getParams().get("expression"), "false");
    	
    	assertEquals("required", valCfg1.getType());
    	assertFalse(valCfg1.isShortCircuit());
    	assertEquals(valCfg1.getDefaultMessage(), "a field error message");
    }
    
    
    public void testParser() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName, this.getClass());

        List configs = ValidatorFileParser.parseActionValidatorConfigs(is, testFileName);

        assertNotNull(configs);
        assertEquals(5, configs.size());

        
        ValidatorConfig cfg = (ValidatorConfig) configs.get(0);
        assertEquals("expression", cfg.getType());
        assertFalse(cfg.isShortCircuit());

        cfg = (ValidatorConfig) configs.get(1);
        assertEquals("expression", cfg.getType());
        assertTrue(cfg.isShortCircuit());
        
        cfg = (ValidatorConfig) configs.get(2);
        assertEquals("required", cfg.getType());
        assertEquals("foo", cfg.getParams().get("fieldName"));
        assertEquals("You must enter a value for foo.", cfg.getDefaultMessage());
        
        cfg = (ValidatorConfig) configs.get(3);
        assertEquals("required", cfg.getType());
        assertTrue(cfg.isShortCircuit());

        cfg = (ValidatorConfig) configs.get(4);
        assertEquals("int", cfg.getType());
        assertFalse(cfg.isShortCircuit());
    }

    protected void setUp() throws Exception {
        super.setUp();
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
