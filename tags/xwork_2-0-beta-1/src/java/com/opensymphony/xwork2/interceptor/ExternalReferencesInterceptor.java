/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ExternalReferenceResolver;
import com.opensymphony.xwork2.config.entities.PackageConfig;


/**
 * <!-- START SNIPPET: description -->
 * TODO: Give a description of the Interceptor.
 * <!-- END SNIPPET: description -->
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * TODO: Describe the paramters for this Interceptor.
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <!-- START SNIPPET: extending -->
 * TODO: Discuss some possible extension of the Interceptor.
 * <!-- END SNIPPET: extending -->
 * <p/>
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
 * @author Ross
 *         <p/>
 *         Resolves external references using the <code>ExternalReferenceResolver</code> configured on the package
 *         Reference Resolution is encapsulated in an interceptor so that the user can configure when references should
 *         be resloved
 */
public class ExternalReferencesInterceptor extends AbstractInterceptor {

    public String intercept(ActionInvocation invocation) throws Exception {

        String packageName = invocation.getProxy().getConfig().getPackageName();
        PackageConfig packageConfig = invocation.getProxy().getConfiguration().getPackageConfig(packageName);

        if (packageConfig != null) {
            ExternalReferenceResolver erResolver = packageConfig.getExternalRefResolver();

            if (erResolver != null) {
                erResolver.resolveReferences(invocation);
            }
        }
        return invocation.invoke();
    }
}
