/*
 * Created on 6/10/2003
 *
 */
package com.opensymphony.xwork.util;

import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;
import junit.framework.TestCase;

/**
 * @author CameronBraid
 *
 */
public class SetPropertiesTest extends TestCase
{

	/**
	 * 
	 */
	public SetPropertiesTest(String a)
	{
		super(a);
	}


	public void testEmptyStringToLong() {
		Bar bar = new Bar();
		bar.setId(null);

		Map context = Ognl.createDefaultContext(bar);

		HashMap props = new HashMap();
		props.put("id", "");

		OgnlUtil.setProperties(props, bar, context);
		assertNull(bar.getId());

		props.put("id", new String[] {""});

		bar.setId(null);
		OgnlUtil.setProperties(props, bar, context);
		assertNull(bar.getId());
	}

	public void testSetEmptyStringAsLong() {
		Bar bar = new Bar();

		OgnlValueStack vs = new OgnlValueStack();
		vs.push(bar);

		vs.setValue("id", "");
		assertNull(bar.getId());

		vs.setValue("id", new String[]{""});
		assertNull(bar.getId());
	}

}
