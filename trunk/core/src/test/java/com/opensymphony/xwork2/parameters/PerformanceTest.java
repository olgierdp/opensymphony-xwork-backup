/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters;

import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.ognl.OgnlUtil;

import java.util.Map;
import java.util.HashMap;

public class PerformanceTest extends XWorkTestCase {
    public void testPerformance() throws Exception {

        SimpleAction action = new SimpleAction();
        Map<String, Object> emptyMap = new HashMap();
        XWorkParametersBinder binder = container.getInstance(XWorkParametersBinder.class);
        OgnlUtil ognlUtil = container.getInstance(OgnlUtil.class);

        int k = 200000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < k; i++) {
            binder.setProperty(emptyMap, action, "theProtectedMap['p0 p1']", "test");
            binder.setProperty(emptyMap, action, "otherMap['my_hero'].name", "test");
            binder.setProperty(emptyMap, action, "nestedAction.bean.name", "test");
        }

        long end = System.currentTimeMillis();
        System.out.println("params: " + (end - start) / 1000);

        action = new SimpleAction();
        emptyMap = new HashMap();

        start = System.currentTimeMillis();
        for (int i = 0; i < k; i++) {
            ognlUtil.setProperty("theProtectedMap['p0 p1']", "test", action, emptyMap);
            ognlUtil.setProperty("otherMap['my_hero'].name", "test", action, emptyMap);
            ognlUtil.setProperty("nestedAction.bean.name", "test", action, emptyMap);
        }

        end = System.currentTimeMillis();
        System.out.println("ognl: " + (end - start) / 1000);
    }
}
