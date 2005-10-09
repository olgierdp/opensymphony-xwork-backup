/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.PackageConfig;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderPackagesTest extends ConfigurationTestBase {

    public void testBadInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-bad-inheritance.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);
    }

    public void testBasicPackages() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-basic-packages.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);

        // setup our expectations
        PackageConfig expectedNamespacePackage = new PackageConfig("namespacepkg", "/namespace/set", false, null);
        PackageConfig expectedAbstractPackage = new PackageConfig("abstractpkg", null, true, null);

        // test expectations
        assertEquals(3, configuration.getPackageConfigs().size());
        assertEquals(expectedNamespacePackage, configuration.getPackageConfig("namespacepkg"));
        assertEquals(expectedAbstractPackage, configuration.getPackageConfig("abstractpkg"));
    }

    public void testDefaultPackage() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-default-package.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        provider.init(configuration);

        // setup our expectations
        PackageConfig expectedPackageConfig = new PackageConfig("default");

        // test expectations
        assertEquals(1, configuration.getPackageConfigs().size());
        assertEquals(expectedPackageConfig, configuration.getPackageConfig("default"));
    }

    public void testPackageInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-package-inheritance.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup our expectations
        PackageConfig defaultPackage = new PackageConfig("default");

        PackageConfig abstractPackage = new PackageConfig("abstractPackage", null, true, null);

        PackageConfig singleInheritancePackage = new PackageConfig("singleInheritance");
        singleInheritancePackage.addParent(defaultPackage);

        PackageConfig multInheritancePackage = new PackageConfig("multipleInheritance");
        multInheritancePackage.addParent(defaultPackage);
        multInheritancePackage.addParent(abstractPackage);
        multInheritancePackage.addParent(singleInheritancePackage);

        provider.init(configuration);

        // test expectations
        assertEquals(4, configuration.getPackageConfigs().size());
        assertEquals(defaultPackage, configuration.getPackageConfig("default"));
        assertEquals(abstractPackage, configuration.getPackageConfig("abstractPackage"));
        assertEquals(singleInheritancePackage, configuration.getPackageConfig("singleInheritance"));
        assertEquals(multInheritancePackage, configuration.getPackageConfig("multipleInheritance"));
    }
}
