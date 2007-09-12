package com.opensymphony.xwork2.util.reflection;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;

public class ReflectionProviderFactory {

    public static ReflectionProvider getInstance() {
        return ActionContext.getContext().getContainer().getInstance(ReflectionProvider.class);
    }
}
