/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.ognl;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import ognl.DefaultMemberAccess;

/**
 * Allows access decisions to be made on the basis of whether a member is static or not
 */
public class StaticMemberAccess extends DefaultMemberAccess {

    private boolean allowStaticMethodAccess;
    
    public StaticMemberAccess(boolean method) {
        super(false);
        allowStaticMethodAccess = method;
    }

    public boolean getAllowStaticMethodAccess() {
        return allowStaticMethodAccess;
    }

    public void setAllowStaticMethodAccess(boolean allowStaticMethodAccess) {
        this.allowStaticMethodAccess = allowStaticMethodAccess;
    }

    @Override
    public boolean isAccessible(Map context, Object target, Member member,
            String propertyName) {
        
        boolean allow = true;
        int modifiers = member.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            if (member instanceof Method && !getAllowStaticMethodAccess()) {
                allow = false;
            }
        }
        
        // Now check for standard scope rules
        if (allow) {
            return super.isAccessible(context, target, member, propertyName);
        }
        
        return false;
    }
    
    
    
    

}
