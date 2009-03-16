/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Scans for resource that resides within classpath and jar file.
 * <p/>
 * Typical usage would be :-
 * <p/>
 * To scan for all resources (apple.txt) that lies in the root of a classpath or in the
 * root of the jar file.
 * <pre>
 * <code>
 *      // to scan for a resource (apple.txt) that lies in the root of a classpath or in
 *      // the root of the jar file.
 *      ResourceScanner resourceScanner = new ResourceScanner(
 *          new String[] { "" },  // an empty string indicate that the scanning should start at the 'root'
 *          MyMain.class
 *      );
 *      List<URL> ourResources = resourceScanner.scanForResources(
 *           new ResourceScanner.Filter() {
 *                 public boolean accept(URL resource) {
 *                     if (resource.getFile().endsWith("apple.txt")) {
 *                         return true;
 *                     }
 *                     return false;
 *                 }
 *           });
 *
 * </code>
 * </pre>
 * To scan for all resources that lies under the '/com/opensymphony/xwork/util' directory in
 * the classpath, we could do
 * <pre>
 * <code>
 *      List<URL> ourResources = new ResourceScanner(
 *              new String[] { "com/opensymphony/xwork/util/" }, // Note that it DOES NOT start with a '/' and MUST ends with '/'
 *              MyMain.class).scanForResources();
 * </code>
 * </pre>
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ResourceScanner {

    private static final Log LOG = LogFactory.getLog(ResourceScanner.class);

    private String[] roots;
    private Class callingClass;

    /**
     * Create an instance of {@link com.opensymphony.xwork.util.ResourceScanner}, taking
     * in arguments
     * <ul>
     *  <li><code>root</code> - An array of String, defining the root we should start scanning
     *                          for resources.</li>
     *  <li><code>callingClass</code> - The class invoking methods on
     *                                  {@link com.opensymphony.xwork.util.ResourceScanner}</li>
     * </ul>
     * @param roots An array of String, defining the root we should start scanning for resources.
     * @param callingClass The class invoking methods on {@link com.opensymphony.xwork.util.ResourceScanner}
     */
    public ResourceScanner(String[] roots, Class callingClass) {
        this.roots = roots;
        this.callingClass = callingClass;
    }

    /**
     * Start scanning for the resources, assuming that all the resources under <code>roots</code>
     * specified in the constructor is what we need.
     *
     * @return {@link java.util.List} of {@link java.net.URL}.
     * @throws IOException
     * @throws URISyntaxException
     */
    public List scanForResources() throws IOException, URISyntaxException {
        return scanForResources(
            new Filter() {
                public boolean accept(URL resource) {
                    return true;
                }
            });
    }

    /**
     * Start scanning for the resources under <code>roots</code> as specified in the constructor
     * applying <code>filter</code>({@link com.opensymphony.xwork.util.ResourceScanner.Filter})
     * to the resources that we've found.
     *
     * @param filter
     * @return {@link java.util.List} of {@link java.net.URL}
     * @throws IOException
     * @throws URISyntaxException
     */
    public List scanForResources(Filter filter) throws IOException, URISyntaxException {
        List resources = new ArrayList();
        for (int a=0; a< roots.length; a++) {
            String root = roots[a].replace('.', '/');

            /*
             * Special case if root is '', we need to scan all the root directory of
             * the jar files as well. We can't do thate with
             *    classLoader.getResources("");
             * it doesn't work, we'll need to do a bit of hack to get around this.
             * This only applies when the resource might be located in a jar file. For
             * non-jar file resources
             *    classLoader.getResources("");
             * works fine.
             */
            if ("".equals(root)) {
                URL[] rootResources = getResources("META-INF", ResourceScanner.class);
                for (int c=0; c<rootResources.length; c++) {
                    if (rootResources[c].toString().startsWith("jar:file:") &&
                       (rootResources[c].toString().indexOf("!") > 0)) {
                        //System.out.println(rootResources[c]);
                        String jarFilePath = rootResources[c].toString().substring(9,
                                rootResources[c].toString().indexOf("!"));
                        resources.addAll(loadRootResourcesFromJarFile(
                            rootResources[c].toString().substring(0, rootResources[c].toString().indexOf("!")),
                            new File(URLDecoder.decode(jarFilePath, "UTF-8")), filter, null));
                    }
                }
            }

            /*
             * Normal case, we just let
             *    classLoader.getResources(...)
             * helps us get the resources. This works for resources lying in jar and non-jar, except
             * for resources in jar file that should be in the root eg. validators.xml that lies in the
             * root of a jar file. In this case, we deal with it in the above code (special case).
             */
            URL[] rootResources = getResources(root, ResourceScanner.class);
            for (int b=0; b< rootResources.length; b++) {
                if (rootResources[b].toString().startsWith("file:")) {
                    //System.out.println(rootResources[b]);
                    resources.addAll(loadResourcesFromClassPath(new File(rootResources[b].toURI()), filter, null));
                }
                else if (rootResources[b].toString().startsWith("jar:file:") &&
                        (rootResources[b].toString().indexOf("!") > 0)) {
                    //System.out.println(rootResources[b]);
                    String jarFilePath = rootResources[b].toString().substring(
                            9, rootResources[b].toString().indexOf("!"));

                    resources.addAll(loadResourcesFromJarFile(
                            rootResources[b].toString().substring(0, rootResources[b].toString().indexOf("!")),
                            root, new File(URLDecoder.decode(jarFilePath, "UTF-8")), filter, null));
                }
            }
        }
        return resources;
    }

    /**
     * Find resources (a {@link java.util.List} of {@link java.net.URL}) from a jar file that lies in the
     * root of the jar file, applying {@link com.opensymphony.xwork.util.ResourceScanner.Filter} on each
     * resources found. 
     *
     * @param jarFilePath
     * @param jarFile
     * @param filter
     * @param resources
     * @return {@link java.util.List} of {@link java.net.URL}.
     * @throws IOException
     */
    protected List loadRootResourcesFromJarFile(String jarFilePath, File jarFile, Filter filter, List resources) throws IOException {
        if (resources == null) {
            resources = new ArrayList();
        }
        JarFile _jarFile = null;
        try {
            _jarFile = new JarFile(jarFile);
            Enumeration jarFileEntries = _jarFile.entries();
            while(jarFileEntries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) jarFileEntries.nextElement();
                String fullResourcePath = jarEntry.getName();
                if (fullResourcePath.indexOf("/") < 0) {
                    //System.out.println(jarEntry.getName());
                    //System.out.println(jarFilePath+"!"+fullResourcePath);
                    //URL _resource = ClassLoaderUtil.getResource(fullResourcePath, ResourceScanner.class);
                    URL _resource = new URL(jarFilePath+"!/"+fullResourcePath);
                    if (_resource != null) {
                        if (filter.accept(_resource)) {
                            resources.add(_resource);
                        }
                    }
                }
            }
        }
        finally {
            if (jarFile != null) {
                _jarFile.close();
            }
        }
        return resources;
    }

    public List loadResourcesFromJarFile(String jarFilePath, String root, File jarFile, Filter filter, List resources) throws IOException {
        if (resources == null) {
            resources = new ArrayList();
        }
        JarFile _jarFile = null;
        try {
            _jarFile = new JarFile(jarFile);
            Enumeration jarFileEntries = _jarFile.entries();
            while(jarFileEntries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) jarFileEntries.nextElement();
                String fullResourcePath = jarEntry.getName();
                //String fullResourcePath = root + (root.endsWith("/")?(jarEntry.getName()):("/"+jarEntry.getName()));
                //System.out.println("** jarEntry.getName()="+jarEntry.getName());
                //System.out.println("** fullResourePath="+fullResourcePath);
                if (fullResourcePath.startsWith(root) &&
                        (fullResourcePath.substring(root.length()).indexOf("/") < 0)) {
                    // see if this resource really exists
                    //URL _resource = ClassLoaderUtil.getResource(fullResourcePath, ResourceScanner.class);
                    URL _resource = new URL(jarFilePath+"!/"+fullResourcePath);

                    if (_resource != null) {
                        if(filter.accept(_resource)) {
                            resources.add(_resource);
                        }
                    }
                }
            }
        }
        finally {
            if (jarFile != null) {
                _jarFile.close();
            }
        }
        return resources;
    }


    public List loadResourcesFromClassPath(File rootFile, Filter filter, List resources) throws URISyntaxException,
            MalformedURLException {
        if (resources == null) {
            resources = new ArrayList();
        }
        if (rootFile.isDirectory()) {
            File[] children = rootFile.listFiles();
            for (int a=0; a<children.length; a++) {
                //System.out.println("** "+children[a]);
                if (children[a].isFile()) {
                    if (filter.accept(children[a].toURL())) {
                        resources.add(children[a].toURL());
                    }
                }
            }
        }
        return resources;
    }


    /**
     * Returns an array of {@link java.net.URL} corresponding to <code>resourceName</code>.
     * Search for {@link java.net.URL}s using {@link ClassLoader} in the following precedence :-
     * <ul>
     *      <li>ThreadLocal's context class loader</li>
     *      <li>ValidatorFactory's class loader</li>
     *      <li><code>callingClass</code>'s class loader</li>
     * </ul>
     *
     * @param resourceName
     * @param callingClass
     * @return URL[]
     * @throws java.io.IOException
     */
     protected URL[] getResources(String resourceName, Class callingClass) throws IOException {
        // use ThreadLocal's class loader
        URL[] urls =  (URL[]) Collections.list(
                Thread.currentThread().getContextClassLoader().getResources(resourceName)
        ).toArray(new URL[0]);

        // use ClassLoaderUtil's classloader
        if (urls.length == 0) {
            urls = (URL[]) Collections.list(
                   ResourceScanner.class.getClassLoader().getResources(resourceName)
            ).toArray(new URL[0]);
        }

        // use callingClass's classloader
        if (urls.length == 0) {
            urls = (URL[]) Collections.list(
                    callingClass.getClassLoader().getResources(resourceName)
            ).toArray(new URL[0]);
        }

        return urls;
     }


    /**
     * @author tmjee
     * @version $Date$ $Id$
     */
    public static interface Filter {
        boolean accept(URL resource);
    }
}
