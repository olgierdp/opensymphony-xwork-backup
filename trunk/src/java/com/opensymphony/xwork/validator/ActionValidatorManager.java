/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.FileManager;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;

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

    public static synchronized List getValidators(ActionInvocation invocation) {
        final String validatorKey = buildValidatorKey(invocation);

        if (validatorCache.containsKey(validatorKey)) {
            conditionalReload(invocation);
        } else {
            List validators = buildValidators(invocation, false);
            validatorCache.put(validatorKey, validators);
        }

        return (List) validatorCache.get(validatorKey);
    }

    protected static String buildValidatorKey(ActionInvocation invocation) {
        final ActionProxy proxy = invocation.getProxy();
        StringBuffer sb = new StringBuffer(proxy.getNamespace());
        sb.append("/");
        sb.append(proxy.getActionName());

        return sb.toString();
    }

    private static List buildAliasValidators(Class aClass, ActionInvocation invocation, boolean checkFile) {
        final String actionClassName = aClass.getName();
        final String actionName = actionClassName.replace('.', '/');
        final String actionPath = actionName.substring(0, actionName.lastIndexOf('/') + 1);
        final String aliasName = actionPath + invocation.getProxy().getActionName();

        if (!aliasName.equals(actionName)) {
            String fileName = aliasName + VALIDATION_CONFIG_SUFFIX;

            return loadFile(fileName, aClass, checkFile);
        }

        return Collections.EMPTY_LIST;
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
    private static List buildValidators(ActionInvocation invocation, boolean checkFile) {
        List validators = new ArrayList();
        final Class actionClass = invocation.getAction().getClass();

        // validators for the action class validators.addAll(buildClassValidators(actionClass, checkFile)); validators.addAll(buildAliasValidators(actionClass, invocation, checkFile));
        // looking for validators for every super class
        Class anActionClass = actionClass;
        anActionClass = anActionClass.getSuperclass();

        while (!anActionClass.equals(Object.class)) {
            validators.addAll(buildClassValidators(anActionClass, checkFile));
            anActionClass = anActionClass.getSuperclass();
        }

        validators.addAll(buildClassValidators(actionClass, checkFile));
        validators.addAll(buildAliasValidators(actionClass, invocation, checkFile));

        return validators;
    }

    private static void conditionalReload(ActionInvocation invocation) {
        if (FileManager.isReloadingConfigs()) {
            final String actionName = buildValidatorKey(invocation);
            validatorCache.put(actionName, buildValidators(invocation, true));
        }
    }

    private static List loadFile(String fileName, Class clazz, boolean checkFile) {
        List retList;

        if ((checkFile && FileManager.fileNeedsReloading(fileName)) || !validatorFileCache.containsKey(fileName)) {
            InputStream is = FileManager.loadFile(fileName, clazz);

            //            InputStream is = ClassLoaderUtil.getResourceAsStream(fileName, clazz);
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
