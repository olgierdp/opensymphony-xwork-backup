/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.XWorkStatic;
import com.opensymphony.xwork.XworkException;
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
    private static final String testFileName3 = "com/opensymphony/xwork/validator/validator-parser-test3.xml";
    private static final String testFileName4 = "com/opensymphony/xwork/validator/validator-parser-test4.xml";
    private static final String testFileName5 = "com/opensymphony/xwork/validator/validator-parser-test5.xml";

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
        assertEquals(6, configs.size());


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
        assertEquals(4, cfg.getLocation().getLineNumber());

        cfg = (ValidatorConfig) configs.get(3);
        assertEquals("required", cfg.getType());
        assertTrue(cfg.isShortCircuit());

        cfg = (ValidatorConfig) configs.get(4);
        assertEquals("int", cfg.getType());
        assertFalse(cfg.isShortCircuit());

        cfg = (ValidatorConfig) configs.get(5);
        assertEquals("regex", cfg.getType());
        assertFalse(cfg.isShortCircuit());
        assertEquals("([aAbBcCdD][123][eEfFgG][456])", cfg.getParams().get("expression"));
    }

    public void testParserWithBadValidation() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName3, this.getClass());

        boolean pass = false;
        try {
            ValidatorFileParser.parseActionValidatorConfigs(is, testFileName3);
        } catch (XworkException ex) {
            assertTrue("Wrong line number", 3 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }

    public void testParserWithBadXML() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName4, this.getClass());

        boolean pass = false;
        try {
            ValidatorFileParser.parseActionValidatorConfigs(is, testFileName4);
        } catch (XworkException ex) {
            assertTrue("Wrong line number: " + ex.getLocation(), 13 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }

    public void testValidatorDefinitionsWithBadClassName() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName5, this.getClass());

        boolean pass = false;
        try {
            ValidatorFileParser.parseValidatorDefinitions(is, testFileName5);
        } catch (XworkException ex) {
            assertTrue("Wrong line number", 3 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }


    protected void setUp() throws Exception {
        super.setUp();
        XWorkStatic.getConfigurationManager().clearConfigurationProviders();
        XWorkStatic.getConfigurationManager().addConfigurationProvider(new MockConfigurationProvider());
        XWorkStatic.getConfigurationManager().getConfiguration().reload();
    }
}
