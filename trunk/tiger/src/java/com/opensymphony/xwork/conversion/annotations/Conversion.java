/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.conversion.annotations;

import java.lang.annotation.*;

/**
 * <!-- START SNIPPET: description -->
 * <p/>A marker annotation for type conversions at Type level.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>The Conversion annotation must be applied at Type level.
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
 * <td>conversion</td>
 * <td>no</td>
 * <td>&nbsp;</td>
 * <td>used for Type Conversions applied at Type level.</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * @Conversion()
 * public class ConversionAction implements Action {
 * }
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Conversion {

    /**
     * Allow Type Conversions being applied at Type level.
     */
    TypeConversion[] conversions() default {};
}
