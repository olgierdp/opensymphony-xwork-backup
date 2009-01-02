package com.opensymphony.xwork2.mvel;

import com.opensymphony.xwork2.mvel.CompoundRootAccessor;
import com.opensymphony.xwork2.mvel.accessors.DefaultVariableResolverFactory;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ArrayUtils;
import com.opensymphony.xwork2.Foo;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.XWorkException;

import junit.framework.TestCase;
import org.mvel2.PropertyAccessor;
import org.mvel2.MVEL;
import org.mvel2.util.PropertyTools;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.Listener;
import org.mvel2.integration.GlobalListenerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class CompoundRootAccessorTest extends TestCase {
    private CompoundRootAccessor accessor;

    public void testGetShortCircuitStack() {
        CompoundRoot root = new CompoundRoot();

        root.add(new Foo("value1"));
        root.add(new Foo("value2"));

        assertEquals("value1", accessor.getProperty(new MVELContext(), root, "name"));
    }


    public void testmvel() {
        class Dummy {
            List myList = Arrays.asList("test1", "test2");

            public List getMyList() {
                return myList;
            }

            public void setMyList(List myList) {
                this.myList = myList;
            }
        }

        PropertyHandlerFactory.registerPropertyHandler(List.class, new PropertyHandler() {
            public Object getProperty(String name, Object contextObj, VariableResolverFactory variableFactory) {
                throw new RuntimeException();
            }

            public Object setProperty(String name, Object contextObj, VariableResolverFactory variableFactory, Object value) {
                throw new RuntimeException();
            }
        });

        MVEL.setProperty(new Dummy(), "myList[0]", "test1");
    }

    public void testSetShortCircuitStack() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        SimpleAction action2 = new SimpleAction();

        root.add(action);
        root.add(action2);

        accessor.setProperty(new MVELContext(), root, "bar", "10");
        assertEquals(10, action.getBar());
        assertEquals(0, action2.getBar());
    }

    public void testGetSimpleBean() {
        CompoundRoot root = new CompoundRoot();

        root.add(new Foo("value1"));

        assertEquals("value1", accessor.getProperty(new MVELContext(), root, "name"));
    }

    public void testSetSimpleBean() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();

        root.add(action);

        accessor.setProperty(new MVELContext(), root, "bar", "10");
        assertEquals(10, action.getBar());
    }

    public void testSetSimpleBeanReportError() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();

        root.add(action);

        MVELContext context = new MVELContext();
        context.put(ValueStack.REPORT_ERRORS_ON_NO_PROP, true);

        try {
            accessor.setProperty(context, root, "bar_notthere", "10");
            fail("Error was not reported while setting a property which was not found");
        } catch (XWorkException e) {
            //expected
        }
    }

    public void testSetSimpleBeanDontReportError() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        root.add(action);

        MVELContext context = new MVELContext();
        context.put(ValueStack.REPORT_ERRORS_ON_NO_PROP, false);

        accessor.setProperty(context, root, "bar_notthere", "10");
    }

    public void testGetSimpleBeanStacked() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        action.setBar(10);

        root.add(new Foo("value1"));
        root.add(action);

        assertEquals(10, accessor.getProperty(new MVELContext(), root, "bar"));
    }

    public void testSetSimpleBeanStacked() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();

        root.add(new Foo("value1"));
        root.add(action);

        accessor.setProperty(new MVELContext(), root, "bar", "10");
        assertEquals(10, action.getBar());
    }

    public void testGetPropertyNotFound() {
        CompoundRoot root = new CompoundRoot();

        root.add(new Foo("value1"));

        assertNull(accessor.getProperty(new MVELContext(), root, "name_not_there"));
    }

    public void testGetSimpleMap() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        action.getDummyMap().put("name", "value1");

        root.add(action);

        assertEquals("value1", accessor.getProperty(new MVELContext(), root, "dummyMap.name"));
        assertEquals("value1", accessor.getProperty(new MVELContext(), root, "dummyMap['name']"));
    }

    public void testSetSimpleMap() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();

        root.add(action);

        accessor.setProperty(new MVELContext(), root, "dummyMap['name2']", "value2");

        assertEquals("value2", action.getDummyMap().get("name2"));
    }

    public void testGetMapWithNestedBean() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        action.getDummyMap().put("foo", new Foo("value1"));
        root.add(action);

        //assertEquals("value1", accessor.getProperty(null, root, "dummyMap.foo.name"));
        assertEquals("value1", accessor.getProperty(new MVELContext(), root, "dummyMap['foo'].name"));
    }

    public void testSetMapWithNestedBean() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        SimpleAction action2 = new SimpleAction();
        action.getDummyMap().put("action", action2);

        root.add(action);

        accessor.setProperty(new MVELContext(), root, "dummyMap.action.bar", "10");
        assertEquals(10, action2.getBar());

        accessor.setProperty(new MVELContext(), root, "dummyMap['action'].bar", "11");
        assertEquals(11, action2.getBar());
    }


    public void testGetSimpleMapWithContext() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        action.getDummyMap().put("name", "value1");
        root.add(action);

        MVELContext context = new MVELContext();
        context.put("propertyName", "name");

        assertEquals("value1", accessor.getProperty(context, root, "dummyMap[propertyName]"));
    }

    public void testSetSimpleMapWithContext() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        root.add(action);

        MVELContext context = new MVELContext();
        context.put("propertyName", "name");

        accessor.setProperty(context, root, "dummyMap[propertyName]", "value1");

        assertEquals("value1", action.getDummyMap().get("name"));
    }

    public void testGetSimpleMapWithContextAndNestedBean() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        action.getDummyMap().put("foo", new Foo("value1"));
        root.add(action);

        MVELContext context = new MVELContext();
        context.put("propertyName", "foo");

        assertEquals("value1", accessor.getProperty(context, root, "dummyMap[propertyName].name"));
    }

    public void testSetSimpleMapWithContextAndNestedBean() {
        CompoundRoot root = new CompoundRoot();
        SimpleAction action = new SimpleAction();
        action.getDummyMap().put("foo", new Foo("value1"));
        root.add(action);

        MVELContext context = new MVELContext();
        context.put("propertyName", "foo");

        assertEquals("value1", accessor.getProperty(context, root, "dummyMap[propertyName].name"));
    }

    public void testGetAccessList() {
        CompoundRoot root = new CompoundRoot();

        SimpleAction action = new SimpleAction();
        action.getSomeList().add("value1");
        root.add(action);

        assertEquals("value1", accessor.getProperty(new MVELContext(), root, "someList[0]"));
    }

    public void testSetList() {
        CompoundRoot root = new CompoundRoot();

        SimpleAction action = new SimpleAction();
        root.add(action);

        ArrayList someList = new ArrayList();
        MVELContext context = new MVELContext();
        context.put(ValueStack.REPORT_ERRORS_ON_NO_PROP, true);
        accessor.setProperty(context, root, "someList", someList);

        assertEquals(someList, action.getSomeList());
    }

    public void testSetListListener() {
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        class MyListener implements Listener {
            public int counter;
            public void onEvent(Object context, String contextName, VariableResolverFactory variableFactory, Object value) {
                counter++;
            }
        }

        class MyBean {
            private List someList;

            public List getSomeList() {
                return someList;
            }
        }

        MyListener listener = new MyListener();
        GlobalListenerFactory.registerGetListener(listener);
        MVEL.getProperty("someList", new MyBean());
        MVEL.getProperty("someList", new MyBean());
        assertEquals(2, listener.counter);
    }

    public class Dummy {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private List someList;

        public List getSomeList() {
            return someList;
        }

        public void setSomeList(List someList) {
            this.someList = someList;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.accessor = new CompoundRootAccessor();
        this.accessor.setMvelUtil(new MVELUtil());
    }
}
