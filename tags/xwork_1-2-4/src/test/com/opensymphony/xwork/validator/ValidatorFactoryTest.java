/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.XWorkTestCase;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ValidatorFactoryTest extends XWorkTestCase {

    public void test() throws Exception {
        ValidatorFactory.reset();
        ValidatorFactory.parseValidators();

        assertEquals(
                "com.opensymphony.xwork.validator.validators.RequiredStringValidator",
                ValidatorFactory.lookupRegisteredValidatorType("requiredstring"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.RequiredFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("required"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.IntRangeFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("int"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.DoubleRangeFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("double"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.DateRangeFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("date"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.ExpressionValidator",
                ValidatorFactory.lookupRegisteredValidatorType("expression"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.FieldExpressionValidator",
                ValidatorFactory.lookupRegisteredValidatorType("fieldexpression"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.EmailValidator",
                ValidatorFactory.lookupRegisteredValidatorType("email"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.URLValidator",
                ValidatorFactory.lookupRegisteredValidatorType("url"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.VisitorFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("visitor"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.ConversionErrorFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("conversion"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.StringLengthFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("stringlength"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.RegexFieldValidator",
                ValidatorFactory.lookupRegisteredValidatorType("regex"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.FooValidator",
                ValidatorFactory.lookupRegisteredValidatorType("foo"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.BarValidator",
                ValidatorFactory.lookupRegisteredValidatorType("bar"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.BazValidator",
                ValidatorFactory.lookupRegisteredValidatorType("baz"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.BooValidator",
                ValidatorFactory.lookupRegisteredValidatorType("boo"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.BingValidator",
                ValidatorFactory.lookupRegisteredValidatorType("bing"));
        assertEquals(
                "com.opensymphony.xwork.validator.validators.BangValidator",
                ValidatorFactory.lookupRegisteredValidatorType("bang"));
    }
}
