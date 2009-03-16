/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class for features needed to compliment the enhancement added to Ognl 2.7.x
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class OgnlTools {

    private static final Map PRIMITIVE_VALUE_GETTERS = new HashMap();

    /**
     * Constant strings for getting the primitive value of different
     * native types on the generic {@link Number} object interface. (or the less
     * generic BigDecimal/BigInteger types)
     */
    static {
        PRIMITIVE_VALUE_GETTERS.put(Double.class, "doubleValue()");
        PRIMITIVE_VALUE_GETTERS.put(Float.class, "floatValue()");
        PRIMITIVE_VALUE_GETTERS.put(Integer.class, "intValue()");
        PRIMITIVE_VALUE_GETTERS.put(Long.class, "longValue()");
        PRIMITIVE_VALUE_GETTERS.put(Short.class, "shortValue()");
        PRIMITIVE_VALUE_GETTERS.put(Byte.class, "byteValue()");
        PRIMITIVE_VALUE_GETTERS.put(BigDecimal.class, "doubleValue()");
        PRIMITIVE_VALUE_GETTERS.put(BigInteger.class, "doubleValue()");
        PRIMITIVE_VALUE_GETTERS.put(Boolean.class, "booleanValue()");
        PRIMITIVE_VALUE_GETTERS.put(Character.class, "charValue()");
    }

    public static String getPrimitiveValueGetter(Class primitiveWrapperClass) {
        return (String) PRIMITIVE_VALUE_GETTERS.get(primitiveWrapperClass);
    }
}
