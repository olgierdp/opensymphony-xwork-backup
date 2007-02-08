/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 28/02/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * @author CameronBraid
 */
public class ChainResultTest extends XWorkTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.getConfiguration().reload();
    }

    public void testNamespaceAndActionExpressionEvaluation() throws Exception {
        ActionChainResult result = new ActionChainResult();
        result.setActionName("${actionName}");
        result.setNamespace("${namespace}");

        String expectedActionName = "testActionName";
        String expectedNamespace = "testNamespace";
        Map values = new HashMap();
        values.put("actionName", expectedActionName);
        values.put("namespace", expectedNamespace);

        OgnlValueStack stack = new OgnlValueStack();
        stack.push(values);

        Mock actionProxyMock = new Mock(ActionProxy.class);
        actionProxyMock.expect("execute");

        ActionProxyFactory testActionProxyFactory = new NamespaceActionNameTestActionProxyFactory(expectedNamespace, expectedActionName, (ActionProxy) actionProxyMock.proxy());

        try {
            ActionProxyFactory.setFactory(testActionProxyFactory);

            ActionContext testContext = new ActionContext(stack.getContext());
            ActionContext.setContext(testContext);
            result.execute(null);
            actionProxyMock.verify();
        } finally {
            ActionProxyFactory.setFactory(new DefaultActionProxyFactory());
            ActionContext.setContext(null);
        }
    }

    public void testRecursiveChain() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", "InfiniteRecursionChain", null);

        try {
            proxy.execute();
            fail("did not detected repeated chain to an action");
        } catch (XworkException e) {
        }
    }

    private class NamespaceActionNameTestActionProxyFactory extends ActionProxyFactory {
        private ActionProxy returnVal;
        private String expectedActionName;
        private String expectedNamespace;

        public NamespaceActionNameTestActionProxyFactory(String expectedNamespace, String expectedActionName, ActionProxy returnVal) {
            this.expectedNamespace = expectedNamespace;
            this.expectedActionName = expectedActionName;
            this.returnVal = returnVal;
        }

        public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception {
            return null;
        }

        public ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception {
            return null;
        }

        public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
            return null;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }
    }
}
