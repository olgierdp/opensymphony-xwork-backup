/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * ValidatorFactory
 * 
 * <p>
 * <!-- START SNIPPET: javadoc -->
 * Validation rules are handled by validators, which must be registered with 
 * the ValidatorFactory (using the registerValidator method). The simplest way to do so is to add a file name 
 * validators.xml in the root of the classpath (/WEB-INF/classes) that declares 
 * all the validators you intend to use. 
 * <!-- END SNIPPET: javadoc -->
 * </p>
 *
 * 
 * <p>
 * <b>INFORMATION</b>
 * <!-- START SNIPPET: information -->
 * validators.xml if being defined should be available in the classpath. However 
 * this is not necessary, if no custom validator is needed. Webwork will automatically 
 * picked up a predefined sets of validators defined in 
 * com/opensymphony/xwork/validator/validators/default.xml packaged together 
 * in xwork jar file that comes with webwork distribution. See ValidatorFactory 
 * static block for details.
 * <!-- END SNIPPET: information -->
 * </p>
 * 
 * <p>
 * <b>WARNING</b>
 * <!-- START SNIPPET: warning -->
 * If custom validator is being defined and a validators.xml is created and 
 * place in the classpath, do remember to copy all the other pre-defined validators 
 * that is needed into the validators.xml as if not they will not be registered. 
 * Once a validators.xml is detected in the classpath, the default one 
 * (com/opensymphony/xwork/validator/validators/default.xml) will not be loaded. 
 * It is only loaded when a custom validators.xml cannot be found in the classpath.
 *  Be careful.
 * <!-- END SNIPPET: warning -->
 * </p>
 * 
 * <p>
 * <!-- START SNIPPET: turnOnValidators -->
 * <b>Note:</b> The default validationWorkflowStack already includes this.<br/>
 * All that is required to enable validation for an Action is to put the 
 * ValidationInterceptor in the interceptor refs of the action (see xwork.xml) like so:
 * <!-- END SNIPPET: turnOnValidators -->
 * </p>
 * 
 * <pre>
 * <!-- START SNIPPET: exTurnOnValidators -->
 *     &lt;interceptor name="validator" class="com.opensymphony.xwork.validator.ValidationInterceptor"/&gt;
 * <!-- END SNIPPET: exTurnOnValidators -->
 * </pre>
 * 
 * <p><b>Field Validators</b>
 * <!-- START SNIPPET: fieldValidators -->
 * Field validators, as the name indicate, act on single fields accessible through an action. 
 * A validator, in contrast, is more generic and can do validations in the full action context, 
 * involving more than one field (or even no field at all) in validation rule.
 * Most validations can be defined on per field basis. This should be preferred over 
 * non-field validation whereever possible, as field validator messages are bound to the 
 * related field and will be presented next to the corresponding input element in the 
 * respecting view.
 * <!-- END SNIPPET: fieldValidators -->
 * </p>
 * 
 * <p><b>Non Field Validators</b>
 * <!-- START SNIPPET: nonFieldValidators -->
 * Non-field validators only add action level messages. Non-field validators 
 * are mostly domain specific and therefore often custom implementations. 
 * The most important standard non-field validator provided by XWork/WebWork 
 * is ExpressionValidator.
 * <!-- END SNIPPET: nonFieldValidators -->
 * </p>
 * 
 * <p><b>NOTE:</b>
 * <!-- START SNIPPET: validatorsNote -->
 * Non-field validators takes precedence over field validators 
 * regardless of the order they are defined in *-validation.xml. If a non-field 
 * validator is short-circuited, it will causes its non-field validator to not 
 * being executed. See validation framework documentation for more info.
 * <!-- END SNIPPET: validatorsNote -->
 * </p>
 * 
 * <p><b>VALIDATION RULES:</b>
 * <!-- START SNIPPET: validationRules1 -->
 * Validation rules can be specified:
 * <ol>
 *  <li> Per Action class: in a file named ActionName-validation.xml</li>
 *  <li> Per Action alias: in a file named ActionName-alias-validation.xml</li>
 *  <li> Inheritance hierarchy and interfaces implemented by Action class: 
 *  WebWork searches up the inheritance tree of the action to find default 
 *  validations for parent classes of the Action and interfaces implemented</li>
 * </ol>
 * Here is an example for SimpleAction-validation.xml:
 * <!-- END SNIPPET: validationRules1 -->
 * <p>
 * 
 * <pre>
 * <!-- START SNIPPET: exValidationRules1 -->
 * &lt;!DOCTYPE validators PUBLIC "-//OpenSymphony Group//XWork Validator 1.0//EN"
 *        "http://www.opensymphony.com/xwork/xwork-validator-1.0.dtd"&gt;
 * &lt;validators&gt;
 *   &lt;field name="bar"&gt;
 *       &lt;field-validator type="required"&gt;
 *           &lt;message&gt;You must enter a value for bar.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *       &lt;field-validator type="int"&gt;
 *           &lt;param name="min">6&lt;/param&gt;
 *           &lt;param name="max"&gt;10&lt;/param&gt;
 *           &lt;message&gt;bar must be between ${min} and ${max}, current value is ${bar}.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;field name="bar2"&gt;
 *       &lt;field-validator type="regex"&gt;
 *           &lt;param name="regex"&gt;[0-9],[0-9]&lt;/param&gt;
 *           &lt;message&gt;The value of bar2 must be in the format "x, y", where x and y are between 0 and 9&lt;/message&gt;
 *      &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;field name="date"&gt;
 *       &lt;field-validator type="date"&gt;
 *           &lt;param name="min"&gt;12/22/2002&lt;/param&gt;
 *           &lt;param name="max"&gt;12/25/2002&lt;/param&gt;
 *           &lt;message&gt;The date must be between 12-22-2002 and 12-25-2002.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;field name="foo"&gt;
 *       &lt;field-validator type="int"&gt;
 *           &lt;param name="min"&gt;0&lt;/param&gt;
 *           &lt;param name="max"&gt;100&lt;/param&gt;
 *           &lt;message key="foo.range"&gt;Could not find foo.range!&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;validator type="expression"&gt;
 *       &lt;param name="expression"&lt;foo &lt; bar&lt;/param&gt;
 *       &lt;message&gt;Foo must be greater than Bar. Foo = ${foo}, Bar = ${bar}.&lt;/message&gt;
 *   &lt;/validator&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: exValidationRules1 -->
 * </pre>
 * 
 * 
 * <p>
 * <!-- START SNIPPET: validationRules2 -->
 * Here we can see the configuration of validators for the SimpleAction class. 
 * Validators (and field-validators) must have a type attribute, which refers 
 * to a name of an Validator registered with the ValidatorFactory as above. 
 * Validator elements may also have <param> elements with name and value attributes 
 * to set arbitrary parameters into the Validator instance. See below for discussion 
 * of the message element.
 * <!-- END SNIPPET: validationRules2 -->
 * </p>
 * 
 * 
 * <p>
 * <!-- START SNIPPET: validationRules3 -->
 * Each Validator or Field-Validator element must define one message element inside 
 * the validator element body. The message element has 1 attributes, key which is not 
 * required. The body of the message tag is taken as the default message which should 
 * be added to the Action if the validator fails.Key gives a message key to look up 
 * in the Action's ResourceBundles using getText() from LocaleAware if the Action 
 * implements that interface (as ActionSupport does). This provides for Localized 
 * messages based on the Locale of the user making the request (or whatever Locale 
 * you've set into the LocaleAware Action). After either retrieving the message from 
 * the ResourceBundle using the Key value, or using the Default message, the current 
 * Validator is pushed onto the ValueStack, then the message is parsed for ${...} 
 * sections which are replaced with the evaluated value of the string between the 
 * ${ and }. This allows you to parameterize your messages with values from the 
 * Validator, the Action, or both. Here is an example of a parameterized message:
 * This will pull the min and max parameters from the IntRangeFieldValidator and 
 * the value of bar from the Action.
 * <!-- END SNIPPET: validationRules3 -->
 * </p>
 * 
 * <pre>
 * <!-- START SNIPPET: exValidationRules3 -->
 *    bar must be between ${min} and ${max}, current value is ${bar}.
 * <!-- END SNIPPET: exValidationRules3 -->
 * </pre>
 * 
 * @version $Date$ $Id$
 * @author Jason Carreira
 * @author James House
 */
public class ValidatorFactory {

    private static Map validators = new HashMap();
    private static Log LOG = LogFactory.getLog(ValidatorFactory.class);

    static {
        parseValidators();
    }


    private ValidatorFactory() {
    }


    /**
     * Get a Validator that matches the given configuration.
     */
    public static Validator getValidator(ValidatorConfig cfg) {

        String className = lookupRegisteredValidatorType(cfg.getType());

        Validator validator;

        try {
            // instantiate the validator, and set configured parameters
            //todo - can this use the ThreadLocal?
            validator = ObjectFactory.getObjectFactory().buildValidator(className, cfg.getParams(), null); // ActionContext.getContext().getContextMap());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("There was a problem creating a Validator of type " + className);
        }

        // set other configured properties
        validator.setMessageKey(cfg.getMessageKey());
        validator.setDefaultMessage(cfg.getDefaultMessage());
        if (validator instanceof ShortCircuitableValidator) {
            ((ShortCircuitableValidator) validator).setShortCircuit(cfg.isShortCircuit());
        }

        return validator;
    }

    public static void registerValidator(String name, String className) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering validator of class " + className + " with name " + name);
        }

        validators.put(name, className);
    }

    public static String lookupRegisteredValidatorType(String name) {
        // lookup the validator class mapped to the type name
        String className = (String) validators.get(name);

        if (className == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + name);
        }

        return className;
    }

    private static void parseValidators() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading validator definitions.");
        }

        InputStream is = ClassLoaderUtil.getResourceAsStream("validators.xml", ValidatorFactory.class);
        if (is == null) {
            is = ClassLoaderUtil.getResourceAsStream("com/opensymphony/xwork/validator/validators/default.xml",
                    ValidatorFactory.class);
        }

        if (is != null) {
            ValidatorFileParser.parseValidatorDefinitions(is);
        }
    }
}
