/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.util.OgnlUtil;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class ParametersInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Action action = invocation.getAction();

        if (log.isDebugEnabled()) {
            log.debug("Setting params " + ActionContext.getContext().getParameters());
        }

        // populate model bean's fields if action is ModelDriven, otherwise populate action's fields
        if (action instanceof ModelDriven) {
            OgnlUtil.setProperties(ActionContext.getContext().getParameters(), ((ModelDriven) action).getModel(), ActionContext.getContext().getContextMap());
        } else {
            OgnlUtil.setProperties(ActionContext.getContext().getParameters(), action, ActionContext.getContext().getContextMap());
        }
    }
}
