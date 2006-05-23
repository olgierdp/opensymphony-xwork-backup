package com.opensymphony.xwork;

import com.opensymphony.xwork.config.ConfigurationManager;

/**
 * XWorkStatic
 * <p/>
 * Created : May 20, 2006 3:25:15 PM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class XWorkStatic {
    private static ConfigurationManager configurationManager = new ConfigurationManager();

    static {
        configurationManager.destroyConfiguration();
    }

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public static void setConfigurationManager(ConfigurationManager configurationManager) {
        XWorkStatic.configurationManager = configurationManager;
    }
}
