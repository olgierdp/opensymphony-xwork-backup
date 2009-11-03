/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters.accessor;

import java.util.Map;

/**
 * This interface is implemented by accessors in this package, which do not depend
 * on OGNL and throw Exception (instead of OgnlException)
 */
public interface ParametersPropertyAccessor {
    Object getProperty(Map context, Object lastObject, Object name) throws Exception;

    void setProperty(Map context, Object target, Object property, Object value) throws Exception;
}
