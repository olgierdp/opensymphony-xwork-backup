/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.XworkException;
import com.opensymphony.xwork.config.providers.XmlHelper;
import com.opensymphony.xwork.util.DomHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.*;


/**
 * This class serves 2 purpose :-
 * <ul>
 * <li>
 * Parse the validation config file. (eg. MyAction-validation.xml, MyAction-actionAlias-validation.xml)
 * to return a List of ValidatorConfig encapsulating the validator information.
 * </li>
 * <li>
 * Parse the validator definition file, (eg. validators.xml) that defines the {@link Validator}s
 * registered with XWork.
 * </li>
 * </ul>
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee 
 * @author Rob Harrop
 * @author Rene Gielen
 *
 * @see com.opensymphony.xwork.validator.ValidatorConfig
 *
 * @version $Date$ $Id$
 */
public class ValidatorFileParser {

    private static final Log LOG = LogFactory.getLog(ValidatorFileParser.class);


    static final String MULTI_TEXTVALUE_SEPARATOR = " ";

    static final Map VALIDATOR_CONFIG_DTD_MAPPINGS = new HashMap() {
        {
            put("-//OpenSymphony Group//XWork Validator 1.0//EN", "xwork-validator-1.0.dtd");
            put("-//OpenSymphony Group//XWork Validator 1.0.2//EN", "xwork-validator-1.0.2.dtd");
            put("-//OpenSymphony Group//XWork Validator 1.0.3//EN", "xwork-validator-1.0.3.dtd");    
        }
    };

    static final Map VALIDATOR_DEFINITION_DTD_MAPPINGS = new HashMap() {
        {
            put("-//OpenSymphony Group//XWork Validator Definition 1.0//EN", "xwork-validator-definition-1.0.dtd");    
        }
    };

    /**
     * Parse resource for a list of ValidatorConfig objects (configuring which validator(s) are
     * being applied to a particular field etc.)
     *
     * @param is input stream to the resource
     * @param resourceName file name of the resource
     * @return List list of ValidatorConfig
     */
    public static List parseActionValidatorConfigs(InputStream is, final String resourceName) {
        List validatorCfgs = new ArrayList();
        Document doc = null;

        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);
            

        doc = DomHelper.parse(in, VALIDATOR_CONFIG_DTD_MAPPINGS);

        if (doc != null) {
            NodeList fieldNodes = doc.getElementsByTagName("field");

            // BUG: xw-305: Let validator be parsed first and hence added to 
            // the beginning of list and therefore evaluated first, so short-circuting
            // it will not cause field-level validator to be fired.
            {NodeList validatorNodes = doc.getElementsByTagName("validator");
            addValidatorConfigs(validatorNodes, new HashMap(), validatorCfgs);}

            for (int i = 0; i < fieldNodes.getLength(); i++) {
                Element fieldElement = (Element) fieldNodes.item(i);
                String fieldName = fieldElement.getAttribute("name");
                Map extraParams = new HashMap();
                extraParams.put("fieldName", fieldName);

                NodeList validatorNodes = fieldElement.getElementsByTagName("field-validator");
                addValidatorConfigs(validatorNodes, extraParams, validatorCfgs);
            }
        }

        return validatorCfgs;
    }

    
    /**
     * Parses validator definitions
     *
     * @deprecated Use parseValidatorDefinitions(InputStream, String)
     * @param is The input stream
     */
    public static void parseValidatorDefinitions(InputStream is) {
        parseValidatorDefinitions(is, null);
    }
    
    
    /**
     * Parses validator definitions (register various validators with XWork).
     *
     * @since 1.2
     * @param is The input stream
     * @param resourceName The location of the input stream
     */
    public static void parseValidatorDefinitions(InputStream is, String resourceName) {
        
        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);
            
        Document doc = DomHelper.parse(in, VALIDATOR_DEFINITION_DTD_MAPPINGS);
        
        NodeList nodes = doc.getElementsByTagName("validator");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element validatorElement = (Element) nodes.item(i);
            String name = validatorElement.getAttribute("name");
            String className = validatorElement.getAttribute("class");

            try {
                // catch any problems here
                ObjectFactory.getObjectFactory().buildValidator(className, new HashMap(), null);
                ValidatorFactory.registerValidator(name, className);
            } catch (Exception e) {
                throw new XworkException("Unable to load validator class " + className, e, validatorElement);
            }
        }
    }

    private static void addValidatorConfigs(NodeList validatorNodes, Map extraParams, List validatorCfgs) {
        for (int j = 0; j < validatorNodes.getLength(); j++) {
            Element validatorElement = (Element) validatorNodes.item(j);
            String validatorType = validatorElement.getAttribute("type");
            Map params = new HashMap(extraParams);

            params.putAll(XmlHelper.getParams(validatorElement));

            // ensure that the type is valid...
            ValidatorFactory.lookupRegisteredValidatorType(validatorType);

            ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
            vCfg.setLocation(DomHelper.getLocationObject(validatorElement));

            vCfg.setShortCircuit(Boolean.valueOf(validatorElement.getAttribute("short-circuit")).booleanValue());

            NodeList messageNodes = validatorElement.getElementsByTagName("message");
            Element messageElement = (Element) messageNodes.item(0);

            final Node defaultMessageNode = messageElement.getFirstChild();
            String defaultMessage = (defaultMessageNode == null) ? "" : defaultMessageNode.getNodeValue();
            vCfg.setDefaultMessage(defaultMessage);

            Map messageParams = XmlHelper.getParams(messageElement);
            String key = messageElement.getAttribute("key");
            if ((key != null) && (key.trim().length() > 0)) {
                vCfg.setMessageKey(key);

                // Get the default message when pattern 2 is used. We are only interested in the
                // i18n message parameters when an i18n message key is specified.
                // pattern 1:
                // <message key="someKey">Default message</message>
                // pattern 2:
                // <message key="someKey">
                //     <param name="1">'param1'</param>
                //     <param name="2">'param2'</param>
                //     <param name="defaultMessage>The Default Message</param>
                // </message>

                if (messageParams.containsKey("defaultMessage")) {
                    vCfg.setDefaultMessage((String) messageParams.get("defaultMessage"));
                }

                // Sort the message param. those with keys as '1', '2', '3' etc. (numeric values)
                // are i18n message parameter, others are excluded.
                TreeMap sortedMessageParameters = new TreeMap();
                for (Iterator i = messageParams.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry messageParamEntry = (Map.Entry) i.next();
                    try {
                        int _order = Integer.parseInt((String) messageParamEntry.getKey());
                        sortedMessageParameters.put(new Integer(_order), messageParamEntry.getValue());
                    }
                    catch(NumberFormatException e) {
                        // ignore if its not numeric.
                    }
                }
                vCfg.setMessageParams((String[]) sortedMessageParameters.values().toArray(new String[0]));
            }
            else {
                if (messageParams != null && (messageParams.size() > 0)) {
                    // we are i18n message parameters defined but no i18n message,
                    // let's warn the user.
                    LOG.warn("validator of type ["+validatorType+"] have i18n message parameters defined but no i18n message key, it's parameters will be ignored");
                }
            }

            validatorCfgs.add(vCfg);
        }
    }
}
