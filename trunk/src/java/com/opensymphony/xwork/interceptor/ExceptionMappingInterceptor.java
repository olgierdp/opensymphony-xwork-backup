package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.entities.ExceptionMappingConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Aug 14, 2005
 * Time: 12:40:02 PM
 */
public class ExceptionMappingInterceptor implements Interceptor {
    //~ Instance fields ////////////////////////////////////////////////////////
    protected Log log = LogFactory.getLog(this.getClass());
    private Throwable exception;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;

        try {
            result = invocation.invoke();
        } catch (Throwable t) {
            List exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings();
            result = this.findResultFromExceptions(exceptionMappings, t);
            exception = t;
            invocation.getStack().push(this);
        }

        return result;
    }

    public Throwable getException() {
        return exception;
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
     * Return the depth to the superclass matching.
     * 0 means ex matches exactly. Returns -1 if there's no match.
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



}
