/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.util.TextUtils;

import ognl.DefaultTypeConverter;

import java.lang.reflect.Member;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;


/**
 *
 *
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class XWorkBasicConverter extends DefaultTypeConverter {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Object OK_NULL_RESULT = new Object();

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object convertValue(Map context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;

        //short circuit conversion for empty strings... these are not conversion errors
        if ("".equals(value)) {
            result = (String.class.equals(toType)) ? "" : OK_NULL_RESULT;

            return result;
        }

        if (toType == String.class) {
            result = doConvertToString(context, value);
        } else if (toType == boolean.class) {
            result = doConvertToBoolean(context, value);
        } else if (toType == Boolean.class) {
            result = doConvertToBoolean(context, value);
        } else if (toType == boolean[].class) {
            result = doConvertToBoolArray(context, value);
        } else if (toType == Boolean[].class) {
            result = doConvertToBooleanArray(context, value);
        } else if (toType == int[].class) {
            result = doConvertToIntArray(context, value);
        } else if (toType == Integer[].class) {
            result = doConvertToIntegerArray(context, value);
        } else if (toType == long[].class) {
            result = doConvertToLongArray(context, value);
        } else if (toType == Long[].class) {
            result = doConvertToLongArray2(context, value);
        } else if (toType == double[].class) {
            result = doConvertToDoubleArray(context, value);
        } else if (toType == Double[].class) {
            result = doConvertToDoubleArray2(context, value);
        } else if (toType == float[].class) {
            result = doConvertToFloatArray(context, value);
        } else if (toType == Float[].class) {
            result = doConvertToFloatArray2(context, value);
        } else if (toType == Date.class) {
            result = doConvertToDate(context, value);
        } else if (toType == List.class) {
            result = doConvertToList(context, value);
        } else if (toType == Set.class) {
            result = doConvertToSet(context, value);
        } else if (toType == Collection.class) {
            result = doConvertToList(context, value);
        } else if (toType == Class.class) {
            result = doConvertToClass(context, value);
        } else {
            if ((toType == String[].class) && (value instanceof String)) {
                result = new String[] {(String) value};
            }
        }

        if (result == null) {
            if (value instanceof String[]) {
                String[] array = (String[]) value;

                if ((array != null) && (array.length == 1)) {
                    value = array[0];
                }

                // let's try to convert the first element only
                result = this.convertValue(context, o, member, s, value, toType);
            } else {
                result = super.convertValue(context, value, toType);
            }
        }

        if (result == null) {
            throw new TypeConversionException("Unable to convert value '" + value + "' to type " + toType.getName());
        }

        if (result == OK_NULL_RESULT) {
            return null;
        }

        return result;
    }

    private Object doConvertToBoolArray(Map context, Object value) {
        boolean[] result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            boolean[] ba = new boolean[sa.length];

            for (int i = 0; i < sa.length; i++) {
                ba[i] = ((Boolean) doConvertToBoolean(context, sa[i])).booleanValue();
            }

            result = ba;
        }

        return result;
    }

    private Object doConvertToBoolean(Map context, Object value) {
        if (value instanceof String) {
            String bStr = (String) value;

            return Boolean.valueOf(bStr);
        }

        return null;
    }

    private Object doConvertToBooleanArray(Map context, Object value) {
        Boolean[] result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            Boolean[] ba = new Boolean[sa.length];

            for (int i = 0; i < sa.length; i++) {
                ba[i] = ((Boolean) doConvertToBoolean(context, sa[i]));
            }

            result = ba;
        }

        return result;
    }

    private Class doConvertToClass(Map context, Object value) {
        Class clazz = null;

        if (value instanceof String) {
            try {
                clazz = Class.forName((String) value);
            } catch (ClassNotFoundException e) {
            }
        }

        return clazz;
    }

    private Date doConvertToDate(Map context, Object value) {
        Date result = null;

        if (value instanceof String) {
            String sa = (String) value;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            try {
                result = sdf.parse(sa);
            } catch (ParseException e) {
            }
        }

        return result;
    }

    private double[] doConvertToDoubleArray(Map context, Object value) {
        double[] result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            double[] da = new double[sa.length];

            for (int i = 0; i < sa.length; i++) {
                da[i] = ((Double) super.convertValue(context, sa[i], Double.class)).doubleValue();
            }

            result = da;
        }

        return result;
    }

    private Double[] doConvertToDoubleArray2(Map context, Object value) {
        double[] primitives = doConvertToDoubleArray(context, value);
        Double[] result = null;

        if (primitives != null) {
            result = new Double[primitives.length];

            for (int i = 0; i < primitives.length; i++) {
                double primitive = primitives[i];
                result[i] = new Double(primitive);
            }
        }

        return result;
    }

    private float[] doConvertToFloatArray(Map context, Object value) {
        float[] result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            float[] da = new float[sa.length];

            for (int i = 0; i < sa.length; i++) {
                da[i] = ((Float) super.convertValue(context, sa[i], Float.class)).floatValue();
            }

            result = da;
        }

        return result;
    }

    private Float[] doConvertToFloatArray2(Map context, Object value) {
        float[] primitives = doConvertToFloatArray(context, value);
        Float[] result = null;

        if (primitives != null) {
            result = new Float[primitives.length];

            for (int i = 0; i < primitives.length; i++) {
                float primitive = primitives[i];
                result[i] = new Float(primitive);
            }
        }

        return result;
    }

    private int[] doConvertToIntArray(Map context, Object value) {
        int[] result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            int[] ia = new int[sa.length];

            for (int i = 0; i < sa.length; i++) {
                ia[i] = ((Integer) super.convertValue(context, sa[i], Integer.class)).intValue();
            }

            result = ia;
        }

        return result;
    }

    private Integer[] doConvertToIntegerArray(Map context, Object value) {
        int[] primitives = doConvertToIntArray(context, value);
        Integer[] result = null;

        if (primitives != null) {
            result = new Integer[primitives.length];

            for (int i = 0; i < primitives.length; i++) {
                int primitive = primitives[i];
                result[i] = new Integer(primitive);
            }
        }

        return result;
    }

    private List doConvertToList(Map context, Object value) {
        List result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            result = new ArrayList(sa.length);

            for (int i = 0; i < sa.length; i++) {
                String s = sa[i];
                result.add(s);
            }
        }

        return result;
    }

    private long[] doConvertToLongArray(Map context, Object value) {
        long[] result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            long[] la = new long[sa.length];

            for (int i = 0; i < sa.length; i++) {
                la[i] = ((Long) super.convertValue(context, sa[i], Long.class)).longValue();
            }

            result = la;
        }

        return result;
    }

    private Long[] doConvertToLongArray2(Map context, Object value) {
        long[] primitives = doConvertToLongArray(context, value);
        Long[] result = null;

        if (primitives != null) {
            result = new Long[primitives.length];

            for (int i = 0; i < primitives.length; i++) {
                long primitive = primitives[i];
                result[i] = new Long(primitive);
            }
        }

        return result;
    }

    private Object doConvertToSet(Map context, Object value) {
        Set result = null;

        if (value instanceof String[]) {
            String[] sa = (String[]) value;
            result = new HashSet(sa.length);

            for (int i = 0; i < sa.length; i++) {
                String s = sa[i];
                result.add(s);
            }
        }

        return result;
    }

    private String doConvertToString(Map context, Object value) {
        String result = null;

        if (value instanceof int[]) {
            int[] x = (int[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Integer(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof long[]) {
            long[] x = (long[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Long(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof double[]) {
            double[] x = (double[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Double(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof boolean[]) {
            boolean[] x = (boolean[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Boolean(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            result = sdf.format(value);
        } else if (value instanceof String[]) {
            result = TextUtils.join(", ", (String[]) value);
        }

        return result;
    }
}
