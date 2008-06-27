/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;

import com.opensymphony.xwork.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

import java.util.*;


/**
 * @author joew@thoughtworks.com
 * @author $Author$
 * @version $Revision$
 */
public class DefaultComponentManager implements ComponentManager, Serializable {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(DefaultComponentManager.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    Map enablers = new HashMap();
    Map enablers2 = new HashMap();
    private DefaultComponentManager fallback;
    private List loadOrder = new ArrayList();
    private Map resourceInstances = new HashMap();
    private Set alreadyLoaded = new HashSet();

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object getComponent(Class enablerType) {
        DefaultComponentManager dcm = this;

        // loop all the DCMs and get the one that holds this enabler
        Class resource = null;

        while (dcm != null) {
            resource = (Class) dcm.enablers.get(enablerType);

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
            throw new RuntimeException(message);
        }
    }

    public void setFallback(ComponentManager fallback) {
        if (fallback instanceof DefaultComponentManager) {
            this.fallback = (DefaultComponentManager) fallback;
        } else {
            throw new RuntimeException("Fallback must be an instance of DefaultConfigurationManager");
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

    private Map getResourceDependencies(Class resourceClass) {
        List interfaces = new ArrayList();
        addAllInterfaces(resourceClass, interfaces);

        Map dependencies = new HashMap();

        for (Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
            Class anInterface = (Class) iterator.next();

            DefaultComponentManager dcm = this;

            while (dcm != null) {
                Class possibleResource = (Class) dcm.enablers.get(anInterface);

                if (possibleResource != null) {
                    dependencies.put(possibleResource, dcm);

                    break;
                }

                dcm = dcm.fallback;
            }
        }

        return dependencies;
    }

    private void addAllInterfaces(Class clazz, List allInterfaces) {
        if (clazz == null) {
            return;
        }

        Class[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        addAllInterfaces(clazz.getSuperclass(), allInterfaces);
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

            dcm.resourceInstances.put(clazz, resource);
            dcm.loadOrder.add(resource);
        }

        // now return this class's enabler
        Class enabler = (Class) dcm.enablers2.get(clazz);

        return enabler;
    }

    private ResourceEnablerPair setupAndOptionallyCreateResource(DefaultComponentManager newDcm, Class depResource) throws Exception {
        ResourceEnablerPair pair = new ResourceEnablerPair();
        Object newResource = newDcm.resourceInstances.get(depResource);

        if (newResource == null) {
            newResource = ObjectFactory.getObjectFactory().buildBean(depResource);
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
            enabler.getMethods()[0].invoke(resource, new Object[] {newResource});
        } catch (Exception e) {
            e.printStackTrace();

            if (log.isDebugEnabled()) {
                log.debug("Error invoking method for resource: " + resource.getClass().getName(), e);
            }
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    class ResourceEnablerPair {
        Class enabler;
        Object resource;
    }
}
