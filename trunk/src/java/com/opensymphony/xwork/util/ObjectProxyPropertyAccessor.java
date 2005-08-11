/*
 * Created on Jun 17, 2005
 *
 */
package com.opensymphony.xwork.util;

import java.util.Map;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

/**
 * @author Gabe
 *
 */
public class ObjectProxyPropertyAccessor implements PropertyAccessor {

	/* (non-Javadoc)
	 * @see ognl.PropertyAccessor#getProperty(java.util.Map, java.lang.Object, java.lang.Object)
	 */
	public Object getProperty(Map context, Object target, Object name)
			throws OgnlException {
		ObjectProxy proxy=(ObjectProxy)target;
		setupContext(context,proxy);
		return OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass())
			.getProperty(context,target,name);
		
	}

	/**
	 * @param context
	 * @param proxy
	 * 
	 * Sets up the context with the last property and last class
	 * accessed.
	 */
	private void setupContext(Map context, ObjectProxy proxy) {
		OgnlContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
		OgnlContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
		
	}

	/* (non-Javadoc)
	 * @see ognl.PropertyAccessor#setProperty(java.util.Map, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public void setProperty(Map context, Object target, Object name, Object value)
			throws OgnlException {
		ObjectProxy proxy=(ObjectProxy)target;
		setupContext(context,proxy);
		OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass())
		.setProperty(context,target,name,value);
		

	}

}
