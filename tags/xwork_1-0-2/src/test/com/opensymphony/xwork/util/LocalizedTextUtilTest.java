/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.test.ModelDrivenAction2;
import com.opensymphony.xwork.test.TestBean2;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.Locale;
import java.util.MissingResourceException;


/**
 * LocalizedTextUtilTest
 *
 * @author Jason Carreira
 *         Created Apr 20, 2003 12:07:17 AM
 */
public class LocalizedTextUtilTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testActionGetText() {
        try {
            ModelDrivenAction2 action = new ModelDrivenAction2();
            TestBean2 bean = (TestBean2) action.getModel();
            Bar bar = new Bar();
            bean.setBarObj(bar);

            Mock mockActionInvocation = new Mock(ActionInvocation.class);
            mockActionInvocation.expectAndReturn("getAction", action);
            ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());
            ActionContext.getContext().getValueStack().push(action);
            ActionContext.getContext().getValueStack().push(action.getModel());

            String message = action.getText("barObj.title");
            assertEquals("Title:", message);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testActionGetTextXXX() {
        try {
            LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/util/FindMe");

            SimpleAction action = new SimpleAction();

            Mock mockActionInvocation = new Mock(ActionInvocation.class);
            mockActionInvocation.expectAndReturn("getAction", action);
            ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());
            ActionContext.getContext().getValueStack().push(action);

            String message = action.getText("bean.name");
            String foundBean2 = action.getText("bean2.name");

            assertEquals("Okay! You found Me!", foundBean2);
            assertEquals("Haha you cant FindMe!", message);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testAddDefaultResourceBundle() {
        try {
            LocalizedTextUtil.findDefaultText("foo.range", Locale.getDefault());
            fail("Found message when it should not be available.");
        } catch (MissingResourceException e) {
        }

        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/SimpleAction");

        try {
            String message = LocalizedTextUtil.findDefaultText("foo.range", Locale.US);
            assertEquals("Foo Range Message", message);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testAddDefaultResourceBundle2() {
        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/SimpleAction");

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("/", "packagelessAction", Collections.EMPTY_MAP, false);
            proxy.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDefaultMessage() {
        try {
            String message = LocalizedTextUtil.findDefaultText(XWorkMessages.ACTION_EXECUTION_ERROR, Locale.getDefault());
            assertEquals("Error during Action invocation", message);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDefaultMessageOverride() {
        try {
            String message = LocalizedTextUtil.findDefaultText(XWorkMessages.ACTION_EXECUTION_ERROR, Locale.getDefault());
            assertEquals("Error during Action invocation", message);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            fail();
        }

        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/test");

        try {
            String message = LocalizedTextUtil.findDefaultText(XWorkMessages.ACTION_EXECUTION_ERROR, Locale.getDefault());
            assertEquals("Testing resource bundle override", message);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFindTextInChildProperty() {
        try {
            ModelDriven action = new ModelDrivenAction2();
            TestBean2 bean = (TestBean2) action.getModel();
            Bar bar = new Bar();
            bean.setBarObj(bar);

            Mock mockActionInvocation = new Mock(ActionInvocation.class);
            mockActionInvocation.expectAndReturn("getAction", action);
            ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());
            ActionContext.getContext().getValueStack().push(action);
            ActionContext.getContext().getValueStack().push(action.getModel());

            String message = LocalizedTextUtil.findText(ModelDrivenAction2.class, "invalid.fieldvalue.barObj.title", Locale.getDefault());
            assertEquals("Title is invalid!", message);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testFindTextInInterface() {
        try {
            Action action = new ModelDrivenAction2();
            Mock mockActionInvocation = new Mock(ActionInvocation.class);
            mockActionInvocation.expectAndReturn("getAction", action);
            ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());

            String message = LocalizedTextUtil.findText(ModelDrivenAction2.class, "test.foo", Locale.getDefault());
            assertEquals("Foo!", message);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testFindTextInPackage() {
        try {
            ModelDriven action = new ModelDrivenAction2();

            Mock mockActionInvocation = new Mock(ActionInvocation.class);
            mockActionInvocation.expectAndReturn("getAction", action);
            ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());

            String message = LocalizedTextUtil.findText(ModelDrivenAction2.class, "package.properties", Locale.getDefault());
            assertEquals("It works!", message);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testParameterizedDefaultMessage() {
        try {
            String message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_ACTION_EXCEPTION, Locale.getDefault(), new String[] {
                    "AddUser"
                });
            assertEquals("There is no Action mapped for action name AddUser", message);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParameterizedDefaultMessageWithPackage() {
        try {
            String message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_PACKAGE_ACTION_EXCEPTION, Locale.getDefault(), new String[] {
                    "blah", "AddUser"
                });
            assertEquals("There is no Action mapped for namespace blah and action name AddUser", message);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        ConfigurationManager.destroyConfiguration();
        ConfigurationManager.getConfiguration().reload();

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
        ActionContext.getContext().setLocale(Locale.US);
    }
}
