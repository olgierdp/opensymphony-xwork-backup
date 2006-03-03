/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.util.*;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor gets all parameters from {@link ActionContext#getParameters()} and sets them on the value stack by
 * calling {@link OgnlValueStack#setValue(String, Object)}, typically resulting in the values submitted in a form
 * request being applied to an action in the value stack. Note that the parameter map must contain a String key and
 * often containers a String[] for the value.
 *
 * <p/> Because parameter names are effectively OGNL statements, it is important that security be taken in to account.
 * This interceptor will not apply any values in the parameters map if the expression contains an assignment (=),
 * multiple expressions (,), or references any objects in the context (#). This is all done in the {@link
 * #acceptableName(String)} method. In addition to this method, if the action being invoked implements the {@link
 * ParameterNameAware} interface, the action will be consulted to determine if the parameter should be set.
 *
 * <p/> In addition to these restrictions, a flag ({@link XWorkMethodAccessor#DENY_METHOD_EXECUTION}) is set such that
 * no methods are allowed to be invoked. That means that any expression such as <i>person.doSomething()</i> or
 * <i>person.getName()</i> will be explicitely forbidden. This is needed to make sure that your application is not
 * exposed to attacks by malicious users.
 *
 * <p/> While this interceptor is being invoked, a flag ({@link InstantiatingNullHandler#CREATE_NULL_OBJECTS}) is turned
 * on to ensure that any null reference is automatically created - if possible. See the type conversion documentation
 * and the {@link InstantiatingNullHandler} javadocs for more information.
 *
 * <p/> Finally, a third flag ({@link XWorkConverter#REPORT_CONVERSION_ERRORS}) is set that indicates any errors when
 * converting the the values to their final data type (String[] -&gt; int) an unrecoverable error occured. With this
 * flag set, the type conversion errors will be reported in the action context. See the type conversion documentation
 * and the {@link XWorkConverter} javadocs for more information.
 *
 * <p/> If you are looking for detailed logging information about your parameters, turn on DEBUG level logging for this
 * interceptor. A detailed log of all the parameter keys and values will be reported.
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
 * @author Patrick Lightbody
 */
public class ParametersInterceptor extends AroundInterceptor {

    private static final Log LOG = LogFactory.getLog(ParametersInterceptor.class);

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
                try {
                    stack.setValue(name, value);
                } catch (RuntimeException e) {
                    final Boolean devMode = (Boolean) stack.getContext().get(ActionContext.DEV_MODE);
                    if (devMode != null && devMode.booleanValue()) {
                        String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "webwork.messages.devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification (set webwork.devMode to false to disable this message):\n{0}", new Object[]{
                                e.getMessage()
                        });
                        LOG.error(developerNotification);
                        if (action instanceof ValidationAware) {
                            ((ValidationAware) action).addActionMessage(developerNotification);
                        }
                    } else {
                        LOG.error("ParametersInterceptor - [setParameters]: Unexpected Exception catched: " + e.getMessage());
                    }
                }
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
        if (name.indexOf('=') != -1 || name.indexOf(',') != -1 || name.indexOf('#') != -1
                || name.indexOf(':') != -1) {
            return false;
        } else {
            return true;
        }
    }
}
