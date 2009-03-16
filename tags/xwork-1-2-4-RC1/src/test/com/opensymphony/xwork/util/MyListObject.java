package com.opensymphony.xwork.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class MyListObject {

    List includes = new ArrayList();
    List excludes = new ArrayList();

    public List getIncludes() {
        return includes;
    }

    public void setIncludes(List includes) {
        this.includes = includes;
    }

    public List getExcludes() {
        return excludes;
    }

    public void setExcludes(List excludes) {
        this.excludes = excludes;
    }
}
