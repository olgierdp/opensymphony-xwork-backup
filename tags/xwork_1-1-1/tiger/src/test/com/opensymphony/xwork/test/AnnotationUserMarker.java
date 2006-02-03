/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.validator.annotations.*;

/**
 * Marker interface to help test hierarchy traversal.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
@Validation(
        validations = @Validations(
                requiredFields = {
                    @RequiredFieldValidator(fieldName = "email", shortCircuit = true, message = "You must enter a value for email."),
                    @RequiredFieldValidator(fieldName = "email2", shortCircuit = true, message = "You must enter a value for email2.")
                },
                expressions = {
                        @ExpressionValidator(shortCircuit = true, expression = "email.equals(email2)", message = "Email not the same as email2" )
                }
        )
)
public interface AnnotationUserMarker {
}
