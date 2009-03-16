/*
 * Created on Jun 12, 2004
 */
package com.opensymphony.xwork.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.opensymphony.xwork.Action;


/**
 * @author Simon Stewart
 */
public class ExecuteInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation mi) throws Throwable {
        if ("execute".equals(mi.getMethod().getName()))
            return Action.INPUT;
        return mi.proceed();
    }
    
}
