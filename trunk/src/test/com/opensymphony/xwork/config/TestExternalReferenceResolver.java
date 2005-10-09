/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Foo;
import com.opensymphony.xwork.config.entities.ExternalReference;
import com.opensymphony.xwork.util.OgnlUtil;
import ognl.Ognl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Test resolver
 */
public class TestExternalReferenceResolver implements ExternalReferenceResolver {
    private Map references;

    public TestExternalReferenceResolver() {
        references = new HashMap();
        references.put("entity1", "I am entity 1");
        references.put("entity2", "I am entity 2");
        references.put("myFoo", new Foo("Little Foo"));
    }

    public void resolveReferences(ActionInvocation invocation) throws ReferenceResolverException {
        List refs = invocation.getProxy().getConfig().getExternalRefs();

        ExternalReference reference;
        String method;

        for (Iterator iter = refs.iterator(); iter.hasNext();) {
            reference = (ExternalReference) iter.next();

            Object obj = null;

            try {
                obj = getReference(reference.getExternalRef());
            } catch (IllegalArgumentException e1) {
                if (reference.isRequired()) {
                    //if a dependacy is required but wasn't found throw an exception
                    throw new ReferenceResolverException("Could not resolve external references using key: " + reference.getExternalRef());
                } else {
                    return;
                }
            }

            try {
                Map context = Ognl.createDefaultContext(invocation.getAction());
                OgnlUtil.setProperty(reference.getName(), obj, invocation.getAction(), context);
            } catch (Exception e) {
                throw new ReferenceResolverException("Failed to set external reference: " + reference.getExternalRef() + " for bean attribute: " + reference.getName() + ". " + e.getMessage(), e);
            }
        }
    }

    private Object getReference(Object key) throws IllegalArgumentException {
        Object result = references.get(key);

        if (result == null) {
            throw new IllegalArgumentException("Object was not found for key: " + key);
        }

        return result;
    }
}
