/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ExternalReferenceResolver;
import com.opensymphony.xwork.config.entities.PackageConfig;


/**
 * @author Ross
 *
 * Resolves external references using the <code>ExternalReferenceResolver</code> configured on the package
 * Reference Resolution is encapsulated in an interceptor so that the user can configure when references should
 * be resloved
 */
public class ExternalReferencesInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        String packageName = invocation.getProxy().getConfig().getPackageName();
        PackageConfig packageConfig = ConfigurationManager.getConfiguration().getPackageConfig(packageName);

        if (packageConfig != null) {
            ExternalReferenceResolver erResolver = packageConfig.getExternalRefResolver();

            if (erResolver != null) {
                erResolver.resolveReferences(invocation);
            }
        }
    }
}
