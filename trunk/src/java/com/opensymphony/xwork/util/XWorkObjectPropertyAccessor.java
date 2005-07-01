/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.xwork.util;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;

/**
 * @author Gabe
 */
public class XWorkObjectPropertyAccessor extends ObjectPropertyAccessor {

	/* (non-Javadoc)
	 * @see ognl.PropertyAccessor#getProperty(java.util.Map, java.lang.Object, java.lang.Object)
	 */
	public Object getProperty(Map context, Object target, Object oname)
			throws OgnlException {
		//set the last set objects in the context
		//so if the next objects accessed are
		//Maps or Collections they can use the information
		//to determine conversion types
		context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
		context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, oname.toString());

		return super.getProperty(context, target, oname);
	}
}
