/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.util.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: description -->
 * This interceptor sets all parameters on the value stack.
 * <p/>
 * This interceptor gets all parameters from {@link ActionContext#getParameters()} and sets them on the value stack by
 * calling {@link ValueStack#setValue(String, Object)}, typically resulting in the values submitted in a form
 * request being applied to an action in the value stack. Note that the parameter map must contain a String key and
 * often containers a String[] for the value.
 * 
 * <p/> The interceptor takes one parameter named 'ordered'. When set to true action properties are guaranteed to be
 * set top-down which means that top action's properties are set first. Then it's subcomponents properties are set.
 * The reason for this order is to enable a 'factory' pattern. For example, let's assume that one has an action 
 * that contains a property named 'modelClass' that allows to choose what is the underlying implementation of model.
 * By assuring that modelClass property is set before any model properties are set, it's possible to choose model
 * implementation during action.setModelClass() call. Similiarily it's possible to use action.setPrimaryKey() 
 * property set call to actually load the model class from persistent storage. Without any assumption on parameter 
 * order you have to use patterns like 'Preparable'.
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
 * <li>ordered - set to true if you want the top-down property setter behaviour</li>
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
public class ParametersInterceptor extends AbstractInterceptor {

    private static final Log LOG = LogFactory.getLog(ParametersInterceptor.class);

    boolean ordered = false;
    
    /** Compares based on number of '.' characters (fewer is higher) */
    static final Comparator rbCollator = new Comparator() {
        public int compare(Object arg0, Object arg1) {
            String s1 = (String) arg0;
            String s2 = (String) arg1;
            int l1=0, l2=0;
            for( int i=s1.length()-1; i>=0; i--) {
                if( s1.charAt(i) == '.') l1++;
            }
            for( int i=s2.length()-1; i>=0; i--) {
                if( s2.charAt(i) == '.') l2++;
            }
            return l1 < l2 ? -1 : ( l2 < l1 ? 1 : s1.compareTo(s2));
        };
    };
    
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            final Map parameters = ac.getParameters();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting params " + getParameterLogMap(parameters));
            }

            if (parameters != null) {
            	Map contextMap = ac.getContextMap();
                try {
                	OgnlContextState.setCreatingNullObjects(contextMap, true);
                	OgnlContextState.setDenyMethodExecution(contextMap, true);
                	OgnlContextState.setReportingConversionErrors(contextMap, true);

                    ValueStack stack = ac.getValueStack();
                    setParameters(action, stack, parameters);
                } finally {
                	OgnlContextState.setCreatingNullObjects(contextMap, false);
                	OgnlContextState.setDenyMethodExecution(contextMap, false);
                	OgnlContextState.setReportingConversionErrors(contextMap, false);
                }
            }
        }
        return invocation.invoke();
    }

    protected void setParameters(Object action, ValueStack stack, final Map parameters) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware)
                ? (ParameterNameAware) action : null;

        Map params = null;
        if( ordered ) {
            params = new TreeMap(getOrderedComparator());
            params.putAll(parameters);
        } else {
            params = new TreeMap(parameters); 
        }
        
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
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

    /**
     * Gets an instance of the comparator to use for the ordered sorting.  Override this
     * method to customize the ordering of the parameters as they are set to the 
     * action.
     * 
     * @return A comparator to sort the parameters
     */
    protected Comparator getOrderedComparator() {
        return rbCollator;
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

    /**
     * Whether to order the parameters or not
     * 
     * @return True to order
     */
    public boolean isOrdered() {
        return ordered;
    }

    /**
     * Set whether to order the parameters by object depth or not
     * 
     * @param ordered True to order them
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
}
