/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.util;

/**
 * <!-- START SNIPPET: description -->
 * <p/>Sets the CreateIfNull for type conversion.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>The CreateIfNull annotation must be applied at method level.
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
 * <td>false</td>
 * <td>The CreateIfNull property value.</td>
 * </tr>
 * </tbody>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * List<User> users = null;
 *
 * &#64;CreateIfNull( value = true )
 * public void setUsers(List<User> users) {
 *   this.users = users;
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public @interface CreateIfNull {

    /**
     * The CreateIfNull value.
     * Defaults to <tt>false</tt>.
     */
    boolean value() default false;
}
