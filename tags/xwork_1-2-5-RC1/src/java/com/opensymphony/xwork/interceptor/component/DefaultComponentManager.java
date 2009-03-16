/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.XworkException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.*;


/**
 * @author joew@thoughtworks.com
 * @author $Author$
 * @version $Revision$
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-WebWork integration documentation for more info.
 */
public class DefaultComponentManager implements ComponentManager, Serializable {

    private static final Log log = LogFactory.getLog(DefaultComponentManager.class);


    Map enablers = new HashMap();
    Map enablers2 = new HashMap();
    private DefaultComponentManager fallback;
    private List loadOrder = new ArrayList();
    private Map resourceInstances = new HashMap();
    private Map dependentResources = new HashMap();
    private Set alreadyLoaded = new HashSet();
    private ComponentConfiguration config;
    private String scope;


    public Object getComponent(Class enablerType) {
        DefaultComponentManager dcm = this;

        // loop all the DCMs and get the one that holds this enabler
        Class resource = null;

        while (dcm != null) {
            resource = getPossibleResource(dcm, enablerType, true);

            if (resource != null) {
                break;
            }

            dcm = dcm.fallback;
        }

        if (resource == null) {
            // this is an unknown resource, return null;
            return null;
        }

        // now that we have the DCM and the resource class, we can set it up
        try {
            ResourceEnablerPair pair = setupAndOptionallyCreateResource(dcm, resource);

            return pair.resource;
        } catch (Exception e) {
            String message = "Could not load resource with enabler " + enablerType;
            log.error(message, e);
            throw new XworkException(message);
        }
    }

    public void setFallback(ComponentManager fallback) {
        if (fallback instanceof DefaultComponentManager) {
            this.fallback = (DefaultComponentManager) fallback;
            this.config = fallback.getConfig();
        } else {
            throw new XworkException("Fallback must be an instance of DefaultConfigurationManager");
        }
    }

    public void addEnabler(Class component, Class enablerType) {
        enablers.put(enablerType, component);
        enablers2.put(component, enablerType);
    }

    public void dispose() {
        Collections.reverse(loadOrder);

        for (Iterator iterator = loadOrder.iterator(); iterator.hasNext();) {
            Object resource = iterator.next();

            if (resource instanceof Disposable) {
                Disposable disposable = (Disposable) resource;
                disposable.dispose();
            }
        }
    }

    public void initializeObject(Object obj) {
        loadResource(obj, obj.getClass(), this);

        // is this even needed now?
        //        if (fallback != null) {
        //            fallback.initializeObject(obj);
        //        }
    }

    public void registerInstance(Class componentType, Object instance) {
        if (!componentType.isInstance(instance)) {
            throw new IllegalArgumentException("The object " + instance + " is not an instance of " + componentType.getName());
        }

        loadResource(instance, componentType, this);
    }

    public Object getComponentInstance(Class componentType) {
        DefaultComponentManager dcm = this;

        // loop all the DCMs and get the one that holds this enabler
        Class enablerType = null;

        while (dcm != null) {
            enablerType = (Class) dcm.enablers2.get(componentType);

            if (enablerType != null) {
                break;
            }

            dcm = dcm.fallback;
        }

        if (enablerType == null) {
            // this is an unknown component type, return null
            return null;
        }

        try {
            ResourceEnablerPair pair = setupAndOptionallyCreateResource(dcm, componentType);

            return pair.resource;
        } catch (Exception e) {
            String message = "Could not load resource of type " + componentType;
            log.error(message, e);
            throw new XworkException(message);
        }
    }

    public ComponentConfiguration getConfig() {
        return config;
    }

