/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.Parameterizable;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.Iterator;
import java.util.Map;


/**
 * Populates the Action with the static parameters defined in the Action
 * configuration by treating the Action as a bean.  If the  Action is 
 * {@link Parameterizable}, a map of the static parameters will be also be
 * passed directly to the Action.
 * <p>
 * Parameters are defined with &lt;param&gt; elements within the Action
 * configuration.
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

        final Map parameters = config.getParams();

        if (log.isDebugEnabled()) {
            log.debug("Setting static parameters " + parameters);
        }

        // for actions marked as Parameterizable, pass the static parameters directly
        if (action instanceof Parameterizable) {
            ((Parameterizable) action).setParams(parameters);
        }

        if (parameters != null) {
            final OgnlValueStack stack = ActionContext.getContext().getValueStack();

            for (Iterator iterator = parameters.entrySet().iterator();
                    iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                stack.setValue(entry.getKey().toString(), entry.getValue());
            }
        }
    }
}
