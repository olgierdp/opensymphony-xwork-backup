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
                ValidatorFactory.lookupRegisteredValidatorType("requiredstring"),
                "com.opensymphony.xwork.validator.validators.RequiredStringValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("required"),
                "com.opensymphony.xwork.validator.validators.RequiredFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("int"), 
                "com.opensymphony.xwork.validator.validators.IntRangeFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("double"),
                "com.opensymphony.xwork.validator.validators.DoubleRangeFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("date"),
                "com.opensymphony.xwork.validator.validators.DateRangeFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("expression"),
                "com.opensymphony.xwork.validator.validators.ExpressionValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("fieldexpression"),
                "com.opensymphony.xwork.validator.validators.FieldExpressionValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("email"),
                "com.opensymphony.xwork.validator.validators.EmailValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("url"),
                "com.opensymphony.xwork.validator.validators.URLValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("visitor"),
                "com.opensymphony.xwork.validator.validators.VisitorFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("conversion"),
                "com.opensymphony.xwork.validator.validators.ConversionErrorFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("stringlength"),
                "com.opensymphony.xwork.validator.validators.StringLengthFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("regex"),
                "com.opensymphony.xwork.validator.validators.RegexFieldValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("foo"),
                "com.opensymphony.xwork.validator.validators.FooValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("bar"),
                "com.opensymphony.xwork.validator.validators.BarValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("baz"),
                "com.opensymphony.xwork.validator.validators.BazValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("boo"),
                "com.opensymphony.xwork.validator.validators.BooValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("bing"),
                "com.opensymphony.xwork.validator.validators.BingValidator");
        assertEquals(
                ValidatorFactory.lookupRegisteredValidatorType("bang"),
                "com.opensymphony.xwork.validator.validators.BangValidator");
    }
}
