package com.opensymphony.xwork2.parameters.bytecode;

/**
 * Classes implementing this interface will call
 * target.get${propertyName} when invoke(...) is called.
 */
public interface Getter {
    Object invoke(Object target);
    String getPropertyName();    
}
