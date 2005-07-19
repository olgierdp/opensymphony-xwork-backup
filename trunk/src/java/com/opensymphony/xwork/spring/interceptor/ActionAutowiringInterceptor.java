/*
 * Created on 6/11/2004
 */
package com.opensymphony.xwork.spring.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;
import com.opensymphony.xwork.spring.SpringObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * Autowires action classes to Spring beans.  The strategy for autowiring the beans can be configured
 * by setting the parameter on the interceptor.  Actions that need access to the <code>ActionContext</code>
 * can implements the <code>ApplicationContextAware</code> interface.  The context will also be placed on 
 * the action context under the APPLICATION_CONTEXT attribute.
 * 
 * @author Simon Stewart
 * @author Eric Hauser
 */
public class ActionAutowiringInterceptor extends AroundInterceptor implements ApplicationContextAware {
  private static final Log log = LogFactory.getLog(ActionAutowiringInterceptor.class);
  
  public static final String APPLICATION_CONTEXT = "org.opensymphony.xwork.spring.interceptor.ActionAutowiringInterceptor.applicationContext";

  private boolean initialized = false;
  private ApplicationContext context;
  private SpringObjectFactory factory;
  private Integer autowireStrategy;
    
  /**
   * @param autowireStrategy
   */
  public void setAutowireStrategy(Integer autowireStrategy) {
    this.autowireStrategy = autowireStrategy;
  }
  
  /**
   * @param dispatcher
   * @param result
   * @throws Exception
   */
  protected void after(ActionInvocation dispatcher, String result) throws Exception {
    // Empty
  }

  /**
   * Looks for the <code>ApplicationContext</code> under the attribute that the Spring listener sets in
   * the servlet context.  The configuration is done the first time here instead of in init() since the 
   * <code>ActionContext</code> is not available during <code>Interceptor</code> initialization.
   * 
   * Autowires the action to Spring beans and places the <code>ApplicationContext</code>
   * on the <code>ActionContext</code>
   * 
   * TODO Should this check to see if the <code>SpringObjectFactory</code> has already been configured
   * instead of instantiating a new one?  Or is there a good reason for the interceptor to have it's own 
   * factory?
   * 
   * @param invocation
   * @throws Exception
   */
  protected void before(ActionInvocation invocation) throws Exception {
    if (!initialized) {
      ApplicationContext applicationContext = (ApplicationContext)ActionContext.getContext().getApplication().get(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

      if (applicationContext == null) {
        log.warn("ApplicationContext could not be found.  Action classes will not be autowired.");
      } else {
        setApplicationContext(applicationContext);
        factory = new SpringObjectFactory();
        factory.setApplicationContext(getApplicationContext());
        if (autowireStrategy != null) {
          factory.setAutowireStrategy(autowireStrategy.intValue());
        }
      }
      initialized = true;
    }
    
    if (factory == null)
      return;

    Action bean = invocation.getAction();
    factory.autoWireBean(bean);
    
    ActionContext.getContext().put(APPLICATION_CONTEXT, context);
  }

  /**
   * @param applicationContext
   * @throws BeansException
   */
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  /**
   * @return
   */
  protected ApplicationContext getApplicationContext() {
    return context;
  }

}
