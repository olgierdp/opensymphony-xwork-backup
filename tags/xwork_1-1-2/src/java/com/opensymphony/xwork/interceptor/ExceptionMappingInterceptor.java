/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.entities.ExceptionMappingConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;

/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor forms the core functionality of the exception handling feature. Exception handling allows you to map
 * an exception to a result code, just as if the action returned a result code instead of throwing an unexpected
 * exception. When an exception is encountered, it is wrapped with an {@link ExceptionHolder} and pushed on the stack,
 * providing easy access to the exception from within your result.
 *
 * <b>Note:</b> While you can configure exception mapping in your configuration file at any point, the configuration
 * will not have any effect if this interceptor is not in the interceptor stack for your actions. It is recommended that
 * you make this interceptor the first interceptor on the stack, ensuring that it has full access to catch any
 * exception, even those caused by other interceptors.
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
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * If you want to add custom handling for publishing the Exception, you may override
 * {@link #publishException(com.opensymphony.xwork.ActionInvocation, ExceptionHolder)}. The default implementation
 * pushes the given ExceptionHolder on value stack. A custom implementation could add additional logging etc.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;xwork&gt;
 *     &lt;include file="webwork-default.xml"/&gt;
 *
 *     &lt;package name="default" extends="webwork-default"&gt;
 *         &lt;global-results&gt;
 *             &lt;result name="success" type="freemarker"&gt;error.ftl&lt;/result&gt;
 *         &lt;/global-results&gt;
 *
 *         &lt;global-exception-mappings&gt;
 *             &lt;exception-mapping exception="java.lang.Exception" result="error"/&gt;
 *         &lt;/global-exception-mappings&gt;
 *
 *         &lt;action name="test"&gt;
 *             &lt;interceptor-ref name="exception"/&gt;
 *             &lt;interceptor-ref name="basicStack"/&gt;
 *             &lt;exception-mapping exception="com.acme.CustomException" result="custom_error"/&gt;
 *             &lt;result name="custom_error"&gt;custom_error.ftl&lt;/result&gt;
 *             &lt;result name="success" type="freemarker"&gt;test.ftl&lt;/result&gt;
 *         &lt;/action&gt;
 *     &lt;/package&gt;
 * &lt;/xwork&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Matthew E. Porter (matthew dot porter at metissian dot com) Date: Aug 14, 2005 Time: 12:40:02 PM
 */
public class ExceptionMappingInterceptor implements Interceptor {
    
    protected static final Log log = LogFactory.getLog(ExceptionMappingInterceptor.class);


    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        String result;

        try {
            result = invocation.invoke();
        } catch (Exception e) {
            List exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings();
            String mappedResult = this.findResultFromExceptions(exceptionMappings, e);
            if (mappedResult != null) {
                result = mappedResult;
                publishException(invocation, new ExceptionHolder(e));
            } else {
                throw e;
            }
        }

        return result;
    }

    private String findResultFromExceptions(List exceptionMappings, Throwable t) {
        String result = null;

        // Check for specific exception mappings.
        if (exceptionMappings != null) {
            int deepest = Integer.MAX_VALUE;
            for (Iterator iter = exceptionMappings.iterator(); iter.hasNext();) {
                ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig) iter.next();
                int depth = getDepth(exceptionMappingConfig.getExceptionClassName(), t);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    result = exceptionMappingConfig.getResult();
                }
            }
        }

        return result;
    }

    /**
     * Return the depth to the superclass matching. 0 means ex matches exactly. Returns -1 if there's no match.
     * Otherwise, returns depth. Lowest depth wins.
     */
    public int getDepth(String exceptionMapping, Throwable t) {
        return getDepth(exceptionMapping, t.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class exceptionClass, int depth) {
        if (exceptionClass.getName().indexOf(exceptionMapping) != -1) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    /**
     * Default implementation to handle ExceptionHolder publishing. Pushes given ExceptionHolder on the stack.
     * Subclasses may override this to customize publishing.
     *
     * @param invocation The invocation to publish Exception for.
     * @param exceptionHolder The exceptionHolder wrapping the Exception to publish.
     */
    protected void publishException(ActionInvocation invocation, ExceptionHolder exceptionHolder) {
        invocation.getStack().push(exceptionHolder);
    }
}
