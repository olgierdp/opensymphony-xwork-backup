package com.opensymphony.xwork.spring;

/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Mar 8, 2004
 */

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork.interceptor.TimerInterceptor;
import com.opensymphony.xwork.validator.Validator;
import com.opensymphony.xwork.validator.validators.ExpressionValidator;
import com.opensymphony.xwork.validator.validators.RequiredStringValidator;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.StaticApplicationContext;

import java.util.HashMap;

// TODO: Document properly

/**
 * @author Simon Stewart
 */
public class SpringObjectFactoryTest extends XWorkTestCase {

    StaticApplicationContext sac;


    public void setUp() throws Exception {
        super.setUp();

        sac = new StaticApplicationContext();

        SpringObjectFactory objFactory = new SpringObjectFactory();
        objFactory.setApplicationContext(sac);
        ObjectFactory.setObjectFactory(objFactory);
    }

    public void testFallsBackToDefaultObjectFactoryActionSearching() throws Exception {
        ActionConfig actionConfig = new ActionConfig(null, ModelDrivenAction.class.getName(), new HashMap(), new HashMap(), null);

        Object action = ObjectFactory.getObjectFactory().buildBean(actionConfig.getClassName(), null);

        assertEquals(ModelDrivenAction.class, action.getClass());
    }

    public void testFallsBackToDefaultObjectFactoryInterceptorBuilding() throws Exception {
        InterceptorConfig iConfig = new InterceptorConfig("timer", ModelDrivenInterceptor.class.getName(), new HashMap());

        Interceptor interceptor = ObjectFactory.getObjectFactory().buildInterceptor(iConfig, new HashMap());

        assertEquals(ModelDrivenInterceptor.class, interceptor.getClass());
    }

    public void testFallsBackToDefaultObjectFactoryResultBuilding() throws Exception {
        ResultConfig rConfig = new ResultConfig(Action.SUCCESS, ActionChainResult.class.getName(), null);
        Result result = ObjectFactory.getObjectFactory().buildResult(rConfig, ActionContext.getContext().getContextMap());

        assertEquals(ActionChainResult.class, result.getClass());
    }

    public void testFallsBackToDefaultObjectFactoryValidatorBuilding() throws Exception {
        Validator validator = ObjectFactory.getObjectFactory().buildValidator(RequiredStringValidator.class.getName(), new HashMap(), null);

        assertEquals(RequiredStringValidator.class, validator.getClass());
    }

    public void testObtainActionBySpringName() throws Exception {
        sac.registerPrototype("simple-action", SimpleAction.class, new MutablePropertyValues());

        ActionConfig actionConfig = new ActionConfig(null, "simple-action", new HashMap(), new HashMap(), null);
        Object action = ObjectFactory.getObjectFactory().buildBean(actionConfig.getClassName(), null);

        assertEquals(SimpleAction.class, action.getClass());
    }

    public void testObtainInterceptorBySpringName() throws Exception {
        sac.registerSingleton("timer-interceptor", TimerInterceptor.class, new MutablePropertyValues());

        InterceptorConfig iConfig = new InterceptorConfig("timer", "timer-interceptor", new HashMap());
        Interceptor interceptor = ObjectFactory.getObjectFactory().buildInterceptor(iConfig, new HashMap());

        assertEquals(TimerInterceptor.class, interceptor.getClass());
    }

    public void testObtainResultBySpringName() throws Exception {
        // TODO: Does this need to be a prototype?
        sac.registerPrototype("chaining-result", ActionChainResult.class, new MutablePropertyValues());

        ResultConfig rConfig = new ResultConfig(Action.SUCCESS, "chaining-result", null);
        Result result = ObjectFactory.getObjectFactory().buildResult(rConfig, ActionContext.getContext().getContextMap());

        assertEquals(ActionChainResult.class, result.getClass());
    }

    public void testObtainValidatorBySpringName() throws Exception {
        sac.registerPrototype("expression-validator", ExpressionValidator.class, new MutablePropertyValues());

        Validator validator = ObjectFactory.getObjectFactory().buildValidator("expression-validator", new HashMap(), null);

        assertEquals(ExpressionValidator.class, validator.getClass());
    }

    public void testShouldAutowireObjectsObtainedFromTheObjectFactoryByFullClassName() throws Exception {
        sac.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) sac.getBean("bean");

        SimpleAction action = (SimpleAction) ObjectFactory.getObjectFactory().buildBean(SimpleAction.class.getName(), null);

