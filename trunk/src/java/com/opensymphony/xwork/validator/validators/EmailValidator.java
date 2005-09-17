/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;



/**
 * EmailValidator checks that a given String field, if not empty, 
 * is a valid email address.
 * 
 * <p>
 * The regular expression used to validate that the string is an email address
 * is:
 * </p>
 * <pre>
 * "\\b(^(\\S+@).+((\\.com)|(\\.net)|(\\.org)|(\\.info)|(\\.edu)|(\\.mil)|(\\.gov)|(\\.biz)|(\\.ws)|(\\.us)|(\\.tv)|(\\.cc)|(\\..{2,2}))$)\\b"
 * </pre>
 * 
 * @author jhouse
 */
public class EmailValidator extends StringRegexValidator {

    public static final String emailAddressPattern = 
        "\\b(^(\\S+@).+((\\.com)|(\\.net)|(\\.org)|(\\.info)|(\\.edu)|(\\.mil)|(\\.gov)|(\\.biz)|(\\.ws)|(\\.us)|(\\.tv)|(\\.cc)|(\\..{2,2}))$)\\b";

    
    //~ Methods ////////////////////////////////////////////////////////////////

    public EmailValidator()
    {
        setRegex(emailAddressPattern);
        setCaseSensitive(false);
    }

}


