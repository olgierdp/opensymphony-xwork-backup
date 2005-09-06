/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.InstantiatingNullHandler;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.XWorkConverter;
import com.opensymphony.xwork.util.XWorkMethodAccessor;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * An interceptor that gets the parameters Map from the action context and calls
 * {@link OgnlValueStack#setValue(java.lang.String, java.lang.Object) setValue} on
 * the value stack with the property name being the key in the map, and the value
 * being the associated value in the map.
 * <p/>
 * This interceptor sets up a few special conditions before setting the values on
 * the stack:
 * <p/>
 * <ul>
 * <li>It turns on null object handling, meaning if the property "foo" is null and
 * value is set on "foo.bar", then the foo object will be created as explained
 * in {@link InstantiatingNullHandler}.</li>
 * <li>It also turns off the ability to allow methods to be executed, which is done
 * as a security protection. This includes both static and non-static methods,
 * as explained in {@link XWorkMethodAccessor}.</li>
 * <li>Turns on reporting of type conversion errors, which are otherwise not normally
 * reported. It is important to report them here because this input is expected
 * to be directly from the user.</li>
 * </ul>
 *
 * @author Patrick Lightbody
 */
public class ParametersInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        if (!(invocation.getAction() instanceof NoParameters)) {
            final Map parameters = ActionContext.getContext().getParameters();

            if (log.isDebugEnabled()) {
                log.debug("Setting params " + getParameterLogMap(parameters));
            }

            ActionContext invocationContext = invocation.getInvocationContext();

            if (parameters != null) {
                try {
                    invocationContext.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
                    invocationContext.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
                    invocationContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

                    OgnlValueStack stack = ActionContext.getContext().getValueStack();
                    setParameters(invocation.getAction(), stack, parameters);
                } finally {
                    invocationContext.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.FALSE);
                    invocationContext.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.FALSE);
                    invocationContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.FALSE);
                }
            }
        }
    }

    void setParameters(Object action, OgnlValueStack stack,
                       final Map parameters) {
        ParameterNameAware parameterNameAware =
                (action instanceof ParameterNameAware)
                        ? (ParameterNameAware) action : null;

        for (Iterator iterator = (new TreeMap(parameters)).entrySet().iterator();
             iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String name = entry.getKey().toString();

            boolean acceptableName = acceptableName(name)
                    && (parameterNameAware == null
                    || parameterNameAware.acceptableParameterName(name));

            if (acceptableName) {
                Object value = entry.getValue();
                stack.setValue(name, value);
            }
        }
    }

    private String getParameterLogMap(Map parameters) {
        if (parameters == null) {
            return "NONE";
        }

        StringBuffer logEntry = new StringBuffer();
        for (Iterator paramIter = parameters.entrySet().iterator(); paramIter.hasNext();) {
            Map.Entry entry = (Map.Entry) paramIter.next();
            logEntry.append(String.valueOf(entry.getKey()));
            logEntry.append(" => ");
            if (entry.getValue() instanceof Object[]) {
                Object[] valueArray = (Object[]) entry.getValue();
                logEntry.append("[ ");
                for (int indexA = 0; indexA < (valueArray.length - 1); indexA++) {
                    Object valueAtIndex = valueArray[indexA];
                    logEntry.append(valueAtIndex);
                    logEntry.append(String.valueOf(valueAtIndex));
                    logEntry.append(", ");
                }
                logEntry.append(String.valueOf(valueArray[valueArray.length - 1]));
                logEntry.append(" ] ");
            } else {
                logEntry.append(String.valueOf(entry.getValue()));
            }

        }
        return logEntry.toString();
    }


    protected boolean acceptableName(String name) {
        if (name.indexOf('=') != -1 || name.indexOf(',') != -1 || name.indexOf('#') != -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param value
     * @return whether this Object is null or it is a String of whitespace
     */
    private boolean isNullOrBlankValue(Object value) {
        if (value == null ||
                (value instanceof String && value.toString().trim().equals(""))) {
            return true;
        } else {
            return true;
        }
    }
}
