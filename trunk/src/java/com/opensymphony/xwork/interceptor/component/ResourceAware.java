/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.interceptor.component;

import java.util.Set;

/**
 * @author plightbo
 * @deprecated WebWork's IoC has been deprecated, please use an alternative such as Spring.
 */
public interface ResourceAware {
    Set getDependentResources();
}
