/*
 * Copyright (c) 2002-2006 by OpenSymphony
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
 * @author James House
 * @author Rainer Hermanns
 * @author tmjee
 */
public class DefaultActionValidatorManager extends AbstractActionValidatorManager {

    /** The file suffix for any validation file. */
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";

    private static final Map validatorCache = Collections.synchronizedMap(new HashMap());
    private static final Map validatorFileCache = Collections.synchronizedMap(new HashMap());
    private static final Log LOG = LogFactory.getLog(DefaultActionValidatorManager.class);

    /**
     * Returns a list of validators for the given class and context. This is the primary
     * lookup method for validators.
     *
     * @param clazz the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    public synchronized List getValidators(Class clazz, String context) {
        final String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            if (FileManager.isReloadingConfigs()) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }

        // get the set of validator configs
        List cfgs = (List) validatorCache.get(validatorKey);

        // create clean instances of the validators for the caller's use
        return buildValidatorsFromValidatorConfig(cfgs);
    }


    private List buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    private List buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    /**
     * <p>This method 'collects' all the validator configurations for a given
     * action invocation.</p>
     *
     * <p>It will traverse up the class hierarchy looking for validators for every super class
     * and directly implemented interface of the current action, as well as adding validators for
     * any alias of this invocation. Nifty!</p>
     *
     * <p>Given the following class structure:
     * <pre>
     *   interface Thing;
     *   interface Animal extends Thing;
     *   interface Quadraped extends Animal;
     *   class AnimalImpl implements Animal;
     *   class QuadrapedImpl extends AnimalImpl implements Quadraped;
     *   class Dog extends QuadrapedImpl;
     * </pre></p>
     *
     * <p>This method will look for the following config files for Dog:
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
     * </pre></p>
     *
     * <p>Note that the validation rules for Thing is never looked for because no class in the
     * hierarchy directly implements Thing.</p>
     *
     * @param clazz the Class to look up validators for.
     * @param context the context to use when looking up validators.
     * @param checkFile true if the validation config file should be checked to see if it has been
     *      updated.
     * @param checked the set of previously checked class-contexts, null if none have been checked
     * @return a list of validator configs for the given class and context.
     */
    private List buildValidatorConfigs(Class clazz, String context, boolean checkFile, Set checked) {
        List validatorConfigs = new ArrayList();

        if (checked == null) {
            checked = new TreeSet();
        } else if (checked.contains(clazz.getName())) {
            return validatorConfigs;
        }

        if (clazz.isInterface()) {
            Class[] interfaces = clazz.getInterfaces();

            for (int x = 0; x < interfaces.length; x++) {
                validatorConfigs.addAll(buildValidatorConfigs(interfaces[x], context, checkFile, checked));
            }
        } else {
            if (!clazz.equals(Object.class)) {
                validatorConfigs.addAll(buildValidatorConfigs(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        // look for validators for implemented interfaces
        Class[] interfaces = clazz.getInterfaces();

        for (int x = 0; x < interfaces.length; x++) {
            if (checked.contains(interfaces[x].getName())) {
                continue;
            }

            validatorConfigs.addAll(buildClassValidatorConfigs(interfaces[x], checkFile));

            if (context != null) {
                validatorConfigs.addAll(buildAliasValidatorConfigs(interfaces[x], context, checkFile));
            }

            checked.add(interfaces[x].getName());
        }

        validatorConfigs.addAll(buildClassValidatorConfigs(clazz, checkFile));

        if (context != null) {
            validatorConfigs.addAll(buildAliasValidatorConfigs(clazz, context, checkFile));
        }

        checked.add(clazz.getName());

        return validatorConfigs;
    }

    private List loadFile(String fileName, Class clazz, boolean checkFile) {
        List retList = Collections.EMPTY_LIST;

        if ((checkFile && FileManager.fileNeedsReloading(fileName)) || !validatorFileCache.containsKey(fileName)) {
            InputStream is = null;

            try {
                is = FileManager.loadFile(fileName, clazz);

                if (is != null) {
                    retList = new ArrayList(ValidatorFileParser.parseActionValidatorConfigs(is, fileName));
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOG.error("Unable to close input stream for " + fileName, e);
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
