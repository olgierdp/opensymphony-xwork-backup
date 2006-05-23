/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.test.ModelDrivenAction2;
import com.opensymphony.xwork.test.TestBean2;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;


/**
 * Unit test for {@link LocalizedTextUtil}.
 *
 * @author jcarreira
 */
public class LocalizedTextUtilTest extends TestCase {

    public void testActionGetText() throws Exception {
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
    }

    public void testNullKeys() {
        LocalizedTextUtil.findText(this.getClass(), null, Locale.getDefault());
    }

    public void testActionGetTextXXX() throws Exception {
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
    }

    public void testAddDefaultResourceBundle() {
        String text = LocalizedTextUtil.findDefaultText("foo.range", Locale.getDefault());
        assertNull("Found message when it should not be available.", null);

        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/SimpleAction");

        String message = LocalizedTextUtil.findDefaultText("foo.range", Locale.US);
        assertEquals("Foo Range Message", message);
    }

    public void testAddDefaultResourceBundle2() throws Exception {
        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/SimpleAction");

        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("/", "packagelessAction", Collections.EMPTY_MAP, false, true);
        proxy.execute();
    }

    public void testDefaultMessage() throws Exception {
        String message = LocalizedTextUtil.findDefaultText(XWorkMessages.ACTION_EXECUTION_ERROR, Locale.getDefault());
        assertEquals("Error during Action invocation", message);
    }

    public void testDefaultMessageOverride() throws Exception {
        String message = LocalizedTextUtil.findDefaultText(XWorkMessages.ACTION_EXECUTION_ERROR, Locale.getDefault());
        assertEquals("Error during Action invocation", message);

        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/test");

        message = LocalizedTextUtil.findDefaultText(XWorkMessages.ACTION_EXECUTION_ERROR, Locale.getDefault());
        assertEquals("Testing resource bundle override", message);
    }

    public void testFindTextInChildProperty() throws Exception {
        ModelDriven action = new ModelDrivenAction2();
        TestBean2 bean = (TestBean2) action.getModel();
        Bar bar = new Bar();
        bean.setBarObj(bar);

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("hashCode", 0);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().getValueStack().push(action.getModel());

        String message = LocalizedTextUtil.findText(ModelDrivenAction2.class, "invalid.fieldvalue.barObj.title", Locale.getDefault());
        assertEquals("Title is invalid!", message);
    }

    public void testFindTextInInterface() throws Exception {
        Action action = new ModelDrivenAction2();
        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());

        String message = LocalizedTextUtil.findText(ModelDrivenAction2.class, "test.foo", Locale.getDefault());
        assertEquals("Foo!", message);
    }

    public void testFindTextInPackage() throws Exception {
        ModelDriven action = new ModelDrivenAction2();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getAction", action);
        ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());

        String message = LocalizedTextUtil.findText(ModelDrivenAction2.class, "package.properties", Locale.getDefault());
        assertEquals("It works!", message);
    }

    public void testParameterizedDefaultMessage() throws Exception {
        String message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_ACTION_EXCEPTION, Locale.getDefault(), new String[]{"AddUser"});
        assertEquals("There is no Action mapped for action name AddUser. Check if there is such an action name defined in xwork.xml and also if the such an action class exists. Check also the log to see if the action class is successfully loaded.", message);
    }

    public void testParameterizedDefaultMessageWithPackage() throws Exception {
        String message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_PACKAGE_ACTION_EXCEPTION, Locale.getDefault(), new String[]{"blah", "AddUser"});
        assertEquals("There is no Action mapped for namespace blah and action name AddUser. Check if there is such an action name with such namespace defined in the xwork.xml and also if such an action class exists. Check also the log to see if the action class is successfully loaded.", message);
    }

    public void testLocalizedDateFormatIsUsed() {
        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/util/LocalizedTextUtilTest");
        Object[] params = new Object[]{new Date()};
        String usDate = LocalizedTextUtil.findDefaultText("test.format.date", Locale.US, params);
        String germanDate = LocalizedTextUtil.findDefaultText("test.format.date", Locale.GERMANY, params);
        assertFalse(usDate.equals(germanDate));
    }

    public void testXW377() {
        LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/xwork/util/LocalizedTextUtilTest");

        String text = LocalizedTextUtil.findText(Bar.class, "xw377", ActionContext.getContext().getLocale(), "xw377", null, ActionContext.getContext().getValueStack());
        assertEquals("xw377", text); // should not log

        String text2 = LocalizedTextUtil.findText(LocalizedTextUtilTest.class, "notinbundle", ActionContext.getContext().getLocale(), "hello", null, ActionContext.getContext().getValueStack());
        assertEquals("hello", text2); // should log WARN

        String text3 = LocalizedTextUtil.findText(LocalizedTextUtilTest.class, "notinbundle.key", ActionContext.getContext().getLocale(), "notinbundle.key", null, ActionContext.getContext().getValueStack());
        assertEquals("notinbundle.key", text3); // should log WARN

        String text4 = LocalizedTextUtil.findText(LocalizedTextUtilTest.class, "xw377", ActionContext.getContext().getLocale(), "hello", null, ActionContext.getContext().getValueStack());
        assertEquals("xw377", text4); // should not log

        String text5 = LocalizedTextUtil.findText(LocalizedTextUtilTest.class, "username", ActionContext.getContext().getLocale(), null, null, ActionContext.getContext().getValueStack());
        assertEquals("Santa", text5); // should not log
    }

    protected void setUp() throws Exception {
        super.setUp();
        XWorkStatic.getConfigurationManager().destroyConfiguration();
        XWorkStatic.getConfigurationManager().getConfiguration().reload();

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
        ActionContext.getContext().setLocale(Locale.US);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        LocalizedTextUtil.clearDefaultResourceBundles();
    }

}
