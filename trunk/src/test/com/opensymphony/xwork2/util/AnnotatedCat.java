/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.util.List;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @author Rainer Hermanns
 * @version $Revision$
 */
@Conversion()
public class AnnotatedCat {

    public static final String SCIENTIFIC_NAME = "Feline";


    Foo foo;
    List kittens;
    String name;


    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setKittens(List kittens) {
        this.kittens = kittens;
    }

    @TypeConversion(
            key = "kittens", converter = "com.opensymphony.xwork2.util.Cat"
    )
    public List getKittens() {
        return kittens;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
