/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;
import com.opensymphony.xwork2.validator.validators.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.validators.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;


/**
 * DefaultValidatorFileParserTest
 * <p/>
 * Created : Jan 20, 2003 3:41:26 PM
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 */
public class DefaultValidatorFileParserTest extends TestCase {

    private static final String testFileName = "com/opensymphony/xwork2/validator/validator-parser-test.xml";
    private static final String testFileName2 = "com/opensymphony/xwork2/validator/validator-parser-test2.xml";
    private static final String testFileName3 = "com/opensymphony/xwork2/validator/validator-parser-test3.xml";
    private static final String testFileName4 = "com/opensymphony/xwork2/validator/validator-parser-test4.xml";
    private static final String testFileName5 = "com/opensymphony/xwork2/validator/validator-parser-test5.xml";
    private static final String testFileName6 = "com/opensymphony/xwork2/validator/validators-fail.xml";
    private Mock mockValidatorFactory;
    private ValidatorFileParser parser;

    public void testParserActionLevelValidatorsShouldBeBeforeFieldLevelValidators() throws Exception {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName2, this.getClass());

        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("expression")), ExpressionValidator.class.getName());
        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("required")), RequiredFieldValidator.class.getName());
        List configs = parser.parseActionValidatorConfigs((ValidatorFactory) mockValidatorFactory.proxy(), is, testFileName2);
        mockValidatorFactory.verify();

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

        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("expression")), ExpressionValidator.class.getName());
        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("expression")), ExpressionValidator.class.getName());
        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("required")), RequiredFieldValidator.class.getName());
        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("required")), RequiredFieldValidator.class.getName());
        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("int")), IntRangeFieldValidator.class.getName());
        mockValidatorFactory.expectAndReturn("lookupRegisteredValidatorType", C.args(C.eq("regex")), RegexFieldValidator.class.getName());
        List configs = parser.parseActionValidatorConfigs((ValidatorFactory) mockValidatorFactory.proxy(), is, testFileName);
        mockValidatorFactory.verify();


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
            parser.parseActionValidatorConfigs((ValidatorFactory) mockValidatorFactory.proxy(), is, testFileName3);
        } catch (XWorkException ex) {
            assertTrue("Wrong line number", 3 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }

    public void testParserWithBadXML() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName4, this.getClass());

        boolean pass = false;
        try {
            parser.parseActionValidatorConfigs((ValidatorFactory) mockValidatorFactory.proxy(), is, testFileName4);
        } catch (XWorkException ex) {
            assertTrue("Wrong line number: " + ex.getLocation(), 13 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }

    public void testParserWithBadXML2() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName6, this.getClass());

        boolean pass = false;
        try {
            parser.parseActionValidatorConfigs((ValidatorFactory) mockValidatorFactory.proxy(), is, testFileName6);
        } catch (XWorkException ex) {
            assertTrue("Wrong line number: " + ex.getLocation(), 8 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }

    public void testValidatorDefinitionsWithBadClassName() {
        InputStream is = ClassLoaderUtil.getResourceAsStream(testFileName5, this.getClass());

        boolean pass = false;
        try {
            parser.parseActionValidatorConfigs((ValidatorFactory) mockValidatorFactory.proxy(), is, testFileName5);
        } catch (XWorkException ex) {
            assertTrue("Wrong line number", 3 == ex.getLocation().getLineNumber());
            pass = true;
        }
        assertTrue("Validation file should have thrown exception", pass);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockValidatorFactory = new Mock(ValidatorFactory.class);
        parser = new DefaultValidatorFileParser();
    }

    @Override
    protected void tearDown() throws Exception {
        mockValidatorFactory = null;
        parser = null;
    }
}
