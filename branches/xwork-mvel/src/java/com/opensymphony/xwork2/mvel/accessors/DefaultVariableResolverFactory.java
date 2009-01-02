package com.opensymphony.xwork2.mvel.accessors;

import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.integration.VariableResolver;
import com.opensymphony.xwork2.mvel.MVELContext;
import com.opensymphony.xwork2.util.CompoundRoot;

public class DefaultVariableResolverFactory extends BaseVariableResolverFactory {
    private MVELContext context;

    public DefaultVariableResolverFactory(MVELContext context) {
        this.appendFactory(new MapVariableResolverFactory(context));

        this.context = context;
    }

    public VariableResolver createVariable(String name, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public VariableResolver createVariable(String name, Object value, Class<?> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isTarget(String name) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isResolveable(String name) {
        return nextFactory.isResolveable(name);
    }

    public MVELContext getContext() {
        return context;
    }
}
