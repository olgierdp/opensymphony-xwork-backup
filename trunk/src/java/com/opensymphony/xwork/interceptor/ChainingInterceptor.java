/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.CompoundRoot;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class ChainingInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation invocation, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        OgnlValueStack stack = invocation.getStack();
        CompoundRoot root = stack.getRoot();
        if (root.size() > 1) {
            List list = new ArrayList(root);
            list.remove(0);
            Collections.reverse(list);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Object o = iterator.next();
                OgnlUtil.copy(o, invocation.getAction(), invocation.getInvocationContext().getContextMap());
            }
        }
    }
}
