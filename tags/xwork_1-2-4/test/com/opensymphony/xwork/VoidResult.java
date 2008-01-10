/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

/**
 */
public class VoidResult implements Result {

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof VoidResult)) {
            return false;
        }

        return true;
    }

    public void execute(ActionInvocation invocation) throws Exception {
    }

    public int hashCode() {
        return 42;
    }
}
