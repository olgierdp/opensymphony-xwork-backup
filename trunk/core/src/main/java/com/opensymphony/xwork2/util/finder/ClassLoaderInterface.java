/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.finder;

import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classes implementing this interface can find resources and load classes, usually delegating to a class
 * loader 
 */
public interface ClassLoaderInterface {

    //key used to add the current ClassLoaderInterface to ActionContext
    public final String CLASS_LOADER_INTERFACE = "__current_class_loader_interface";

    Class<?> loadClass(String name) throws ClassNotFoundException;

    URL getResource(String name);

    public Enumeration<URL> getResources(String name) throws IOException;

    public InputStream getResourceAsStream(String name) throws IOException;

    ClassLoaderInterface getParent();
}
