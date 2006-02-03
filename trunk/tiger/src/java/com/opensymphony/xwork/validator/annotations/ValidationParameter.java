/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * <table class='confluenceTable'>
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Default </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>name</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>parameter name.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>value</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>parameter value.</td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author jepjep
 * @author Rainer Hermanns
 */
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationParameter {

	String name();

	String value();

}