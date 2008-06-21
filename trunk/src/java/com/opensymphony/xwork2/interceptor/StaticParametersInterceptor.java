/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor populates the action with the static parameters defined in the action configuration. If the action
 * implements {@link Parameterizable}, a map of the static parameters will be also be passed directly to the action.
 *
 * <p/> Parameters are typically defined with &lt;param&gt; elements within xwork.xml.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <p/>There are no extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="staticParams"&gt;
 *          &lt;param name="parse"&gt;true&lt;/param&gt;
 *          &lt;param name="overwrite"&gt;false&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 */
public class StaticParametersInterceptor extends AbstractInterceptor {

    private boolean parse;
    private boolean overwrite;

    private static final Logger LOG = LoggerFactory.getLogger(StaticParametersInterceptor.class);

    public void setParse(String value) {
        this.parse = Boolean.valueOf(value).booleanValue();
    }

    /**
     * Overwrites already existing parameters from other sources.
     * Static parameters are the successor over previously set parameters, if true.
     *
     * @param value
     */
    public void setOverwrite(String value) {
        this.overwrite = Boolean.valueOf(value).booleanValue();
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        Object action = invocation.getAction();

        final Map<String, String> parameters = config.getParams();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting static parameters " + parameters);
        }

        // for actions marked as Parameterizable, pass the static parameters directly
        if (action instanceof Parameterizable) {
            ((Parameterizable) action).setParams(parameters);
        }

        if (parameters != null) {
            ActionContext ac = ActionContext.getContext();
            final ValueStack stack = ac.getValueStack();

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                stack.setValue(entry.getKey(), entry.getValue());
                Object val = entry.getValue();
                if (parse && val instanceof String) {
                    val = TextParseUtil.translateVariables(val.toString(), stack);
                }
                stack.setValue(entry.getKey(), val);
            }
            addParametersToContext(ac, parameters);
        }
        return invocation.invoke();
    }


    /**
     * @param ac The action context
     * @return the parameters from the action mapping in the context.  If none found, returns
     *         an empty map.
     */
    protected Map<String, String> retrieveParameters(ActionContext ac) {
        ActionConfig config = ac.getActionInvocation().getProxy().getConfig();
        if (config != null) {
            return config.getParams();
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Adds the parameters into context's ParameterMap.
     * As default, static parameters will not overwrite existing paramaters from other sources.
     * If you want the static parameters as successor over already existing parameters, set overwrite to <tt>true</tt>.
     *
     * @param ac        The action context
     * @param newParams The parameter map to apply
     */
    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
        Map<String, Object> previousParams = ac.getParameters();

        Map<String, Object> combinedParams;
        if ( overwrite ) {
            if (previousParams != null) {
                combinedParams = new TreeMap<String, Object>(previousParams);
            } else {
                combinedParams = new TreeMap<String, Object>();
            }
            if ( newParams != null) {
                combinedParams.putAll(newParams);
            }
        } else {
            if (newParams != null) {
                combinedParams = new TreeMap<String, Object>(newParams);
            } else {
                combinedParams = new TreeMap<String, Object>();
            }
            if ( previousParams != null) {
                combinedParams.putAll(previousParams);
            }
        }
        ac.setParameters(combinedParams);
    }
}
