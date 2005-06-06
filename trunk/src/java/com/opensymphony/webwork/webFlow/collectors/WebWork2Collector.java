/*
 * Created on Aug 12, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.collectors;

import com.opensymphony.webwork.webFlow.JarFileFilter;
import com.opensymphony.webwork.webFlow.entities.XWorkCommand;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Gets Flows for WebWork2 applications
 */
public class WebWork2Collector implements Collector {
    private static final Log LOG = LogFactory.getLog(WebWork2Collector.class);
    private String basePath = "";
    private URLClassLoader classLoader;
    private Configuration config = null;

    public Map getPackages() {
        Map packages = new HashMap();
        if (config != null) {
            Set packageNames = config.getPackageConfigNames();
            for (Iterator iter = packageNames.iterator(); iter.hasNext();) {
                Map pkgCommands = new HashMap();
                String pkgName = (String) iter.next();
                PackageConfig pkgConfig = config.getPackageConfig(pkgName);
                Map resultConfigs = pkgConfig.getGlobalResultConfigs();
                if (LOG.isDebugEnabled())
                    LOG.debug(pkgName + "=" + pkgConfig);
                Set resultNames = resultConfigs.keySet();
                for (Iterator iterator = resultNames.iterator(); iterator.hasNext();) {
                    String resultName = (String) iterator.next();
                    ResultConfig resultConfig = (ResultConfig) resultConfigs.get(resultName);
                    if (LOG.isDebugEnabled())
                        LOG.debug(" - result " + resultName + "=" + resultConfig.getParams());
                }

                Map actionConfigMap = pkgConfig.getAllActionConfigs();
                Set actionNames = actionConfigMap.keySet();
                for (Iterator iterator = actionNames.iterator(); iterator.hasNext();) {
                    String actionName = (String) iterator.next();
                    ActionConfig actionConfig = (ActionConfig) actionConfigMap.get(actionName);
                    XWorkCommand command = new XWorkCommand(actionConfig, this.basePath);
                    command.setName(actionName);
                    command.setNamespace(pkgConfig.getNamespace());
                    pkgCommands.put(actionName, command);
                    //if (LOG.isDebugEnabled())
                    //	LOG.debug( "  - "+command );
                }	//end each command in package
                packages.put(pkgName, pkgCommands);
            }	//end each package
        }	//end if config != null
        return packages;
    }

    public void setBasePath(String basePath) throws IOException {
        this.basePath = basePath;
        String configFilePath = this.basePath + "WEB-INF/classes/xwork.xml";
        File configFile = new File(configFilePath);
        if (LOG.isDebugEnabled())
            LOG.debug("can read config file? " + configFile.canRead());
        //initClassLoader( configFile.getParentFile().getCanonicalPath() + "/" );
        ConfigurationProvider configProvider = new ArbitraryXMLConfigurationProvider(configFile
                .getCanonicalPath());
        ConfigurationManager.addConfigurationProvider(configProvider);
        this.config = ConfigurationManager.getConfiguration();
    }

    public void initClassLoader(String basePathString) {
        LOG.debug("basePathString=" + basePathString);
        URL[] classPathURLs = new URL[100];
        try {
            URL classPathURL = new URL("file://" + basePathString);
            classPathURLs[0] = classPathURL;
            //TODO Find a way of dynamically discovering jars, or have them enter them. As a plugin, this will be easier
            String libPath = this.basePath + "WEB-INF/lib/";
            File libDir = new File(libPath);
            if (LOG.isDebugEnabled())
                LOG.debug("libDir=" + libDir);
            if (libDir != null && libDir.isDirectory()) {
                FileFilter jarFilter = new JarFileFilter(".*\\.jar");
                File[] jars = libDir.listFiles(jarFilter);
                for (int i = 0; i < jars.length; i++) {
                    File jar = jars[i];
                    if (!jar.getName().matches("commons-logging.*") && !jar.getName().matches("xwork.*") && !jar.getName().matches("webwork.*")) {
                        String jarPath = "file://" + jar.getCanonicalPath();
                        if (LOG.isDebugEnabled())
                            LOG.debug("jarPath=" + jarPath);
                        URL jarURL = new URL(jarPath);
                        classPathURLs[i + 1] = jarURL;
                    }
                }
            }
        } catch (MalformedURLException e1) {
            LOG.error("MalformedURLException", e1);
        } catch (IOException e) {
            LOG.error("IOException", e);
        }
        this.classLoader = URLClassLoader.newInstance(classPathURLs);
        Thread.currentThread().setContextClassLoader(this.classLoader);
    }

    /**
     * Default constructor
     */
    public WebWork2Collector() {
        super();
    }
}