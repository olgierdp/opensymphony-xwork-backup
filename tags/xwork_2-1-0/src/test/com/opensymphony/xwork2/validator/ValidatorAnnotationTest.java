package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork2.interceptor.StaticParametersInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;

import java.util.*;

/**
 * Unit test for annotated Validators.
 *
 * @author Rainer Hermanns
 */
public class ValidatorAnnotationTest extends XWorkTestCase {

    public void testNotAnnotatedMethodSuccess() throws Exception {
        HashMap params = new HashMap();
        params.put("date", "12/23/2002");
        params.put("foo", "5");
        params.put("bar", "7");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "notAnnotatedMethod", extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());

        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(0, errors.size());
    }

    public void testNotAnnotatedMethodSuccess2() throws Exception {
        HashMap params = new HashMap();

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "notAnnotatedMethod", extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());

        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(0, errors.size());
    }

    public void testAnnotatedMethodFailure() throws Exception {
        HashMap params = new HashMap();

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", extraContext);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasActionErrors());
        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(1, errors.size());

        assertEquals("Need param1 or param2.", errors.iterator().next());

    }

    public void testAnnotatedMethodSuccess() throws Exception {
        HashMap params = new HashMap();

        //make it not fail
        params.put("param1", "key1");
        params.put("param2", "key2");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    public void testAnnotatedMethodSuccess2() throws Exception {
        HashMap params = new HashMap();

        //make it not fail
        params.put("param2", "key2");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    public void testAnnotatedMethodSuccess3() throws Exception {
        HashMap params = new HashMap();

        //make it not fail
        params.put("param1", "key1");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    protected void setUp() throws Exception {
        super.setUp();

        loadConfigurationProviders(new XmlConfigurationProvider("xwork-default.xml"), new XmlConfigurationProvider("xwork-test-validation.xml"));
    }

}