        assertEquals(bean, action.getBean());
    }

    public void testShouldGiveReferenceToAppContextIfBeanIsApplicationContextAwareAndNotInstantiatedViaSpring() throws Exception {
        Foo foo = (Foo) ObjectFactory.getObjectFactory().buildBean(Foo.class.getName(), null);

        assertTrue("Expected app context to have been set", foo.isApplicationContextSet());
    }

    public static class Foo implements ApplicationContextAware {
        boolean applicationContextSet = false;

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            applicationContextSet = true;
        }

        public boolean isApplicationContextSet() {
            return applicationContextSet;
        }
    }

    public void testShouldAutowireObjectsObtainedFromTheObjectFactoryByClass() throws Exception {
        sac.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) sac.getBean("bean");

        SimpleAction action = (SimpleAction) ObjectFactory.getObjectFactory().buildBean(SimpleAction.class, null);

        assertEquals(bean, action.getBean());
    }

    public void testShouldGiveReferenceToAppContextIfBeanIsLoadedByClassApplicationContextAwareAndNotInstantiatedViaSpring() throws Exception {
        Foo foo = (Foo) ObjectFactory.getObjectFactory().buildBean(Foo.class, null);

        assertTrue("Expected app context to have been set", foo.isApplicationContextSet());
    }

    public void testLookingUpAClassInstanceDelegatesToSpring() throws Exception {
        sac.registerPrototype("simple-action", SimpleAction.class, new MutablePropertyValues());

        Class clazz = ObjectFactory.getObjectFactory().getClassInstance("simple-action");

        assertNotNull("Nothing returned", clazz);
        assertEquals("Expected to have instance of SimpleAction returned", SimpleAction.class, clazz);
    }

    public void testLookingUpAClassInstanceFallsBackToTheDefaultObjectFactoryIfSpringBeanNotFound() throws Exception {
        Class clazz = ObjectFactory.getObjectFactory().getClassInstance(SimpleAction.class.getName());

        assertNotNull("Nothing returned", clazz);
        assertEquals("Expected to have instance of SimpleAction returned", SimpleAction.class, clazz);
    }

    public void testSetAutowireStrategy() throws Exception {
        SpringObjectFactory objectFactory = (SpringObjectFactory) ObjectFactory.getObjectFactory();
        assertEquals(objectFactory.getAutowireStrategy(), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);

        objectFactory.setAutowireStrategy(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);

        sac.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) sac.getBean("bean");

        sac.registerPrototype("simple-action", SimpleAction.class, new MutablePropertyValues());

        ActionConfig actionConfig = new ActionConfig(null, "simple-action", new HashMap(), new HashMap(), null);
        SimpleAction simpleAction = (SimpleAction) objectFactory.buildBean(actionConfig.getClassName(), null);
        objectFactory.autoWireBean(simpleAction);
        assertEquals(simpleAction.getBean(), bean);
    }

    public void testShouldUseConstructorBasedInjectionWhenCreatingABeanFromAClassName() throws Exception {
        SpringObjectFactory factory = (SpringObjectFactory) ObjectFactory.getObjectFactory();
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());

        ConstructorBean bean = (ConstructorBean) factory.buildBean(ConstructorBean.class, null);

        assertNotNull("Bean should not be null", bean);
        assertNotNull("Action should have been added via DI", bean.getAction());
    }

    public void testFallBackToDefaultObjectFactoryWhenTheCConstructorDIIsAmbiguous() throws Exception {
        SpringObjectFactory factory = (SpringObjectFactory) ObjectFactory.getObjectFactory();
        sac.registerSingleton("firstActionBean", SimpleAction.class, new MutablePropertyValues());
        sac.registerSingleton("secondActionBean", SimpleAction.class, new MutablePropertyValues());

        ConstructorBean bean = (ConstructorBean) factory.buildBean(ConstructorBean.class, null);

        assertNotNull("Bean should have been created using default constructor", bean);
        assertNull("Not expecting this to have been set", bean.getAction());
    }

    public void testObjectFactoryUsesSpringObjectFactoryToCreateActions() throws Exception {
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());
        ActionConfig actionConfig = new ActionConfig();
        actionConfig.setClassName(ConstructorAction.class.getName());

        ConstructorAction action = (ConstructorAction) ObjectFactory.getObjectFactory().buildBean(actionConfig.getClassName(), null);

        assertNotNull("Bean should not be null", action);
        assertNotNull("Action should have been added via DI", action.getAction());
    }

    public void testShouldUseApplicationContextToApplyAspectsToGeneratedBeans() throws Exception {
        sac.registerSingleton("debugInterceptor", DebugInterceptor.class, new MutablePropertyValues());

        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("beanNames", new String[]{"*Action"});
        values.addPropertyValue("interceptorNames", new String[]{"debugInterceptor"});
        sac.registerSingleton("proxyFactory", BeanNameAutoProxyCreator.class, values);

        sac.refresh();

        ActionConfig actionConfig = new ActionConfig();
        actionConfig.setClassName(SimpleAction.class.getName());
        Action action = (Action) ObjectFactory.getObjectFactory().buildBean(actionConfig.getClassName(), null);

        assertNotNull("Bean should not be null", action);
        System.out.println("Action class is: " + action.getClass().getName());
        assertTrue("Action should have been advised", action instanceof Advised);
    }

    public static class ConstructorBean {
        private SimpleAction action;

        public ConstructorBean() {
            // Empty constructor
        }

        public ConstructorBean(SimpleAction action) {
            this.action = action;
        }

        public SimpleAction getAction() {
            return action;
        }
    }

    public static class ConstructorAction implements Action {
        private SimpleAction action;

        public ConstructorAction(SimpleAction action) {
            this.action = action;
        }

        public String execute() throws Exception {
            return SUCCESS;
        }

        public SimpleAction getAction() {
            return action;
        }
    }
}
