package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.LocationUtils;

import java.util.LinkedHashMap;

/**
 * Attaches location information to the factory.  Construction limited to no-arg
 * constructors.
 */
public class LocatableFactory<T> extends Located implements Factory<T> {

    volatile ContainerImpl.ConstructorInjector<? extends T> constructor;
    private Class implementation;
    private Class type;
    private String name;
    private Scope scope;

    public LocatableFactory(String name, Class type, Class implementation, Scope scope, Object location) {
        this.implementation = implementation;
        this.type = type;
        this.name = name;
        this.scope = scope;
        setLocation(LocationUtils.getLocation(location));
    }

    @SuppressWarnings("unchecked")
    public T create(Context context) {
        if (constructor == null) {
            this.constructor =
                    ((ContainerImpl)context.getContainer()).getConstructor(implementation);
        }
        InternalContext ctx = new InternalContext((ContainerImpl) context.getContainer());
        ctx.setExternalContext((ExternalContext)context);
        return (T) constructor.construct(ctx, type);
    }

    public String toString() {
        String fields = new LinkedHashMap<String, Object>() {
            {
                put("type", type);
                put("name", name);
                put("implementation", implementation);
                put("scope", scope);
            }
        }.toString();
        StringBuilder sb = new StringBuilder(fields);
        sb.append(super.toString());
        sb.append(" defined at ");
        sb.append(getLocation().toString());
        return sb.toString();
    }
}