/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.finder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Default implementation of ClassLoaderInterface, which delegates to an actual ClassLoader
 */
public class ClassLoaderInterfaceDelegate implements ClassLoaderInterface {
    private ClassLoader classLoader;

    public ClassLoaderInterfaceDelegate(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    public URL getResource(String className) {
        return classLoader.getResource(className);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        return classLoader.getResources(name);
    }

    public InputStream getResourceAsStream(String name) {
        return classLoader.getResourceAsStream(name);
    }

    public ClassLoaderInterface getParent() {
        return classLoader.getParent() != null ? new ClassLoaderInterfaceDelegate(classLoader.getParent()) : null;
    }
}
