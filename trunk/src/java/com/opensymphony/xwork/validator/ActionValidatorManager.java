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

import java.util.*;


/**
 * This is the entry point into XWork's rule-based validation framework.  Validation rules are
 * specified in XML configuration files named "className-contextName-validation.xml" where
 * className is the name of the class the configuration is for and -contextName is optional
 * (contextName is an arbitrary key that is used to look up additional validation rules for a
 * specific context).
 *
 * @author Jason Carreira
 * @author Mark Woon
 */
public class ActionValidatorManager {
    //~ Static fields/initializers /////////////////////////////////////////////

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
            if (FileManager.isReloadingConfigs()) {
                validatorCache.put(validatorKey, buildValidators(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidators(clazz, context, false, null));
        }

        return (List) validatorCache.get(validatorKey);
    }

    public static void validate(Object object, String context) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext);
    }

    public static void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        List validators = getValidators(object.getClass(), context);
        Set shortcircuitedFields = null;

        for (Iterator iterator = validators.iterator(); iterator.hasNext();) {
            Validator validator = (Validator) iterator.next();
            validator.setValidatorContext(validatorContext);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Running validator: " + validator + " for object " + object);
            }

            FieldValidator fValidator = null;

            if (validator instanceof FieldValidator) {
                fValidator = (FieldValidator) validator;

                if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fValidator.getFieldName())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Short-circuited, skipping");
                    }

                    continue;
                }
            }

            if (validator instanceof ShortCircuitableValidator && ((ShortCircuitableValidator) validator).isShortCircuit()) {
                // get number of existing errors
                List errs = null;

                if (fValidator != null) {
                    if (validatorContext.hasFieldErrors()) {
                        Collection fieldErrors = (Collection) validatorContext.getFieldErrors().get(fValidator.getFieldName());

                        if (fieldErrors != null) {
                            errs = new ArrayList(fieldErrors);
                        }
                    }
                } else if (validatorContext.hasActionErrors()) {
                    Collection actionErrors = validatorContext.getActionErrors();

                    if (actionErrors != null) {
                        errs = new ArrayList(actionErrors);
                    }
                }

                validator.validate(object);

                if (fValidator != null) {
                    if (validatorContext.hasFieldErrors()) {
                        Collection errCol = (Collection) validatorContext.getFieldErrors().get(fValidator.getFieldName());

                        if ((errCol != null) && !errCol.equals(errs)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Short-circuiting on field validation");
                            }

                            if (shortcircuitedFields == null) {
                                shortcircuitedFields = new TreeSet();
                            }

                            shortcircuitedFields.add(fValidator.getFieldName());
                        }
                    }
                } else if (validatorContext.hasActionErrors()) {
                    Collection errCol = validatorContext.getActionErrors();

                    if ((errCol != null) && !errCol.equals(errs)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Short-circuiting");
                        }

                        break;
                    }
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
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    private static List buildClassValidators(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    /**
     * This method 'collects' all the validators for a given action invocation.
     *
     * It will traverse up the class hierarchy looking for validators for every super class
     * and directly implemented interface of the current action, as well as adding validators for
     * any alias of this invocation. Nifty!
     *
     * Given the following class structure:
     * <pre>
     *   interface Thing;
     *   interface Animal extends Thing;
     *   interface Quadraped extends Animal;
     *   class AnimalImpl implements Animal;
     *   class QuadrapedImpl extends AnimalImpl implements Quadraped;
     *   class Dog extends QuadrapedImpl;
     * </pre>
     *
     * This method will look for the following config files for Dog:
     * <pre>
     *   Animal
     *   Animal-context
     *   AnimalImpl
     *   AnimalImpl-context
     *   Quadraped
     *   Quadraped-context
     *   QuadrapedImpl
     *   QuadrapedImpl-context
     *   Dog
     *   Dog-context
     * </pre>
     *
     * Note that the validation rules for Thing is never looked for because no class in the
     * hierarchy directly implements Thing.
     *
     * @param clazz the Class to look up validators for
     * @param context the context to use when looking up validators
     * @param checkFile true if the validation config file should be checked to see if it has been
     * updated
     * @param checked the set of previously checked class-contexts, null if none have been checked
     */
    private static List buildValidators(Class clazz, String context, boolean checkFile, Set checked) {
        List validators = new ArrayList();

        if (checked == null) {
            checked = new TreeSet();
        } else if (checked.contains(clazz.getName())) {
            return validators;
        }

        if (clazz.isInterface()) {
            Class[] interfaces = clazz.getInterfaces();

            for (int x = 0; x < interfaces.length; x++) {
                validators.addAll(buildValidators(interfaces[x], context, checkFile, checked));
            }
        } else {
            if (!clazz.equals(Object.class)) {
                validators.addAll(buildValidators(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        // look for validators for implemented interfaces
        Class[] interfaces = clazz.getInterfaces();

        for (int x = 0; x < interfaces.length; x++) {
            if (checked.contains(interfaces[x].getName())) {
                continue;
            }

            validators.addAll(buildClassValidators(interfaces[x], checkFile));

            if (context != null) {
                validators.addAll(buildAliasValidators(interfaces[x], context, checkFile));
            }

            checked.add(interfaces[x].getName());
        }

        validators.addAll(buildClassValidators(clazz, checkFile));

        if (context != null) {
            validators.addAll(buildAliasValidators(clazz, context, checkFile));
        }

        checked.add(clazz.getName());

        return validators;
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
