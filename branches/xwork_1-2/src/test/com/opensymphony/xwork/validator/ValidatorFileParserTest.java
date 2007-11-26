/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.XworkException;
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
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ValidatorFileParserTest extends TestCase {

    private static final String testFileName = "com/opensymphony/xwork/validator/validator-parser-test.xml";
    private static final String testFileName2 = "com/opensymphony/xwork/validator/validator-parser-test2.xml";
    private static final String testFileName3 = "com/opensymphony/xwork/validator/validator-parser-test3.xml";
    private static final String testFileName4 = "com/opensymphony/xwork/validator/validator-parser-test4.xml";
    private static final String testFileName5 = "com/opensymphony/xwork/validator/validator-parser-test5.xml";
    private static final String testFileName6 = "com/opensymphony/xwork/validator/validator-parser-test6.xml";

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
        assertEquals(6, cfg.getLocation().getLineNumber());
        
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
            assertTrue("Wrong line number", 5==ex.getLocation().getLineNumber());
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
            assertTrue("Wrong line number: "+ex.getLocation(), 15==ex.getLocation().getLineNumber());
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
            assertTrue("Wrong line number", 6==ex.getLocation().getLineNumber());
            pass = true;
        } 
        assertTrue("Validation file should have thrown exception", pass);
    }

    public void testParseValidatorDefinition() throws Exception {
        InputStream is = null;
        try {
            is = ClassLoaderUtil.getResourceAsStream("validators.xml", getClass());

            ValidatorFileParser.parseValidatorDefinitions(is, "-//OpenSymphony Group//XWork Validator Definition 1.0//EN");

            /*
             *   <validator name="required" class="com.opensymphony.xwork.validator.validators.RequiredFieldValidator"/>
             *   <validator name="requiredstring" class="com.opensymphony.xwork.validator.validators.RequiredStringValidator"/>
             *   <validator name="int" class="com.opensymphony.xwork.validator.validators.IntRangeFieldValidator"/>
             *   <validator name="double" class="com.opensymphony.xwork.validator.validators.DoubleRangeFieldValidator"/>
             *   <validator name="date" class="com.opensymphony.xwork.validator.validators.DateRangeFieldValidator"/>
             *   <validator name="expression" class="com.opensymphony.xwork.validator.validators.ExpressionValidator"/>
             *   <validator name="fieldexpression" class="com.opensymphony.xwork.validator.validators.FieldExpressionValidator"/>
             *   <validator name="email" class="com.opensymphony.xwork.validator.validators.EmailValidator"/>
             *   <validator name="url" class="com.opensymphony.xwork.validator.validators.URLValidator"/>
             *   <validator name="visitor" class="com.opensymphony.xwork.validator.validators.VisitorFieldValidator"/>
             *   <validator name="conversion" class="com.opensymphony.xwork.validator.validators.ConversionErrorFieldValidator"/>
             *   <validator name="stringlength" class="com.opensymphony.xwork.validator.validators.StringLengthFieldValidator"/>
             *   <validator name="regex" class="com.opensymphony.xwork.validator.validators.RegexFieldValidator"/>
             */
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("required"),
                    "com.opensymphony.xwork.validator.validators.RequiredFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("requiredstring"),
                    "com.opensymphony.xwork.validator.validators.RequiredStringValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("int"),
                    "com.opensymphony.xwork.validator.validators.IntRangeFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("double"),
                    "com.opensymphony.xwork.validator.validators.DoubleRangeFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("date"),
                    "com.opensymphony.xwork.validator.validators.DateRangeFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("expression"),
                    "com.opensymphony.xwork.validator.validators.ExpressionValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("fieldexpression"),
                    "com.opensymphony.xwork.validator.validators.FieldExpressionValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("email"),
                    "com.opensymphony.xwork.validator.validators.EmailValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("url"),
                    "com.opensymphony.xwork.validator.validators.URLValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("visitor"),
                    "com.opensymphony.xwork.validator.validators.VisitorFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("conversion"),
                    "com.opensymphony.xwork.validator.validators.ConversionErrorFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("stringlength"),
                    "com.opensymphony.xwork.validator.validators.StringLengthFieldValidator");
            assertEquals(ValidatorFactory.lookupRegisteredValidatorType("regex"),
                    "com.opensymphony.xwork.validator.validators.RegexFieldValidator");
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public void testValidatorWithI18nMessage() throws Exception {
        InputStream is = null;
        try {
            is = ClassLoaderUtil.getResourceAsStream(testFileName6, this.getClass());
            List validatorConfigs = ValidatorFileParser.parseActionValidatorConfigs(is, "-//OpenSymphony Group//XWork Validator 1.0.3//EN");

            assertEquals(validatorConfigs.size(), 2);

            assertEquals(((ValidatorConfig)validatorConfigs.get(0)).getParams().get("fieldName"), "name");
            assertEquals(((ValidatorConfig)validatorConfigs.get(0)).getMessageParams().length, 0);
            assertEquals(((ValidatorConfig)validatorConfigs.get(0)).getMessageKey(), "error.name");
            assertEquals(((ValidatorConfig)validatorConfigs.get(0)).getDefaultMessage(), "default message 1");
            assertEquals(((ValidatorConfig)validatorConfigs.get(0)).getParams().size(), 1);
            assertEquals(((ValidatorConfig)validatorConfigs.get(0)).getType(), "requiredstring");

            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getParams().get("fieldName"), "address");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageParams().length, 5);
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageParams()[0], "'tmjee'");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageParams()[1], "'phil'");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageParams()[2], "'rainer'");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageParams()[3], "'hopkins'");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageParams()[4], "'jimmy'");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getMessageKey(), "error.address");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getDefaultMessage(), "The Default Message");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getParams().size(), 3);
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getParams().get("trim"), "true");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getParams().get("anotherParam"), "anotherValue");
            assertEquals(((ValidatorConfig)validatorConfigs.get(1)).getType(), "requiredstring");
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }



    protected void setUp() throws Exception {
        super.setUp();
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
