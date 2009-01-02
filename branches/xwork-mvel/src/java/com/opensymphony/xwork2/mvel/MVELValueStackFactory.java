/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.mvel;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import java.util.Map;
import java.util.Set;

import org.mvel2.integration.*;
import org.mvel2.MVEL;

/**
 * Creates an MVEL value stack
 */
public class MVELValueStackFactory implements ValueStackFactory {

    private XWorkConverter xworkConverter;
    private CompoundRootAccessor compoundRootAccessor;
    private TextProvider textProvider;
    private Container container;
    private boolean allowStaticMethodAccess;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject("system")
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    @Inject(value = "allowStaticMethodAccess", required = false)
    public void setAllowStaticMethodAccess(String allowStaticMethodAccess) {
        this.allowStaticMethodAccess = "true".equalsIgnoreCase(allowStaticMethodAccess);
    }

    public ValueStack createValueStack() {
        ValueStack stack = new MVELValueStack(xworkConverter, compoundRootAccessor, textProvider, allowStaticMethodAccess);
        container.inject(stack);
        stack.getContext().put(ActionContext.CONTAINER, container);
        return stack;
    }

    public ValueStack createValueStack(ValueStack stack) {
        ValueStack result = new MVELValueStack(stack, xworkConverter, compoundRootAccessor, allowStaticMethodAccess);
        container.inject(result);
        stack.getContext().put(ActionContext.CONTAINER, container);
        return result;
    }

    @Inject
    public void setContainer(Container container) throws ClassNotFoundException {
        Set<String> names = container.getInstanceNames(PropertyHandler.class);
        if (names != null) {
            MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
            for (String name : names) {
                //the handler for nullHandler does not handle an specific class
                //but null values on any class
                if (!"nullHandler".equals(name)) {
                    Class cls = Class.forName(name);
                    if (cls != null) {
                        PropertyHandler instance = container.getInstance(PropertyHandler.class, name);
                        PropertyHandlerFactory.registerPropertyHandler(cls, instance);
                    }
                }
            }
        }

        compoundRootAccessor = (CompoundRootAccessor) container.getInstance(CompoundRootAccessor.class);

        PropertyHandler nullHandler = container.getInstance(PropertyHandler.class, "nullHandler");
        //what if it is null? oh the irony
        if (nullHandler != null) {
            PropertyHandlerFactory.setNullMethodHandler(nullHandler);
            PropertyHandlerFactory.setNullPropertyHandler(nullHandler);
        }

        Listener objectListener = container.getInstance(Listener.class);
        if (objectListener != null) {
            GlobalListenerFactory.registerGetListener(objectListener);
            GlobalListenerFactory.registerSetListener(objectListener);
        }

        /*names = container.getInstanceNames(MethodAccessor.class);
        if (names != null) {
            for (String name : names) {
                Class cls = Class.forName(name);
                if (cls != null) {
                    OgnlRuntime.setMethodAccessor(cls, container.getInstance(MethodAccessor.class, name));
                }
            }
        }

        }*/

        if (compoundRootAccessor == null) {
            throw new IllegalStateException("Couldn't find the compound root accessor");
        }
        this.container = container;
    }
}
