/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.XworkException;
import com.opensymphony.xwork.util.CompoundRoot;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class ChainingInterceptor extends AroundInterceptor {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String ACTION_CONTEXT_CHAIN_DEPTH = "com.opensymphony.xwork.interceptor.ChainingInterceptor.CHAIN_DEPTH";

    //~ Instance fields ////////////////////////////////////////////////////////

    // to prevent infinite recursion, only allow X chains within the same request
    private int maxChainDepth = 100;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMaxChainDepth(int maxChainDepth) {
        this.maxChainDepth = maxChainDepth;
    }

    protected void after(ActionInvocation invocation, String result) throws Exception {
        incrementChainDepth();
    }

    protected void before(ActionInvocation invocation) throws Exception {
        int currentDepth = currentChainDepth();

        if (currentDepth > (maxChainDepth + 1)) {
            throw new XworkException("Chain infinite recursion detected, maxChainDepth=" + maxChainDepth);
        }

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

    private int currentChainDepth() {
        Integer chainDepth = (Integer) ActionContext.getContext().get(ACTION_CONTEXT_CHAIN_DEPTH);

        if (chainDepth == null) {
            return 0;
        } else {
            return chainDepth.intValue();
        }
    }

    private void incrementChainDepth() {
        ActionContext.getContext().put(ACTION_CONTEXT_CHAIN_DEPTH, new Integer(currentChainDepth() + 1));
    }
}
