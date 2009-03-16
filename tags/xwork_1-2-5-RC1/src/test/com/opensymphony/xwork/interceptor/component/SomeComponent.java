/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.component;


/**
 * @author $Author$
 * @version $Revision$
 */
public class SomeComponent implements FooAware, BarAware, SuperChild, Initializable, Disposable {

    Bar bar;
    Foo foo;
    Super superlative;
    boolean disposeCalled;
    boolean initCalled;


    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Bar getBar() {
        return bar;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setSuper(Super superlative) {
        this.superlative = superlative;
    }

    public Super getSuper() {
        return superlative;
    }

    public void dispose() {
        disposeCalled = true;
    }

    public void init() {
        initCalled = true;
    }
}
