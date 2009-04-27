/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;

/**
 * <code>DefaultValidatorFactoryTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: $
 */
public class DefaultValidatorFactoryTest extends TestCase {

    public void testParseValidators() {
        Mock mockValidatorFileParser = new Mock(ValidatorFileParser.class);
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/validator/validators/default.xml")));
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("validators.xml")));
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("myOther-validators.xml")));
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("my-validators.xml")));
        DefaultValidatorFactory factory = new DefaultValidatorFactory(null, (ValidatorFileParser) mockValidatorFileParser.proxy());
    }
}
