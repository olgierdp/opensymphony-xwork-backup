package com.opensymphony.xwork2.parameters.bytecode;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.SimpleAction;

public class AccessorBytecodeUtilTest extends XWorkTestCase {
    private AccessorBytecodeUtil accessorBytecodeUtil;

    public void testCreateSetter() throws Exception {
        Setter setter = accessorBytecodeUtil.getSetter(SimpleAction.class, String.class, "name");

        SimpleAction action = new SimpleAction();
        setter.invoke(action, "Fyodor");

        assertEquals("Fyodor", action.getName());
    }

    public void testCreateGetter() throws Exception {
        Getter getter = accessorBytecodeUtil.getGetter(SimpleAction.class, "name");

        SimpleAction action = new SimpleAction();
        action.setName("Dostoevsky");
        String result = (String) getter.invoke(action);

        assertNotNull(result);
        assertEquals("Dostoevsky", result);
    }

    public void testGetterCache() throws Exception {
        Getter getter0 = accessorBytecodeUtil.getGetter(SimpleAction.class, "name");
        Getter getter1 = accessorBytecodeUtil.getGetter(SimpleAction.class, "name");

        assertEquals(1, accessorBytecodeUtil.gettersCache.size());
        assertSame(getter0, getter1);
    }

    public void testSetterCache() throws Exception {
        Setter setter0 = accessorBytecodeUtil.getSetter(SimpleAction.class, String.class, "name");
        Setter setter1 = accessorBytecodeUtil.getSetter(SimpleAction.class, String.class, "name");

        assertEquals(1, accessorBytecodeUtil.settersCache.size());
        assertSame(setter0, setter1);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.accessorBytecodeUtil = container.getInstance(AccessorBytecodeUtil.class);
    }
}
