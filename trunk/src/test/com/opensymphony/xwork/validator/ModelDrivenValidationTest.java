package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ModelDrivenAction;
import com.opensymphony.xwork.ActionContext;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * ModelDrivenValidationTest
 * @author Jason Carreira
 * Created Oct 1, 2003 10:08:25 AM
 */
public class ModelDrivenValidationTest extends TestCase {

    public void testModelDrivenValidation() throws Exception {
        Map params = new HashMap();
        params.put("count",new String[]{"11"});
        Map context = new HashMap();
        context.put(ActionContext.PARAMETERS, params);
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null,"TestModelDrivenValidation",context);
        assertEquals(Action.SUCCESS, proxy.execute());
        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        assertTrue(action.hasFieldErrors());
        assertTrue(action.getFieldErrors().containsKey("count"));
        assertEquals("count must be between 1 and 10, current value is 11.", ((List)action.getFieldErrors().get("count")).get(0));
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
}
