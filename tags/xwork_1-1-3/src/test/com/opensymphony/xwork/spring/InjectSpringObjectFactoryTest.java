/*
 * Created on Jun 29, 2004
 */
package com.opensymphony.xwork.spring;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.XWorkTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test that Spring correctly calls the "init" method of the
 * {@link com.opensymphony.xwork.spring.SpringObjectFactory}
 * 
 * @author Simon Stewart
 */
public class InjectSpringObjectFactoryTest extends XWorkTestCase {

    public void testSpringShouldCallInitMethod() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "com/opensymphony/xwork/spring/autowireContext.xml");

        ObjectFactory factory = ObjectFactory.getObjectFactory();

        assertTrue(
                "ObjectFactory should be an instance of SpringObjectFactory",
                factory instanceof SpringObjectFactory);
    }
}
