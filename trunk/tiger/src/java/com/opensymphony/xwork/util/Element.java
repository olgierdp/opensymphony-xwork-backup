/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.util;

/**
 * <!-- START SNIPPET: description -->
 * <p/>Sets the Element for type conversion.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * <p/>The Element annotation must be applied at method level.
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
 * <td>The element property value.</td>
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
 * List<User> userList = null;
 *
 * &#64;Element( value = com.acme.User )
 * public void setUserMap(Map<Long, User> userMap) {
 *   this.userMap = userMap;
 * }
 *
 * &#64;Element( value = com.acme.User )
 * public void setUserList(List<User> userList) {
 *   this.userList = userList;
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public @interface Element {

    /**
     * The Element value.
     * Defaults to <tt>java.lang.Object.class</tt>.
     */
    Class value() default java.lang.Object.class;
}
