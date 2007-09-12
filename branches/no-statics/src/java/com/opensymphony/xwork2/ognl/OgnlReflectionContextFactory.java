package com.opensymphony.xwork2.ognl;

import java.util.Map;

import ognl.Ognl;

import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;

public class OgnlReflectionContextFactory implements ReflectionContextFactory {

    public Map createDefaultContext(Object root) {
        return Ognl.createDefaultContext(root);
    }

}
