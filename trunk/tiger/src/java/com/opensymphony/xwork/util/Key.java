/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */

package com.opensymphony.xwork.util;

/**
 * <!-- START SNIPPET: description -->
 * <p/>Sets the Key for type conversion.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>The Key annotation must be applied at method level.
 * <!-- END SNIPPET: usage -->
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
 * <td>value</td>
 * <td>no</td>
 * <td>java.lang.Object.class</td>
 * <td>The key property value.</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * // The key property for User objects within the users collection is the <code>userName</code> attribute.
 * Map<Long, User> userMap = null;
 *
 * @Key( value = java.lang.Long.class )
 * public void setUserMap(Map<Long, User> userMap) {
 *   this.userMap = userMap;
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public @interface Key {

    /**
     * The Key value.
     * Defaults to <tt>java.lang.Object.class</tt>.
     */
    Class value() default java.lang.Object.class;
}
