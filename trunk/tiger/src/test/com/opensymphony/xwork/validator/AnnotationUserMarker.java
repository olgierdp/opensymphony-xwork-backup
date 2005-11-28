/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.validator.annotations.*;

/**
 * Marker interface to help test hierarchy traversal.
 *
 * @author Mark Woon
 */
/*
    <field name="email">
        <field-validator type="required" short-circuit="true">
            <message>You must enter a value for email.</message>
        </field-validator>
    </field>
    <field name="email2">
        <field-validator type="required" short-circuit="true">
            <message>You must enter a value for email2.</message>
        </field-validator>
    </field>
    <validator type="expression" short-circuit="true">
        <param name="expression">email.equals(email2)</param>
        <message>Email not the same as email2</message>
    </validator>
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
