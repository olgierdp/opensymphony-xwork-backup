/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Unchainable;
import com.opensymphony.xwork.util.CompoundRoot;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.*;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class ChainingInterceptor extends AroundInterceptor {

    Collection excludes;
    Collection includes;
    
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

            Map ctxMap = invocation.getInvocationContext().getContextMap();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Object o = iterator.next();
                if (!(o instanceof Unchainable)) {
                    OgnlUtil.copy(o, invocation.getAction(), ctxMap,excludes, includes);
                }
            }
        }
    }
    
    
    /**
     * @return Returns the exclusions.
     */
    public Collection getExcludes() {
        return excludes;
    }
    /**
     * @param excludes The exclusions to set.
     */
    public void setExcludes(Collection excludes) {
        this.excludes = excludes;
    }
    
    /**
     * @return Returns the includes.
     */
    public Collection getIncludes() {
        return includes;
    }
    /**
     * @param includes The includes to set.
     */
    public void setIncludes(Collection includes) {
        this.includes = includes;
    }
}
