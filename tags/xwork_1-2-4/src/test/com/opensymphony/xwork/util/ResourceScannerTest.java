/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;

import java.net.URL;
import java.util.List;

/**
 * Test case for {@link com.opensymphony.xwork.util.ResourceScanner}.
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ResourceScannerTest extends XWorkTestCase {

    public void test1() throws Exception {
        ResourceScanner scanner = new ResourceScanner(
                new String[] { "com/opensymphony/xwork/util/" }, 
                ResourceScanner.class
        );
        List l = scanner.scanForResources(new ResourceScanner.Filter() {
            public boolean accept(URL resource) {
                return true;
            }
        });
        
        assertTrue(l.size() > 0);
    }

    public void test2() throws Exception {
        ResourceScanner scanner = new ResourceScanner(
                new String[] { "org/apache/commons/logging/" }, 
                ResourceScanner.class
        );
        List l = scanner.scanForResources(new ResourceScanner.Filter() {
            public boolean accept(URL resource) {
                return true;
            }
        });
        assertTrue(l.size() > 0);
    }

    public void test3() throws Exception {
        ResourceScanner scanner = new ResourceScanner(
                new String[] {
                        "com/opensymphony/xwork/util/",
                        "org/apache/commons/logging/"
                }, 
                ResourceScanner.class
        );
        List l = scanner.scanForResources(new ResourceScanner.Filter() {
            public boolean accept(URL resource) {
                return true;
            }
        });
        
        assertTrue(l.size() > 0);
    }

    public void test() throws Exception {

        ResourceScanner scanner = new ResourceScanner(
                new String[] { "" },
                ResourceScanner.class
        );
        List l = scanner.scanForResources(new ResourceScanner.Filter() {
            public boolean accept(URL resource) {
                System.out.println(resource.getFile());
                if (resource.getFile().endsWith("COPYRIGHT")) {
                    return true;
                }
                return true;
            }
        });

        assertTrue(l.size() > 0);
    }
}
