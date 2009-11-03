/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters.accessor;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


import java.util.*;


/**
 * Implementation similar to CompundRootAccessor, but does not depend on OGNL 
 */
public class ParametersCompoundRootAccessor implements ParametersPropertyAccessor {

    private final static Logger LOG = LoggerFactory.getLogger(ParametersCompoundRootAccessor.class);

    static boolean devMode = false;

    private ReflectionProvider reflectionProvider;

    @Inject("devMode")
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws Exception {
        CompoundRoot root = (CompoundRoot) target;

        for (Object o : root) {
            if (o == null) {
                continue;
            }
            if (reflectionProvider.getSetMethod(o.getClass(), name.toString()) != null) {
                reflectionProvider.setProperty(name.toString(), value, o, context);

                return;
            } else if (o instanceof Map) {
                Map<Object, Object> map = (Map) o;
                map.put(name, value);
                return;
            }

        }

        Boolean reportError = (Boolean) context.get(ValueStack.REPORT_ERRORS_ON_NO_PROP);

        final String msg = "No object in the CompoundRoot has a publicly accessible property named '" + name + "' (no setter could be found).";

        if ((reportError != null) && (reportError.booleanValue())) {
            throw new XWorkException(msg);
        } else {
            if (devMode) {
                LOG.warn(msg);
            }
        }
    }

    public Object getProperty(Map context, Object target, Object name) throws Exception {
        CompoundRoot root = (CompoundRoot) target;

        if (name instanceof Integer) {
            Integer index = (Integer) name;

            return root.cutStack(index.intValue());
        } else if (name instanceof String) {
            if ("top".equals(name)) {
                if (root.size() > 0) {
                    return root.get(0);
                } else {
                    return null;
                }
            }

            for (Object o : root) {
                if (o == null) {
                    continue;
                }

                try {
                    if ((reflectionProvider.getGetMethod(o.getClass(), name.toString()) != null) || ((o instanceof Map) && ((Map) o).containsKey(name))) {
                        return reflectionProvider.getValue(name.toString(), context, o);
                    }
                } catch (Exception e) {
                    final String msg = "Caught an exception while getting property " + name;
                    throw new XWorkException(msg, e);
                }
            }

            return null;
        } else {
            return null;
        }
    }
}
