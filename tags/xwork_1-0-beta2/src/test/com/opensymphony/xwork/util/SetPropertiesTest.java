/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 6/10/2003
 *
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import ognl.Ognl;

import java.util.HashMap;
import java.util.Map;


/**
 * @author CameronBraid
 *
 */
public class SetPropertiesTest extends TestCase {
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *
     */
    public SetPropertiesTest(String a) {
        super(a);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testOgnlUtilEmptyStringAsLong() {
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

    public void testValueStackSetValueEmptyStringAsLong() {
        Bar bar = new Bar();

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(bar);

        vs.setValue("id", "");
        assertNull(bar.getId());

        bar.setId(null);

        vs.setValue("id", new String[] {""});
        assertNull(bar.getId());
    }
}
