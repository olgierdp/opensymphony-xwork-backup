/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.util.*;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * Default validator factory
 *
 * @version $Date$ $Id$
 * @author Jason Carreira
 * @author James House
 */
public class DefaultValidatorFactory implements ValidatorFactory {

    protected Map<String, String> validators = new HashMap<String, String>();
    private static Logger LOG = LoggerFactory.getLogger(DefaultValidatorFactory.class);
    protected ObjectFactory objectFactory;
    protected ValidatorFileParser validatorFileParser;

    @Inject
    public DefaultValidatorFactory(@Inject ObjectFactory objectFactory, @Inject ValidatorFileParser parser) {
        this.objectFactory = objectFactory;
        this.validatorFileParser = parser;
        parseValidators();
    }

    public Validator getValidator(ValidatorConfig cfg) {

        String className = lookupRegisteredValidatorType(cfg.getType());

        Validator validator;

        try {
            // instantiate the validator, and set configured parameters
            //todo - can this use the ThreadLocal?
            validator = objectFactory.buildValidator(className, cfg.getParams(), null); // ActionContext.getContext().getContextMap());
        } catch (Exception e) {
            final String msg = "There was a problem creating a Validator of type " + className + " : caused by " + e.getMessage();
            throw new XWorkException(msg, e, cfg);
        }

        // set other configured properties
        validator.setMessageKey(cfg.getMessageKey());
        validator.setDefaultMessage(cfg.getDefaultMessage());
        if (validator instanceof ShortCircuitableValidator) {
            ((ShortCircuitableValidator) validator).setShortCircuit(cfg.isShortCircuit());
        }

        return validator;
    }

    public void registerValidator(String name, String className) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering validator of class " + className + " with name " + name);
        }

        validators.put(name, className);
    }

    public String lookupRegisteredValidatorType(String name) {
        // lookup the validator class mapped to the type name
        String className = validators.get(name);

        if (className == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + name);
        }

        return className;
    }

    private void parseValidators() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading validator definitions.");
        }

        List<File> files = new ArrayList<File>();
        try {
            // Get custom validator configurations via the classpath
            Iterator<URL> urls = ClassLoaderUtil.getResources("", DefaultValidatorFactory.class, false);
            while (urls.hasNext()) {
                URL u = urls.next();
                URI uri = new URI(u.toExternalForm().replaceAll(" ", "%20"));
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    File f = new File(uri);
                    FilenameFilter filter = new FilenameFilter() {
                        public boolean accept(File file, String fileName) {
                            return fileName.contains("-validators.xml");
                        }
                    };
                    files.addAll(Arrays.asList(f.listFiles(filter)));
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // swallow
        } catch (IOException e) {
            throw new ConfigurationException("Unable to parse validators", e);
        }

        // Parse default validator configurations
        String resourceName = "com/opensymphony/xwork2/validator/validators/default.xml";
        retrieveValidatorConfiguration(resourceName);

        // Overwrite and extend defaults with application specific validator configurations
        resourceName = "validators.xml";
        retrieveValidatorConfiguration(resourceName);

        // Add custom (plugin) specific validator configurations
        for (File file : files) {
            retrieveValidatorConfiguration(file.getName());
        }
    }

    private void retrieveValidatorConfiguration(String resourceName) {
        InputStream is = ClassLoaderUtil.getResourceAsStream(resourceName, DefaultValidatorFactory.class);
        if (is != null) {
            validatorFileParser.parseValidatorDefinitions(validators, is, resourceName);
        }
    }
}
