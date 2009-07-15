/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * class taken from Apache JCI
 */
public final class ResourceStoreClassLoader extends ClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceStoreClassLoader.class);

    private final ResourceStore[] stores;

    public ResourceStoreClassLoader(final ClassLoader pParent, final ResourceStore[] pStores) {
        super(pParent);

        stores = new ResourceStore[pStores.length];
        System.arraycopy(pStores, 0, stores, 0, stores.length);
    }

    private Class fastFindClass(final String name) {

        if (stores != null) {
            String fileName = name.replace('.', '/') + ".class";
            for (final ResourceStore store : stores) {
                final byte[] clazzBytes = store.read(fileName);
                if (clazzBytes != null) {
                    return defineClass(name, clazzBytes, 0, clazzBytes.length);
                }
            }
        }

        return null;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);

        if (clazz == null) {
            clazz = fastFindClass(name);

            if (clazz == null) {
                final ClassLoader parent = getParent();
                if (parent != null) {
                    clazz = parent.loadClass(name);
                } else {
                    throw new ClassNotFoundException(name);
                }

            }
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    protected Class findClass(final String name) throws ClassNotFoundException {
        final Class clazz = fastFindClass(name);
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }
}
