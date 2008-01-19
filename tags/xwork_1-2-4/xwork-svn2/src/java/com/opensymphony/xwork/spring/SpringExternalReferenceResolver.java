/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.spring;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.ExternalReferenceResolver;
import com.opensymphony.xwork.config.ReferenceResolverException;
import com.opensymphony.xwork.config.entities.ExternalReference;
import com.opensymphony.xwork.util.OgnlUtil;
import ognl.Ognl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Resolves an xwork external-ref references to a component available from Spring application
 * context.
 *
 * @author Ross
 */
public class SpringExternalReferenceResolver implements
        ExternalReferenceResolver, ApplicationContextAware 
{
    protected ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void resolveReferences(ActionInvocation invocation) throws ReferenceResolverException {
        if (applicationContext == null)
            throw new IllegalStateException("The application context has not been set on this resolver");
        
        List externalRefs = invocation.getProxy().getConfig().getExternalRefs();
        Object bean;
        ExternalReference reference;

        Iterator iter = externalRefs.iterator();
        while (iter.hasNext()) {
            reference = (ExternalReference) iter.next();
            //IF the reference name is null we can can try and look up the
            // reference based on the type
            if (reference.getExternalRef() == null) {
                Class[] types = getParameterTypes(invocation.getAction(), reference.getName());
                if (types == null || types.length == 0 || types.length > 1) {
                    throw new ReferenceResolverException(
                        "Unable to find a method on the action called "
                            + reference.getName()
                            + " that takes a single parameter");
                } else {
                    String names[] = applicationContext.getBeanNamesForType(types[0]);
                    if (names == null || names.length == 0 || names.length > 1) {
                        throw new ReferenceResolverException(
                            "The container is unable to resolve single instance of " + types[0]);
                    } else {
                        reference.setExternalRef(names[0]);
                    }
                }
            }

            try {
                bean = applicationContext.getBean(reference.getExternalRef());
            } catch (NoSuchBeanDefinitionException e) {
                if (reference.isRequired()) {
                    //if a dependacy is required but wasn't found throw an
                    // exception
                    throw new ReferenceResolverException(
                        "Failed to find external reference: " + reference.getExternalRef(),
                        e);
                } else {
                    return;
                }
            }

            try {
                Map context = Ognl.createDefaultContext(invocation.getAction());
                OgnlUtil.setProperty(reference.getName(), bean, invocation.getAction(), context);
            } catch (Exception e) {
                throw new ReferenceResolverException(
                    "Failed to set external reference: "
                        + reference.getExternalRef()
                        + " for bean attribute: "
                        + reference.getName()
                        + ". "
                        + e.getMessage(),
                    e);
            }
        }
    }

    //TODO find a utility class that does this or put it in a utility class
    //TODO We will also want to cache this info somewhere so we dont execute
    //     each time a request is made
    private Class[] getParameterTypes(Object bean, String methodName) {
        if(!methodName.startsWith("set")) {
            methodName = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        }
        Method methods[] = bean.getClass().getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                return methods[i].getParameterTypes();
            }
        }
        return null;
    }
}
