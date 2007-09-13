/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.ognl;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.MethodAccessor;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectProxyPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkEnumerationAcccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkIteratorPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkListPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMapPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMethodAccessor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProviderFactory;

/**
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory implements ValueStackFactory {
    
    private XWorkConverter xworkConverter;
    private CompoundRootAccessor compoundRootAccessor;
    private TextProvider textProvider;
    private Container container;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public ValueStack createValueStack() {
        ValueStack stack = new OgnlValueStack(xworkConverter, compoundRootAccessor, textProvider);
        container.inject(stack);
        return stack;
    }

    public ValueStack createValueStack(ValueStack stack) {
        ValueStack result = new OgnlValueStack(stack, xworkConverter, compoundRootAccessor);
        container.inject(result);
        return result;
    }
    
    @Inject
    public void setContainer(Container container) throws ClassNotFoundException {
        Set<String> names = container.getInstanceNames(PropertyAccessor.class);
        if (names != null) {
            for (String name : names) {
                Class cls = Class.forName(name);
                if (cls != null) {
                    OgnlRuntime.setPropertyAccessor(cls, container.getInstance(PropertyAccessor.class, name));
                    if (compoundRootAccessor == null && CompoundRoot.class.isAssignableFrom(cls)) {
                        compoundRootAccessor = (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, name);
                    }
                }
            }
        }
        
        names = container.getInstanceNames(MethodAccessor.class);
        if (names != null) {
            for (String name : names) {
                Class cls = Class.forName(name);
                if (cls != null) {
                    OgnlRuntime.setMethodAccessor(cls, container.getInstance(MethodAccessor.class, name));
                }
            }
        }
        
        names = container.getInstanceNames(NullHandler.class);
        if (names != null) {
            for (String name : names) {
                Class cls = Class.forName(name);
                if (cls != null) {
                    OgnlRuntime.setNullHandler(cls, new OgnlNullHandlerWrapper(container.getInstance(NullHandler.class, name)));
                }
            }
        }
        if (compoundRootAccessor == null) {
            throw new IllegalStateException("Couldn't find the compound root accessor");
        }
        this.container = container;
    }
}
