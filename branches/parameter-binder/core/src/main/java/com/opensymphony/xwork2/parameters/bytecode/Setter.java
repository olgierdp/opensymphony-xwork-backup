package com.opensymphony.xwork2.parameters.bytecode;

/**
 * Classes implementing this interface will call
 * target.set${propertyName}(param) when invoke(...) is called.
 */
public interface Setter {
    void invoke(Object target, Object param);
    String getPropertyName();
}
