package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

/**
 * Test accessing {@link com.opensymphony.xwork.util.CompoundRootAccessor} through
 * the new enchance feature of OGNL (2.7+)
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CompoundRootAccessorTest extends XWorkTestCase {

    private PropertyAccessor oldCompoundRootAccessor;

    protected void setUp() throws Exception {
        super.setUp();
        oldCompoundRootAccessor = OgnlRuntime.getPropertyAccessor(CompoundRoot.class);
    }

    protected void tearDown() throws Exception {
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, oldCompoundRootAccessor);
        super.tearDown();
    }

    public void test() throws Exception {
        CompoundRoot root = new CompoundRoot();
        OgnlRuntime.getPropertyAccessor(CompoundRoot.class);

        // TODO: finish me

    }

}
