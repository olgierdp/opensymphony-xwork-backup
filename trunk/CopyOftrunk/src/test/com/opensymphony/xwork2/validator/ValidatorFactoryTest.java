package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.util.reflection.ReflectionProviderFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * <code>ValidatorFactoryTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: $
 */
public class ValidatorFactoryTest extends XWorkTestCase {

    public void testParseValidators() {
        try {
            Class c = Class.forName("com.opensymphony.xwork2.validator.ValidatorFactory");
            Constructor constructor = c.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            
            assertNotNull(c);
            Field field = c.getDeclaredField("validators");
            field.setAccessible(true);
            Map validators = (Map) field.get(null);
            assertNotNull(validators);
            assertNotNull(validators.get("requiredAdditional"));
            assertNotNull(validators.get("requiredAnother"));
            assertNotNull(validators.get("regex"));

            // There should be 15 validators at all: 
            // 13 from default.xml overwritten by validators.xml
            //  1 from my-Validators.xml
            //  1 from myOther-validators.xml
            assertEquals(17, validators.size());
        } catch (Exception e) {
            fail("We shouldn't get here...");
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new MockConfigurationProvider());
    }

}
