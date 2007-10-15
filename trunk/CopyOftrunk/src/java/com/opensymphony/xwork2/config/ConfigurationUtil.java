/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * ConfigurationUtil
 *
 * @author Jason Carreira
 *         Created May 23, 2003 11:22:49 PM
 */
public class ConfigurationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUtil.class);


    private ConfigurationUtil() {
    }


    public static List buildParentsFromString(Configuration configuration, String parent) {
        if ((parent == null) || (parent.equals(""))) {
            return Collections.EMPTY_LIST;
        }

        StringTokenizer tokenizer = new StringTokenizer(parent, ", ");
        List parents = new ArrayList();

        while (tokenizer.hasMoreTokens()) {
            String parentName = tokenizer.nextToken().trim();

            if (!parentName.equals("")) {
                PackageConfig parentPackageContext = configuration.getPackageConfig(parentName);

                if (parentPackageContext != null) {
                    parents.add(parentPackageContext);
                }
            }
        }

        return parents;
    }
}
