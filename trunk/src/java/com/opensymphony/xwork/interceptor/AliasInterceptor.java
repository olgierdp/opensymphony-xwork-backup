package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 * TODO: Give a description of the Interceptor.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * TODO: Describe the paramters for this Interceptor.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * TODO: Discuss some possible extension of the Interceptor.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- TODO: Describe how the Interceptor reference will effect execution --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *      TODO: fill in the interceptor reference.
 *     &lt;interceptor-ref name=""/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * The aim of this Interceptor is to covert different named parameters.
 * Act as the glue between actions sharing similiar parameter intents, but different naming
 * It can get helpful when chaining but could also convert request parameters
 * <p/>
 * action's aliases expression should be in the form of  #{ "name1" : "alias1", "name2" : "alias2" }
 * e.g. assuming action1(or something else in the stack) has a getter named "name1" and the action this interceptor
 * is applied to has a setter named "alias1", "alias1" will be set with the value from "name1"
 *
 * @author Matthew Payne
 */

public class AliasInterceptor extends AroundInterceptor {
    private static final Log log = LogFactory.getLog(AliasInterceptor.class);

    private static final String DEFAULT_ALIAS_KEY = "aliases";

    String aliasesKey = DEFAULT_ALIAS_KEY;


    public void setAliasesKey(String aliasesKey) {
        this.aliasesKey = aliasesKey;
    }

    public void destroy() {
    }

    public void init() {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        
        // get the action's parameters
        final Map parameters = config.getParams();

        if (parameters.containsKey(aliasesKey)) {

            String aliasExpression = (String) parameters.get(aliasesKey);
            OgnlValueStack stack = ActionContext.getContext().getValueStack();
            Object obj = stack.findValue(aliasExpression);

            if (obj != null && obj instanceof Map) {
                // override
                Map aliases = (Map) obj;
                Iterator itr = aliases.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry entry = (Map.Entry) itr.next();
                    String name = entry.getKey().toString();
                    String alias = (String) entry.getValue();
                    Object value = stack.findValue(name);
                    stack.setValue(alias, value);

                }

            } else {
                log.debug("invalid alias expression:" + aliasesKey);
            }


        }

    }

    protected void after(ActionInvocation invocation, String result) throws Exception {
    }
}
