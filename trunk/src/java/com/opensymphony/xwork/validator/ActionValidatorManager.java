/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.FileManager;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ActionValidatorManager
 *
 * Created : Jan 20, 2003 8:26:51 AM
 *
 * @author Jason Carreira
 */
public class ActionValidatorManager {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";
    private static final Map validatorCache = Collections.synchronizedMap(new HashMap());
    private static final Map validatorFileCache = Collections.synchronizedMap(new HashMap());

    //~ Methods ////////////////////////////////////////////////////////////////

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
    * of the current action, as well as adding validators for any alias of this invocation. Nifty!
    */
    private static List buildValidators(Class clazz, String context, boolean checkFile) {
        List validators = new ArrayList();

        // validators for the action class validators.addAll(buildClassValidators(actionClass, checkFile)); validators.addAll(buildAliasValidators(actionClass, invocation, checkFile));
        // looking for validators for every super class
        Class anActionClass = clazz;
        anActionClass = anActionClass.getSuperclass();

        while (!anActionClass.equals(Object.class)) {
            validators.addAll(buildClassValidators(anActionClass, checkFile));
            anActionClass = anActionClass.getSuperclass();
        }

        validators.addAll(buildClassValidators(clazz, checkFile));
        validators.addAll(buildAliasValidators(clazz, context, checkFile));

        return validators;
    }

    private static void conditionalReload(Class clazz, String context) {
        if (FileManager.isReloadingConfigs()) {
            final String actionName = buildValidatorKey(clazz, context);
            validatorCache.put(actionName, buildValidators(clazz, context, true));
        }
    }

    private static List loadFile(String fileName, Class clazz, boolean checkFile) {
        List retList;

        if ((checkFile && FileManager.fileNeedsReloading(fileName)) || !validatorFileCache.containsKey(fileName)) {
            InputStream is = FileManager.loadFile(fileName, clazz);

            if (is != null) {
                retList = new ArrayList(ValidatorFileParser.parseActionValidators(is));
            } else {
                retList = Collections.EMPTY_LIST;
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = (List) validatorFileCache.get(fileName);
        }

        return retList;
    }
}
