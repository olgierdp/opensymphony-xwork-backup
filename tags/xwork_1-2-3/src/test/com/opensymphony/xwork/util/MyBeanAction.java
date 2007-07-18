/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.Action;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * <code>MyBeanAction</code>
 *
 * @author Rainer Hermanns
 */
public class MyBeanAction implements Action {

    private List beanList = new ArrayList();
    private Map beanMap = new HashMap();

    public List getBeanList() {
        return beanList;
    }

    public void setBeanList(List beanList) {
        this.beanList = beanList;
    }

    public Map getBeanMap() {
        return beanMap;
    }

    public void setBeanMap(Map beanMap) {
        this.beanMap = beanMap;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

}
