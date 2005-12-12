/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.*;

/**
 * <!-- START SNIPPET: description -->
 * The validator allows you to forward validator to object properties of your action
 * using the objects own validator files. This allows you to use the ModelDriven development
 * pattern and manage your validations for your models in one place, where they belong, next to
 * your model classes.
 *
 * The VisitorFieldValidator can handle either simple Object properties, Collections of Objects, or Arrays.
 * The error message for the VisitorFieldValidator will be appended in front of validator messages added
 * by the validations for the Object message.
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
 * <table class='confluenceTable'>
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Default </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> context </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> action alias </td>
 * <td class='confluenceTd'> Determines the context to use for validating the Object property. If not defined, the context of the Action validation is propogated to the Object property validation.  In the case of Action validation, this context is the Action alias.  </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> appendPrefix </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> true </td>
 * <td class='confluenceTd'> Determines whether the field name of this field validator should be prepended to the field name of the visited field to determine the full field name when an error occurs.  For example, suppose that the bean being validated has a "name" property.  If <em>appendPrefix</em> is true, then the field error will be stored under the field "bean.name".  If <em>appendPrefix</em> is false, then the field error will be stored under the field "name".  <br clear="all" /> <img class="emoticon" src="/images/icons/emoticons/warning.gif" height="16" width="16" align="absmiddle" alt="" border="0"/> If you are using the VisitorFieldValidator to validate the model from a ModelDriven Action, you should set <em>appendPrefix</em> to false unless you are using "model.name" to reference the properties on your model. </td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * @VisitorFieldValidator(message = "Default message", key = "i18n.key", shortCircuit = true, context = "action alias", appendPrefix = true)
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitorFieldValidator {

    /**
     * The aliasNames are the names of the Action aliases as defined in the xwork.xml configuration for the particular Action.
     */
    String[] aliasNames() default {};

    /**
     * Determines the context to use for validating the Object property.
     * If not defined, the context of the Action validator is propogated to the Object property validator.
     * In the case of Action validator, this context is the Action alias.
     */
    String context() default "";

    /**
     * Determines whether the field name of this field validator should be prepended to the field name of
     * the visited field to determine the full field name when an error occurs. For example, suppose that
     * the bean being validated has a "name" property.
     *
     * If appendPrefix is true, then the field error will be stored under the field "bean.name".
     * If appendPrefix is false, then the field error will be stored under the field "name".
     *
     * If you are using the VisitorFieldValidator to validate the model from a ModelDriven Action,
     * you should set appendPrefix to false unless you are using "model.name" to reference the properties
     * on your model.
     */
    boolean appendPrefix() default true;

    /**
     * The default error message for this validator.
     */
    String message();

    /**
     * The message key to lookup for i18n.
     */
    String key() default "";

    /**
     * The optional fieldName for SIMPLE validator types.
     */
    String fieldName() default "";

    /**
     * If this is activated, the validator will be used as short-circuit.
     *
     * Adds the short-circuit="true" attribute value if <tt>true</tt>.
     *
     */
    boolean shortCircuit() default false;
}
