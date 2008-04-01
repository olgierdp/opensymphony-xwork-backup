/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;


/**
 * Configuration for InterceptorStack.
 * <p/>
 * In the xml configuration file this is defined as the <code>interceptor-stack</code> tag.
 *
 * @author Mike
 * @author Rainer Hermanns
 */
public class InterceptorStackConfig extends Located implements Serializable {

    private List<InterceptorMapping> interceptors;
    private String name;


    protected InterceptorStackConfig() {
        this.interceptors = new ArrayList<InterceptorMapping>();
    }

    protected InterceptorStackConfig(InterceptorStackConfig orig) {
        this.name = orig.name;
        this.interceptors = new ArrayList<InterceptorMapping>(orig.interceptors);
    }


    public Collection<InterceptorMapping> getInterceptors() {
        return interceptors;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }

        final InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig) o;

        if ((interceptors != null) ? (!interceptors.equals(interceptorStackConfig.interceptors)) : (interceptorStackConfig.interceptors != null))
        {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorStackConfig.name)) : (interceptorStackConfig.name != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((interceptors != null) ? interceptors.hashCode() : 0);

        return result;
    }

    /**
     * The builder for this object.  An instance of this object is the only way to construct a new instance.  The
     * purpose is to enforce the immutability of the object.  The methods are structured in a way to support chaining.
     * After setting any values you need, call the {@link #build()} method to create the object.
     */
    public static class Builder implements InterceptorListHolder {
        private InterceptorStackConfig target;

        public Builder(String name) {
            target = new InterceptorStackConfig();
            target.name = name;
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        public Builder addInterceptor(InterceptorMapping interceptor) {
            target.interceptors.add(interceptor);
            return this;
        }

        public Builder addInterceptors(List<InterceptorMapping> interceptors) {
            target.interceptors.addAll(interceptors);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public InterceptorStackConfig build() {
            target.interceptors = Collections.unmodifiableList(target.interceptors);
            InterceptorStackConfig result = target;
            target = new InterceptorStackConfig(target);
            return result;
        }
    }
}
