/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.Parameterizable;
import com.opensymphony.xwork.util.OgnlUtil;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class StaticParametersInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation invocation, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        Action action = invocation.getAction();

        if (log.isDebugEnabled()) {
            log.debug("Setting static params " + config.getParams());
        }

        // for actions marked as Parameterizable, pass the static params directly
        if (action instanceof Parameterizable) {
            ((Parameterizable) action).setParams(config.getParams());
        }

        // populate model bean's fields if action is ModelDriven, otherwise populate action's fields
        if (action instanceof ModelDriven) {
            OgnlUtil.setProperties(config.getParams(), ((ModelDriven) action).getModel(), ActionContext.getContext().getContextMap());
        } else {
            OgnlUtil.setProperties(config.getParams(), action, ActionContext.getContext().getContextMap());
        }
    }
}
