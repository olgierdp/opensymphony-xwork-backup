/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * XWorkListPropertyAccessorTest
 * <p/>
 * Created : Nov 7, 2005 3:54:44 PM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class XWorkListPropertyAccessorTest extends XWorkTestCase {

    public void testCanAccessListSizeProperty() {
        OgnlValueStack vs = new OgnlValueStack();
        List myList = new ArrayList();
        myList.add("a");
        myList.add("b");

        ListHolder listHolder = new ListHolder(myList);

        vs.push(listHolder);

        assertEquals(new Integer(myList.size()), vs.findValue("myList.size()"));
        assertEquals(new Integer(myList.size()), vs.findValue("myList.size"));
    }

    private class ListHolder {
        private List myList;

        public ListHolder(List myList) {
            this.myList = myList;
        }

        public List getMyList() {
            return myList;
        }

        public void setMyList(List myList) {
            this.myList = myList;
        }
    }
}
