/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */

import com.opensymphony.xwork.ActionSupport;


/**
 * @author Mark Woon
 */
public class PackagelessAction extends ActionSupport {

    /**
     * Default constructor.
     */
    public PackagelessAction() {
    }


    public String execute() {
        // from action's bundle
        System.out.println(getText("actionProperty"));

        // from default bundle
        System.out.println(getText("foo.range"));

        // nonexistant
        System.out.println(getText("non.existant"));

        return NONE;
    }
}