    public void setConfig(ComponentConfiguration config) {
        this.config = config;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void reset() {
        enablers.clear();
        enablers2.clear();
        loadOrder.clear();
        resourceInstances.clear();
        alreadyLoaded.clear();
        dependentResources.clear();
    }

    private Map getResourceDependencies(Class resourceClass) {
        List interfaces = new ArrayList();
        addAllInterfaces(resourceClass, interfaces);

        Map dependencies = new HashMap();

        for (Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
            Class anInterface = (Class) iterator.next();

            DefaultComponentManager dcm = this;
            while (dcm != null) {
                dcm.resolveDependencies();
                Class possibleResource = getPossibleResource(dcm, anInterface, true);

                if (possibleResource != null) {
                    dependencies.put(possibleResource, dcm);

                    break;
                }

                dcm = dcm.fallback;
            }
        }

        return dependencies;
    }

    private Class getPossibleResource(DefaultComponentManager dcm, Class anInterface, boolean retry) {
        for (Iterator iterator = dcm.enablers.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Class key = (Class) entry.getKey();
            if (anInterface.equals(key)) {
                return (Class) entry.getValue();
            } else if (retry && anInterface.getName().equals(key.getName())) {
                // looks like we should reload!
                log.warn("Re-configuration IoC for scope " + dcm.scope);
                dcm.dispose();
                config.configure(dcm, dcm.scope);
                return getPossibleResource(dcm, anInterface, false);
            }
        }

        return null;
    }

    private void addAllInterfaces(Class clazz, List allInterfaces) {
        if (clazz == null) {
            return;
        }

        Class[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        addAllInterfaces(interfaces, allInterfaces);
        addAllInterfaces(clazz.getSuperclass(), allInterfaces);
    }

    private void addAllInterfaces(Class[] clazzes, List allInterfaces) {
        if (clazzes != null) {
            for (int i = 0; i < clazzes.length; i++) {
                addAllInterfaces(clazzes[i], allInterfaces);
            }
        }
    }

    private Class loadResource(Object resource, Class clazz, DefaultComponentManager dcm) {
        boolean resourceNotLoaded = !dcm.loadOrder.contains(resource);

        if (resourceNotLoaded) {
            Map resources = getResourceDependencies(clazz);

            for (Iterator iterator = resources.entrySet().iterator();
                 iterator.hasNext();) {
                Map.Entry mapEntry = (Map.Entry) iterator.next();
                Class depResource = (Class) mapEntry.getKey();
                DefaultComponentManager newDcm = (DefaultComponentManager) mapEntry.getValue();

                try {
                    ResourceEnablerPair pair = setupAndOptionallyCreateResource(newDcm, depResource);
                    setupResource(resource, pair.enabler, pair.resource);
                } catch (Exception e) {
                    e.printStackTrace();

                    if (log.isDebugEnabled()) {
                        log.debug("Error loading or setting up resource: " + resources.getClass().getName(), e);
                    }
                }
            }

            dcm.alreadyLoaded.add(clazz);

            if (resource instanceof Initializable) {
                Initializable initializable = (Initializable) resource;
                initializable.init();
            }

            if (resource instanceof ResourceAware) {
                ResourceAware ra = (ResourceAware) resource;
                Set dr = ra.getDependentResources();
                HashMap times = new HashMap();
                for (Iterator iterator = dr.iterator(); iterator.hasNext();) {
                    String name = (String) iterator.next();
                    times.put(name, new Long(getLastModified(name)));
                }
                dcm.dependentResources.put(ra, times);
            }

            dcm.resourceInstances.put(clazz, resource);
            dcm.loadOrder.add(resource);
        }

        // now return this class's enabler
        Class enabler = (Class) dcm.enablers2.get(clazz);

        return enabler;
    }

    private long getLastModified(String name) {
        try {
            URL url = ClassLoaderUtil.getResource(name, getClass());
            File file = new File(new URI(url.toExternalForm()));
            return file.lastModified();
        } catch (Exception e) {
            return 0;
        }
    }

    private ResourceEnablerPair setupAndOptionallyCreateResource(DefaultComponentManager newDcm, Class depResource) throws Exception {
        ResourceEnablerPair pair = new ResourceEnablerPair();
        Object newResource = newDcm.resourceInstances.get(depResource);

        if (newResource == null) {
            newResource = ObjectFactory.getObjectFactory().buildBean(depResource, null);
        }

        pair.resource = newResource;

        Class enabler = loadResource(newResource, depResource, newDcm);
        pair.enabler = enabler;

        return pair;
    }

    private void setupResource(Object resource, Class enabler, Object newResource) {
        if (enabler == null) {
            return;
        }

        try {
            enabler.getMethods()[0].invoke(resource, new Object[]{newResource});
        } catch (Exception e) {
            e.printStackTrace();

            if (log.isDebugEnabled()) {
                log.debug("Error invoking method for resource: " + resource.getClass().getName(), e);
            }
        }
    }

    private void resolveDependencies() {
        for (Iterator iterator = dependentResources.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ResourceAware ra = (ResourceAware) entry.getKey();
            Map times = (Map) entry.getValue();

            Set dr = ra.getDependentResources();
            for (Iterator iterator1 = dr.iterator(); iterator1.hasNext();) {
                String name = (String) iterator1.next();
                long lastTime = ((Long) times.get(name)).longValue();
                long lastMod = getLastModified(name);
                if (lastTime < lastMod) {
                    // reload this DCM
                    dispose();
                    config.configure(DefaultComponentManager.this, scope);
                    break;
                }
            }
        }
    }

    class ResourceEnablerPair {
        Class enabler;
        Object resource;
    }
}
