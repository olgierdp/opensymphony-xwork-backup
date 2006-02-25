package com.opensymphony.xwork.interceptor;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.TextParseUtil;


/**
 * An abstract <code>Interceptor</code> that is applied to selectively according
 * to specified included/excluded method lists.
 * 
 * <p/>
 * 
 * Setable parameters are as follows:
 * 
 * <ul>
 * 		<li>excludeMethods - methods name to be excluded</li>
 * 		<li>includeMethods - methods name to be included</li>
 * </ul>
 * 
 * <p/>
 * 
 * <b>NOTE:</b> If method name are available in both includeMethods and 
 * excludeMethods, it will still be considered as an included method. In short
 * includeMethods takes precedence over excludeMethods.
 * 
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @version $Date$ $Id$
 */
public abstract class MethodFilterInterceptor implements Interceptor {
    protected transient Log log = LogFactory.getLog(getClass());
    
    protected Set excludeMethods = Collections.EMPTY_SET;
    protected Set includeMethods = Collections.EMPTY_SET;

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = TextParseUtil.commaDelimitedStringToSet(excludeMethods);
    }

    public void setIncludeMethods(String includeMethods) {
        this.includeMethods = TextParseUtil.commaDelimitedStringToSet(includeMethods);
    }

    /**
     * @see com.opensymphony.xwork.interceptor.Interceptor#intercept(com.opensymphony.xwork.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        if (applyInterceptor(invocation)) {
            return doIntercept(invocation);
        } 
        
        return invocation.invoke();
    }

    protected boolean applyInterceptor(ActionInvocation invocation) {
        String method = invocation.getProxy().getMethod();
        // ValidationInterceptor
        if (excludeMethods.contains(method) && !includeMethods.contains(method)) {
            log.debug("Skipping validation. Method [" + method + "] found in exclude list.");
            return false;
        }

        if ( includeMethods.size() == 0 || includeMethods.contains(method) ) {
            return true;
        }
        
        return false;
    }

    /**
     * Subclasses must override to implement the interceptor logic.
     * 
     * @param invocation the action invocation
     * @return the result of invocation
     * @throws Exception
     */
    protected abstract String doIntercept(ActionInvocation invocation) throws Exception;
    
    /**
     * @see com.opensymphony.xwork.interceptor.Interceptor#destroy()
     */
    public void destroy() {
    }

    /**
     * @see com.opensymphony.xwork.interceptor.Interceptor#init()
     */
    public void init() {
    }
}
