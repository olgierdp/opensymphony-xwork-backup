/*
 * Created on 6/11/2004
 */
package com.opensymphony.xwork.spring.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon Stewart
 */
public class ActionAutowiringInterceptorTest extends XWorkTestCase {

  public void testShouldAutowireAction() throws Exception {
    StaticWebApplicationContext context = new StaticWebApplicationContext();
    context.getBeanFactory().registerSingleton("bean", new TestBean());
    TestBean bean = (TestBean)context.getBean("bean");

    loadSpringApplicationContextIntoApplication(context);

    SimpleAction action = new SimpleAction();
    ActionInvocation invocation = new TestActionInvocation(action);

    ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
    interceptor.setApplicationContext(context);
    interceptor.init();

    interceptor.before(invocation);

    assertEquals(bean, action.getBean());
  }
  
  public void testSetAutowireType() throws Exception {
    XmlConfigurationProvider c = new XmlConfigurationProvider("com/opensymphony/xwork/spring/xwork-autowire.xml");
    ConfigurationManager.addConfigurationProvider(c);
    ConfigurationManager.getConfiguration().reload();
    
    StaticWebApplicationContext appContext = new StaticWebApplicationContext();

    loadSpringApplicationContextIntoApplication(appContext);

    ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
    interceptor.init();
    
    SimpleAction action = new SimpleAction();
    ActionInvocation invocation = new TestActionInvocation(action);
    
    interceptor.before(invocation);

    ApplicationContext loadedContext = interceptor.getApplicationContext();

    assertEquals(appContext, loadedContext);
  }

  protected void loadSpringApplicationContextIntoApplication(ApplicationContext appContext) {
    Map application = new HashMap();
    application.put(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appContext);

    Map context = new HashMap();
    context.put(ActionContext.APPLICATION, application);
    ActionContext actionContext = new ActionContext(context);
    ActionContext.setContext(actionContext);
  }

  public void testLoadsApplicationContextUsingWebApplicationContextUtils() throws Exception {
    StaticWebApplicationContext appContext = new StaticWebApplicationContext();

    loadSpringApplicationContextIntoApplication(appContext);

    ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
    interceptor.init();
    
    SimpleAction action = new SimpleAction();
    ActionInvocation invocation = new TestActionInvocation(action);
    
    interceptor.before(invocation);

    ApplicationContext loadedContext = interceptor.getApplicationContext();

    assertEquals(appContext, loadedContext);
  }

  public void testIfApplicationContextIsNullThenBeanWillNotBeWiredUp() throws Exception {
    Map context = new HashMap();
    context.put(ActionContext.APPLICATION, new HashMap());
    ActionContext actionContext = new ActionContext(context);
    ActionContext.setContext(actionContext);

    ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
    interceptor.init();

    SimpleAction action = new SimpleAction();
    ActionInvocation invocation = new TestActionInvocation(action);
    TestBean bean = action.getBean();

    // If an exception is thrown here, things are going to go wrong in
    // production
    interceptor.before(invocation);

    assertEquals(bean, action.getBean());
  }
  
}
