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
 * <!-- START SNIPPET: description -->
 *
 * This interceptor gets all parameters from {@link ActionContext#getParameters()} and sets them on the value stack by
 * calling {@link OgnlValueStack#setValue(String, Object)}, typically resulting in the values submitted in a form
 * request being applied to an action in the value stack.
 *
 * <p/> Because parameter names are effectively OGNL statements, it is important that security be taken in to account.
 * This interceptor will not apply any values in the parameters map if the expression contains an assignment (=),
 * multiple expressions (,), or references any objects in the context (#). This is all done in the {@link
 * #acceptableName(String)} method. In addition to this method, if the action being invoked implements the {@link
 * ParameterNameAware} interface, the action will be consulted to determine if the parameter should be set.
 *
 * <p/> In addition to these restrictions  
 *
 *
 *
 * <p/> Conversion errors, method execution, null props
 *
 * <p/> Logging...
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <p/> The best way to add behavior to this interceptor is to utilize the {@link ParameterNameAware} interface in your
 * actions. However, if you wish to apply a global rule that isn't implemented in your action, then you could extend
 * this interceptor and override the {@link #acceptableName(String)} method.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * An interceptor that gets the parameters Map from the action context and calls {@link
 * OgnlValueStack#setValue(java.lang.String, java.lang.Object) setValue} on the value stack with the property name being
 * the key in the map, and the value being the associated value in the map. <p/> This interceptor sets up a few special
 * conditions before setting the values on the stack: <p/> <ul> <li>It turns on null object handling, meaning if the
 * property "foo" is null and value is set on "foo.bar", then the foo object will be created as explained in {@link
 * InstantiatingNullHandler}.</li> <li>It also turns off the ability to allow methods to be executed, which is done as a
 * security protection. This includes both static and non-static methods, as explained in {@link
 * XWorkMethodAccessor}.</li> <li>Turns on reporting of type conversion errors, which are otherwise not normally
 * reported. It is important to report them here because this input is expected to be directly from the user.</li>
 * </ul>
 *
 * @author Patrick Lightbody
 */
public class ParametersInterceptor extends AroundInterceptor {

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        if (!(invocation.getAction() instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            final Map parameters = ac.getParameters();

            if (log.isDebugEnabled()) {
                log.debug("Setting params " + getParameterLogMap(parameters));
            }

            if (parameters != null) {
                try {
                    ac.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
                    ac.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
                    ac.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

                    OgnlValueStack stack = ac.getValueStack();
                    setParameters(invocation.getAction(), stack, parameters);
                } finally {
                    ac.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.FALSE);
                    ac.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.FALSE);
                    ac.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.FALSE);
                }
            }
        }
    }

    protected void setParameters(Object action, OgnlValueStack stack, final Map parameters) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware)
                ? (ParameterNameAware) action : null;

        for (Iterator iterator = (new TreeMap(parameters)).entrySet().iterator(); iterator.hasNext();) {
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
}
