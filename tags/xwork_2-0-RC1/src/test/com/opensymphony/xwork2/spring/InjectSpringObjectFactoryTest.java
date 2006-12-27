/*
 * Created on Jun 29, 2004
 */
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test that Spring correctly calls the "init" method of the
 * {@link com.opensymphony.xwork2.spring.SpringObjectFactory}
 * 
 * @author Simon Stewart
 */
public class InjectSpringObjectFactoryTest extends XWorkTestCase {

    public void testSpringShouldCallInitMethod() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "com/opensymphony/xwork2/spring/autowireContext.xml");

        ObjectFactory factory = ObjectFactory.getObjectFactory();

        assertTrue(
                "ObjectFactory should be an instance of SpringObjectFactory",
                factory instanceof SpringObjectFactory);
    }
}
