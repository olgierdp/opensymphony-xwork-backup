/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;

import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.validator.validators.ExpressionValidator;
import com.opensymphony.xwork.validator.validators.IntRangeFieldValidator;
import com.opensymphony.xwork.validator.validators.RequiredFieldValidator;

import junit.framework.TestCase;

import java.io.InputStream;

import java.util.List;


/**
 * ValidatorFileParserTest
 *
 * Created : Jan 20, 2003 3:41:26 PM
 *
 * @author Jason Carreira
 */
public class ValidatorFileParserTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testParser() {
        InputStream is = ClassLoaderUtil.getResourceAsStream("com/opensymphony/xwork/validator/validator-parser-test.xml", this.getClass());

        List configs = ValidatorFileParser.parseActionValidators(is);
        assertNotNull(configs);
        assertEquals(5, configs.size());

        Validator validator = (Validator) configs.get(0);
        assertTrue(validator instanceof RequiredFieldValidator);

        FieldValidator fieldValidator = (FieldValidator) validator;
        assertEquals("foo", fieldValidator.getFieldName());
        assertEquals("You must enter a value for foo.", fieldValidator.getDefaultMessage());

        validator = (Validator) configs.get(1);
        assertTrue(validator instanceof ShortCircuitableValidator);
        assertTrue(((ShortCircuitableValidator) validator).isShortCircuit());

        validator = (Validator) configs.get(2);
        assertTrue(validator instanceof IntRangeFieldValidator);
        assertFalse(((ShortCircuitableValidator) validator).isShortCircuit());

        validator = (Validator) configs.get(3);
        assertTrue(validator instanceof ExpressionValidator);
        assertFalse(((ShortCircuitableValidator) validator).isShortCircuit());

        validator = (Validator) configs.get(4);
        assertTrue(validator instanceof ExpressionValidator);
        assertTrue(((ShortCircuitableValidator) validator).isShortCircuit());
    }

    protected void setUp() throws Exception {
        super.setUp();
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
