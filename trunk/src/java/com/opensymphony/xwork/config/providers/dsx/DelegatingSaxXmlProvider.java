/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.util.FileManager;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork.xml.DefaultDelegatingHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * DelegatingSaxXmlProvider
 * @author Jason Carreira
 * Created May 19, 2003 7:54:41 AM
 */
public class DelegatingSaxXmlProvider implements ConfigurationProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log LOG = LogFactory.getLog(DelegatingSaxXmlProvider.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private String configFileName = "xwork.xml";

    //~ Constructors ///////////////////////////////////////////////////////////

    //    private Configuration configuration;
    public DelegatingSaxXmlProvider() {
    }

    public DelegatingSaxXmlProvider(String configFileName) {
        this.configFileName = configFileName;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public void destroy() {
    }

    /**
    * Initializes the configuration object.
    */
    public void init(Configuration configuration) throws ConfigurationException {
        //        this.configuration = configuration;
        SAXParser saxParser;

        DefaultDelegatingHandler handler;

        try {
            saxParser = SAXParserFactory.newInstance().newSAXParser();

            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setEntityResolver(new EntityResolver() {
                    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                        if ("-//OpenSymphony Group//XWork 1.0//EN".equals(publicId)) {
                            return new InputSource(ClassLoaderUtil.getResourceAsStream("xwork-1.0.dtd", XmlConfigurationProvider.class));
                        }

                        return null;
                    }
                });

            InputStream is = getInputStream(configFileName);

            if (is == null) {
                throw new Exception("Could not open file " + configFileName);
            }

            handler = new DefaultDelegatingHandler();
            new PackageHandler("/xwork/package", configuration).registerWith(handler);
            xmlReader.setContentHandler(handler);
            xmlReader.setErrorHandler(handler);
            xmlReader.parse(new InputSource(is));
        } catch (Exception e) {
            LOG.fatal("Could not load XWork configuration file, failing", e);

            return;
        }

        if ((handler != null) && (handler.getException() != null) && (handler.getException().hasErrors())) {
            Iterator exceptionIter = handler.getException().exceptions();

            while (exceptionIter.hasNext()) {
                LOG.error("Caught exception while parsing file " + configFileName, (Throwable) exceptionIter.next());
            }
        }
    }

    /**
    * Tells whether the ConfigurationProvider should reload its configuration. This method should only be called
    * if ConfigurationManager.isReloadingConfigs() is true.
    * @return true if the file has been changed since the last time we read it
    */
    public boolean needsReload() {
        return FileManager.fileNeedsReloading(configFileName);
    }

    protected InputStream getInputStream(String fileName) {
        InputStream is = FileManager.loadFile(fileName, this.getClass());

        return is;
    }
}
