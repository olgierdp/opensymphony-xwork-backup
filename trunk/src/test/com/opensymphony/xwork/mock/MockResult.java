/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.mock;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 5:03:07 PM
 * To change this template use Options | File Templates.
 */
public class MockResult implements Result {

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MockResult)) {
            return false;
        }

        return true;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        System.out.println("MockResult.execute");
    }

    public int hashCode() {
        return 10;
    }
}
