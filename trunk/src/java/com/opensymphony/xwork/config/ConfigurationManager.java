/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.util.FileManager;

import com.opensymphony.xwork.config.impl.DefaultConfiguration;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * ConfigurationManager
 * @author Jason Carreira
 * Created Mar 1, 2003 1:06:04 AM
 */
public class ConfigurationManager {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected static final Log LOG = LogFactory.getLog(ConfigurationManager.class);
    protected static Configuration configurationInstance = null;
    private static List configurationProviders = Collections.synchronizedList(new ArrayList());

    //~ Constructors ///////////////////////////////////////////////////////////

    private ConfigurationManager() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Get the current XWork configuration object.  By default an instance of DefaultConfiguration will be returned
    * @see com.opensymphony.xwork.config.impl.DefaultConfiguration
    */
    public static Configuration getConfiguration() {
        if (configurationInstance == null) {
            configurationInstance = new DefaultConfiguration();
        } else {
            conditionalReload();
        }

        return configurationInstance;
    }

    /**
    * <p>
    * get the current list of ConfigurationProviders.
    * </p>
    * <p>
    * if no custom ConfigurationProviders have been added, this method
    * will return a list containing only the default ConfigurationProvider, XMLConfigurationProvider.  if a custom
    * ConfigurationProvider has been added, then the XmlConfigurationProvider must be added by hand.
    * </p>
    * @todo the lazy instantiation of XmlConfigurationProvider should be refactored to be elsewhere.  the behavior described above seems unintuitive.
    * @return the list of registered ConfigurationProvider objects
    * @see com.opensymphony.xwork.config.ConfigurationProvider
    */
    public static List getConfigurationProviders() {
        if (configurationProviders.size() == 0) {
            configurationProviders.add(new XmlConfigurationProvider());
        }

        return configurationProviders;
    }

    /**
    * adds a configuration provider to the List of ConfigurationProviders.  a given ConfigurationProvider may be added
    * more than once
    * @todo what does it mean to have a configuration provider added more than once?  should configurationProviders be a set instead?
    * @param provider the ConfigurationProvider to register
    */
    public static void addConfigurationProvider(ConfigurationProvider provider) {
        configurationProviders.add(provider);
    }

    /**
    * clears the registered ConfigurationProviders.  this method will call destroy() on each of the registered
    * ConfigurationProviders
    * @see com.opensymphony.xwork.config.ConfigurationProvider#destroy
    */
    public synchronized static void clearConfigurationProviders() {
        for (Iterator iterator = configurationProviders.iterator();
                iterator.hasNext();) {
            ConfigurationProvider provider = (ConfigurationProvider) iterator.next();
            provider.destroy();
        }

        configurationProviders.clear();
    }

    /**
    * reloads the Configuration files if the configuration files indicate that they need to be reloaded.
    *
    * @todo as FileManager.setReloadingConfigs never appears to be set anywhere, will this ever do anything?
    * @todo it currently appears that the reload strategy is to check on each call to getConfiguration().  this seems extremely burdensome.  a caching mechanism should be implemented
    */
    private static void conditionalReload() {
        if (FileManager.isReloadingConfigs()) {
            LOG.debug("Checking ConfigurationProviders for reload.");

            boolean reload = false;

            for (Iterator iterator = getConfigurationProviders().iterator();
                    iterator.hasNext();) {
                ConfigurationProvider provider = (ConfigurationProvider) iterator.next();

                if (provider.needsReload()) {
                    reload = true;

                    break;
                }
            }

            // @todo if a configure does need to be reloaded, it seems reasonable to limit the reload to only that which needs it
            if (reload) {
                try {
                    configurationInstance.reload();
                } catch (ConfigurationException e) {
                    LOG.error("Caught an exception while reloading the Configuration.", e);
                }
            }
        }
    }
}
