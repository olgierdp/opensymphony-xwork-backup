package com.opensymphony.xwork.validator;

import junit.framework.TestCase;

import java.util.*;

import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * VisitorFieldValidatorModelTest
 * @author Jason Carreira
 * Date: Mar 18, 2004 2:51:42 PM
 */
public class VisitorFieldValidatorModelTest extends TestCase {

    protected VisitorValidatorModelAction action;
    private Locale origLocale;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        action = new VisitorValidatorModelAction();

        TestBean bean = action.getBean();
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        bean.setBirth(cal.getTime());
        bean.setCount(-1);

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
    }

    public void testModelFieldErrorsAddedWithoutFieldPrefix() throws Exception {
        ActionValidatorManager.validate(action,null);
        assertTrue(action.hasFieldErrors());

        Map fieldErrors = action.getFieldErrors();
        // the required string validation inherited from the VisitorValidatorTestAction
        assertTrue(fieldErrors.containsKey("context"));
        // the bean validation which is now at the top level because we set the appendPrefix to false
        assertTrue(fieldErrors.containsKey("name"));

        List nameMessages = (List) fieldErrors.get("name");
        assertEquals(1, nameMessages.size());

        String nameMessage = (String) nameMessages.get(0);
        assertEquals("You must enter a name.", nameMessage);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
        Locale.setDefault(origLocale);
    }


}
