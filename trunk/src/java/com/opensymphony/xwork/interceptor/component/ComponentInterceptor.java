/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: description -->
 *
 * A simple interceptor that applies the WebWork IOC container {@link ComponentManager} against the executing action. Note, WebWork IOC is deprecated and it is highly recommended that you look at alternative solutions, such as Spring.
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
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="componentStack"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author joew@thoughtworks.com
 * @author $Author$
 * @deprecated WebWork's IoC has been deprecated, please use an alternative such as Spring.
 * @version $Revision$
 */
public class ComponentInterceptor extends AroundInterceptor {
    public static final String COMPONENT_MANAGER = "com.opensymphony.xwork.interceptor.component.ComponentManager";
    private static final Log LOG = LogFactory.getLog(ComponentInterceptor.class);
    private static boolean deprecationLogged = false;

    public void init() {
    }

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation dispatcher) throws Exception {
        if (!deprecationLogged) {
            LOG.info("WebWork's IoC has been deprecated, please use an alternative such as Spring");
            deprecationLogged = true;
        }
        ComponentManager container = (ComponentManager) ActionContext.getContext().get(COMPONENT_MANAGER);

        if (container != null) {
            container.initializeObject(dispatcher.getAction());
        }
    }
}
