/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.FileManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * ActionValidatorManager
 *
 * Created : Jan 20, 2003 8:26:51 AM
 *
 * @author Jason Carreira
 */
public class ActionValidatorManager {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String ACTION_VALIDATOR_KEY = "__ACTION_VALIDATOR_KEY__";
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";
    private static final Map validatorCache = Collections.synchronizedMap(new HashMap());
    private static final Map validatorFileCache = Collections.synchronizedMap(new HashMap());
    private static final Log LOG = LogFactory.getLog(ActionValidatorManager.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Primary method for validator lookup.
     */
    public static synchronized List getValidators(Class clazz, String context) {
        final String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            conditionalReload(clazz, context);
        } else {
            List validators = buildValidators(clazz, context, false);
            validatorCache.put(validatorKey, validators);
        }

        return (List) validatorCache.get(validatorKey);
    }

    public static void validate(Object object, String context) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext);
    }

    public static void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        List validators = getValidators(object.getClass(), context);

        Set shortcircuited = null;

        for (Iterator iterator = validators.iterator(); iterator.hasNext();) {
            Validator validator = (Validator) iterator.next();
            validator.setValidatorContext(validatorContext);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Running validator: " + validator + " for object " + object);
            }

            String fieldName = ACTION_VALIDATOR_KEY;
            FieldValidator fValidator = null;

            if (validator instanceof FieldValidator) {
                fValidator = (FieldValidator) validator;
                fieldName = fValidator.getFieldName();
            }

            if ((shortcircuited != null) && shortcircuited.contains(fieldName)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Short-circuited, skipping");
                }

                continue;
            }

            if (validator instanceof ShortCircuitingValidator && ((ShortCircuitingValidator) validator).isShortCircuit()) {
                // get number of existing errors
                int errs = 0;

                if (fValidator != null) {
                    if (validatorContext.hasFieldErrors()) {
                        Collection fieldErrors = (Collection) validatorContext.getFieldErrors().get(fieldName);

                        if (fieldErrors != null) {
                            errs = fieldErrors.size();
                        }
                    }
                } else if (validatorContext.hasActionErrors()) {
                    Collection actionErrors = validatorContext.getActionErrors();

                    if (actionErrors != null) {
                        errs = actionErrors.size();
                    }
                }

                validator.validate(object);

                Collection errCol = null;

                if (fValidator != null) {
                    if (validatorContext.hasFieldErrors()) {
                        errCol = (Collection) validatorContext.getFieldErrors().get(fieldName);
                    }
                } else if (validatorContext.hasActionErrors()) {
                    errCol = validatorContext.getActionErrors();
                }

                if ((errCol != null) && (errCol.size() > errs)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Short-circuiting");
                    }

                    if (shortcircuited == null) {
                        shortcircuited = new TreeSet();
                    }

                    shortcircuited.add(fieldName);
                }

                continue;
            }

            validator.validate(object);
        }
    }

    protected static String buildValidatorKey(Class clazz, String context) {
        StringBuffer sb = new StringBuffer(clazz.getName());
        sb.append("/");
        sb.append(context);

        return sb.toString();
    }

    private static List buildAliasValidators(Class aClass, String context, boolean checkFile) {
        final String fullClassName = aClass.getName();
        final String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        final String fullPath = fullClassName.replace('.', '/');
        final String path = fullPath.substring(0, fullPath.lastIndexOf('/') + 1);
        final String fileName = path + className + "-" + context + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    private static List buildClassValidators(Class aClass, boolean checkFile) {
        final String actionClassName = aClass.getName();
        final String actionName = actionClassName.replace('.', '/');

        String fileName = actionName + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    /**
    * This method 'collects' all the validators for a given action invocation.
    *
    * It will traverse up the class hierarchy looking for validators for every super class
    * and interface of the current action, as well as adding validators for any alias of
    * this invocation. Nifty!
    */
    private static List buildValidators(Class clazz, String context, boolean checkFile) {
        List validators = new ArrayList();

        // order matters for short-circuit
        validators.addAll(buildAliasValidators(clazz, context, checkFile));
        validators.addAll(buildClassValidators(clazz, checkFile));

        // look for validators for implemented interfaces
        Class[] interfaces = clazz.getInterfaces();

        for (int x = 0; x < interfaces.length; x++) {
            validators.addAll(buildClassValidators(interfaces[x], checkFile));
        }

        // looking for validators in class hierarchy
        Class anActionClass = clazz.getSuperclass();

        while (!anActionClass.equals(Object.class)) {
            // look for validators for this class
            validators.addAll(buildClassValidators(anActionClass, checkFile));

            // look for validators for implemented interfaces
            interfaces = anActionClass.getInterfaces();

            for (int x = 0; x < interfaces.length; x++) {
                validators.addAll(buildClassValidators(interfaces[x], checkFile));
            }

            // search up class hierarchy
            anActionClass = anActionClass.getSuperclass();
        }

        return validators;
    }

    private static void conditionalReload(Class clazz, String context) {
        if (FileManager.isReloadingConfigs()) {
            final String actionName = buildValidatorKey(clazz, context);
            validatorCache.put(actionName, buildValidators(clazz, context, true));
        }
    }

    private static List loadFile(String fileName, Class clazz, boolean checkFile) {
        List retList = Collections.EMPTY_LIST;

        if ((checkFile && FileManager.fileNeedsReloading(fileName)) || !validatorFileCache.containsKey(fileName)) {
            InputStream is = null;

            try {
                is = FileManager.loadFile(fileName, clazz);

                if (is != null) {
                    retList = new ArrayList(ValidatorFileParser.parseActionValidators(is));
                }
            } catch (Exception e) {
                LOG.error("Caught exception while loading file " + fileName, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOG.error("Unable to close input stream", e);
                    }
                }
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = (List) validatorFileCache.get(fileName);
        }

        return retList;
    }
}
