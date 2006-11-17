package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.LocationUtils;

/**
 * Attaches location information to the factory.  Construction limited to no-arg 
 * constructors.  
 */
public class LocatableFactory extends Located implements Factory {

    private Class cls;
    public LocatableFactory(Class cls, Object location) {
        this.cls = cls;
        setLocation(LocationUtils.getLocation(location));
    }
    
    public Object create(Context context) throws Exception {
        Object obj = cls.newInstance();
        context.getContainer().inject(obj);
        return obj;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" defined at ");
        sb.append(getLocation().toString());
        return sb.toString();
    }
}