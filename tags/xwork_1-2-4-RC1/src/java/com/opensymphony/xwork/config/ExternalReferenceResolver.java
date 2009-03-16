/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ActionInvocation;


/**
 * Resolves references declared in the action configuration from an external source
 */
public interface ExternalReferenceResolver {

    public void resolveReferences(ActionInvocation invocation) throws ReferenceResolverException;
}
