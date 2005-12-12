/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.*;

/**
 * <!-- START SNIPPET: description -->
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 *
 * <!-- END SNIPPET: usage -->
 *
 * <p/> <u>Annotation parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <table>
 * <thead>
 * <tr>
 * <th>Parameter</th>
 * <th>Required</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>param</td>
 * <td>required</td>
 * <td>default</td>
 * <td>description</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <u>An Annotated Interface</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * @Validation()
 * public interface AnnotationDataAware {
 *
 *     void setBarObj(Bar b);
 *
 *     Bar getBarObj();
 *
 *     @RequiredFieldValidator(message = "You must enter a value for data.")
 *     @RequiredStringValidator(message = "You must enter a value for data.")
 *     void setData(String data);
 *
 *     String getData();
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p/> <u>Example code:</u>
 *
 * <u>An Annotated Class</u>
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * @Validation()
 * public class SimpleAnnotationAction extends ActionSupport {
 *
 *     @RequiredFieldValidator(type = ValidatorType.FIELD, message = "You must enter a value for bar.")
 *     @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")
 *     public void setBar(int bar) {
 *         this.bar = bar;
 *     }
 *
 *     public int getBar() {
 *         return bar;
 *     }
 *
 *     @Validations(
 *             requiredFields =
 *                     {@RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "customfield", message = "You must enter a value for field.")},
 *             requiredStrings =
 *                     {@RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "stringisrequired", message = "You must enter a value for string.")},
 *             emails =
 *                     { @EmailValidator(type = ValidatorType.SIMPLE, fieldName = "emailaddress", message = "You must enter a value for email.")},
 *             urls =
 *                     { @UrlValidator(type = ValidatorType.SIMPLE, fieldName = "hreflocation", message = "You must enter a value for email.")},
 *             stringLengthFields =
 *                     {@StringLengthFieldValidator(type = ValidatorType.SIMPLE, trim = true, minLength="10" , maxLength = "12", fieldName = "needstringlength", message = "You must enter a stringlength.")},
 *             intRangeFields =
 *                     { @IntRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "intfield", min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *             dateRangeFields =
 *                     {@DateRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "datefield", min = "-1", max = "99", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *             expressions = {
 *                 @ExpressionValidator(expression = "foo &gt; 1", message = "Foo must be greater than Bar 1. Foo = ${foo}, Bar = ${bar}."),
 *                 @ExpressionValidator(expression = "foo &gt; 2", message = "Foo must be greater than Bar 2. Foo = ${foo}, Bar = ${bar}."),
 *                 @ExpressionValidator(expression = "foo &gt; 3", message = "Foo must be greater than Bar 3. Foo = ${foo}, Bar = ${bar}."),
 *                 @ExpressionValidator(expression = "foo &gt; 4", message = "Foo must be greater than Bar 4. Foo = ${foo}, Bar = ${bar}."),
 *                 @ExpressionValidator(expression = "foo &gt; 5", message = "Foo must be greater than Bar 5. Foo = ${foo}, Bar = ${bar}.")
 *     }
 *     )
 *     public String execute() throws Exception {
 *         return SUCCESS;
 *     }
 * }
 *
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {

    /**
     * Used for class or interface validation rules.
     */
    Validations[] validations() default {};
}
