/*
 * Copyright (c) 2004 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.xwork.config.entities;

import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.SimpleFooAction;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * ActionConfigTest
 *
 * @author Jason Carreira <jason@zenfrog.com>
 */
public class ActionConfigTest extends TestCase {
    public void testActionClassChangeGetsNewMethod() throws NoSuchMethodException {
        ActionConfig config = new ActionConfig();
        Method method1 = config.getMethod(SimpleAction.class);
        Method method2 = config.getMethod(SimpleFooAction.class);
        assertNotSame(method1,method2);
    }
}
