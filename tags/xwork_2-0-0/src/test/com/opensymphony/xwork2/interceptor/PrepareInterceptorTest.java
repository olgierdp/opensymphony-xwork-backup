/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
*/
package com.opensymphony.xwork2.interceptor;

import org.easymock.MockControl;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.SimpleFooAction;
import junit.framework.TestCase;

/**
 * Unit test for PrepareInterceptor.
 *
 * @author Claus Ibsen
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class PrepareInterceptorTest extends TestCase {

    private Mock mock;
    private PrepareInterceptor interceptor;

    public void testPrepareCalled() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setMethod("execute");
        mai.setProxy(mockActionProxy);
        mai.setAction(mock.proxy());
        mock.expect("prepare");

        interceptor.intercept(mai);
    }

    public void testNoPrepareCalled() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(new SimpleFooAction());

        interceptor.intercept(mai);
    }
    
    public void testPrefixInvocation1() throws Exception {
    	
    	MockControl controlAction = MockControl.createControl(ActionInterface.class);
    	ActionInterface mockAction = (ActionInterface) controlAction.getMock();
    	mockAction.prepareSubmit();
    	controlAction.setVoidCallable(1);
    	mockAction.prepare();
    	controlAction.setVoidCallable(1);
    	
    	MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
    	ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
    	mockActionProxy.getMethod();
    	controlActionProxy.setDefaultReturnValue("submit");
    	
    	
    	MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
    	ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
    	mockActionInvocation.getAction();
    	controlActionInvocation.setDefaultReturnValue(mockAction);
    	mockActionInvocation.invoke();
    	controlActionInvocation.setDefaultReturnValue("okok");
    	mockActionInvocation.getProxy();
    	controlActionInvocation.setDefaultReturnValue(mockActionProxy);
    	
    	
    	controlAction.replay();
    	controlActionProxy.replay();
    	controlActionInvocation.replay();
    	
    	PrepareInterceptor interceptor = new PrepareInterceptor();
    	String result = interceptor.intercept(mockActionInvocation);
    	
    	assertEquals("okok", result);
    	
    	controlAction.verify();
    	controlActionProxy.verify();
    	controlActionInvocation.verify();
    }
    
    public void testPrefixInvocation2() throws Exception {
    	
    	MockControl controlAction = MockControl.createControl(ActionInterface.class);
    	ActionInterface mockAction = (ActionInterface) controlAction.getMock();
    	mockAction.prepare();
    	controlAction.setVoidCallable(1);
    	
    	MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
    	ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
    	mockActionProxy.getMethod();
    	controlActionProxy.setDefaultReturnValue("save");
    	
    	
    	MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
    	ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
    	mockActionInvocation.getAction();
    	controlActionInvocation.setDefaultReturnValue(mockAction);
    	mockActionInvocation.invoke();
    	controlActionInvocation.setDefaultReturnValue("okok");
    	mockActionInvocation.getProxy();
    	controlActionInvocation.setDefaultReturnValue(mockActionProxy);
    	
    	
    	controlAction.replay();
    	controlActionProxy.replay();
    	controlActionInvocation.replay();
    	
    	PrepareInterceptor interceptor = new PrepareInterceptor();
    	String result = interceptor.intercept(mockActionInvocation);
    	
    	assertEquals("okok", result);
    	
    	controlAction.verify();
    	controlActionProxy.verify();
    	controlActionInvocation.verify();
    }
    

    protected void setUp() throws Exception {
        mock = new Mock(Preparable.class);
        interceptor = new PrepareInterceptor();
    }

    protected void tearDown() throws Exception {
        mock.verify();
    }

    
    /**
     * Simple interface to test prefix action invocation 
     * eg. prepareSubmit(), prepareSave() etc.
     * 
     * @author tm_jee
     * @version $Date$ $Id$
     */
    public interface ActionInterface extends Action, Preparable {
    	void prepareSubmit();
    }
}
