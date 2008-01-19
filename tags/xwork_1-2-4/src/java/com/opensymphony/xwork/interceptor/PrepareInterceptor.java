/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Preparable;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor calls prepare() on actions which implement {@link Preparable}. This interceptor is very useful for
 * any situation where you need to ensure some logic runs before the actual execute method runs.
 *
 * <p/> A typical use of this is to run some logic to load an object from the database so that when parameters are set
 * they can be set on this object. For example, suppose you have a User object with two properties: <i>id</i> and
 * <i>name</i>. Provided that the params interceptor is called twice (once before and once after this interceptor), you
 * can load the User object using the id property, and then when the second params interceptor is called the parameter
 * <i>user.name</i> will be set, as desired, on the actual object loaded from the database. See the example for more
 * info.
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
 * There are no known extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- Calls the params interceptor twice, allowing you to
 *      pre-load data for the second time parameters are set --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="prepare"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * Date: Nov 5, 2003 2:33:11 AM
 *
 * @author Jason Carreira
 * @author Philip Luppens
 * @author tm_jee
 * @see com.opensymphony.xwork.Preparable
 */
public class PrepareInterceptor extends AroundInterceptor {

	private static final long serialVersionUID = -7182584510651751135L;

    private static final Log _log = LogFactory.getLog(PrepareInterceptor.class);
	
	private final static String PREPARE_PREFIX = "prepare";
	private final static String ALT_PREPARE_PREFIX = "prepareDo";

	private boolean alwaysInvokePrepare = true;
	
	public void setAlwaysInvokePrepare(String alwaysInvokePrepare) {
		this.alwaysInvokePrepare = Boolean.valueOf(alwaysInvokePrepare).booleanValue();
	}
	
    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    	
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof Preparable) {
        	try {
        		PrefixMethodInvocationUtil.invokePrefixMethod(invocation, 
        			new String[] { PREPARE_PREFIX, ALT_PREPARE_PREFIX });
        	}
        	catch(Exception e) {
        		// just in case there's an exception while doing reflection, 
        		// we still want prepare() to be able to get called.
        		_log.warn("an exception occured while trying to execute prefixed method", e);
        	}
        	if (alwaysInvokePrepare) {
        		((Preparable) action).prepare();
        	}
        }
    }
}
