package com.opensymphony.xwork2.ognl;

import java.util.Map;

import com.opensymphony.xwork2.conversion.NullHandler;

public class OgnlNullHandlerWrapper implements ognl.NullHandler {

    private NullHandler wrapped;
    
    public OgnlNullHandlerWrapper(NullHandler target) {
        this.wrapped = target;
    }
    
    public Object nullMethodResult(Map context, Object target,
            String methodName, Object[] args) {
        return wrapped.nullMethodResult(context, target, methodName, args);
    }

    public Object nullPropertyValue(Map context, Object target, Object property) {
        return wrapped.nullPropertyValue(context, target, property);
    }

}
