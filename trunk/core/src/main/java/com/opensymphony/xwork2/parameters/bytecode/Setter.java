/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters.bytecode;

/**
 * Classes implementing this interface will call
 * target.set${propertyName}(param) when invoke(...) is called.
 */
public interface Setter {
    void invoke(Object target, Object param);
    String getPropertyName();
    Class getPropertyClass();
}
