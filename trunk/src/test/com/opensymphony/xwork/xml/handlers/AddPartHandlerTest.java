/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.opensymphony.xwork.xml.DefaultDelegatingHandler;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.AddPartHandler;

import junit.framework.TestCase;


/**
 * AddPartHandlerTest
 * @author Jason Carreira
 * Created May 19, 2003 8:26:54 AM
 */
public class AddPartHandlerTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testAddPart() {
        DefaultDelegatingHandler root = new DefaultDelegatingHandler("");
        TestBean parent = new TestBean();
        parent.setName("parent");

        TestBean child1 = new TestBean();
        child1.setName("child1");

        TestBean child2 = new TestBean();
        child2.setName("child2");
        root.getValueStack().push(parent);
        root.getValueStack().push(child1);
        assertEquals(0, parent.getChildren().size());

        Path fooPath = Path.getInstance("/Foo");

        AddPartHandler handler = new AddPartHandler(fooPath, "addChild");
        handler.registerWith(root);

        handler.endingPath(fooPath);

        assertEquals(1, parent.getChildren().size());
        assertEquals(child1, parent.getChildren().get(0));

        root.getValueStack().push(child2);
        handler.endingPath(fooPath);

        assertEquals(2, parent.getChildren().size());
        assertEquals(child1, parent.getChildren().get(0));
        assertEquals(child2, parent.getChildren().get(1));
    }
}
