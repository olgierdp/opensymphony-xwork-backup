package com.opensymphony.xwork.util;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * User: plightbo
 * Date: Jan 13, 2004
 * Time: 7:02:33 PM
 */
public class XWorkList extends ArrayList {
    private Class clazz;

    public XWorkList(Class clazz) {
        this.clazz = clazz;
    }

    public synchronized Object get(int index) {
        while (index >= this.size()) {
            try {
                this.add(clazz.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return super.get(index);
    }

}
