/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.*;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * ValidatorFileParser
 *
 * Created : Jan 20, 2003 1:30:53 PM
 *
 * @author Jason Carreira
 */
public class ValidatorFileParser {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(ValidatorFileParser.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public static List parseActionValidators(InputStream is) {
        List validators = new ArrayList();
        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            dbf.setNamespaceAware(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {
                    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                        if ("-//OpenSymphony Group//XWork Validator 1.0//EN".equals(publicId)) {
                            ClassLoader loader = Thread.currentThread().getContextClassLoader();

                            return new InputSource(loader.getResourceAsStream("xwork-validator-1.0.dtd"));
                        }

                        return null;
                    }
                });
            builder.setErrorHandler(new ErrorHandler() {
                    public void warning(SAXParseException exception) throws SAXException {
                        log.warn(exception.getMessage() + " at (" + exception.getLineNumber() + ":" + exception.getColumnNumber() + ")");
                    }

                    public void error(SAXParseException exception) throws SAXException {
                        log.error(exception.getMessage() + " at (" + exception.getLineNumber() + ":" + exception.getColumnNumber() + ")");
                    }

                    public void fatalError(SAXParseException exception) throws SAXException {
                        log.fatal(exception.getMessage() + " at (" + exception.getLineNumber() + ":" + exception.getColumnNumber() + ")");
                    }
                });
            doc = builder.parse(is);
        } catch (Exception e) {
            log.fatal("Caught exception while attempting to load validation configuration file.", e);
        }

        if (doc != null) {
            NodeList fieldNodes = doc.getElementsByTagName("field");

            for (int i = 0; i < fieldNodes.getLength(); i++) {
                Element fieldElement = (Element) fieldNodes.item(i);
                String fieldName = fieldElement.getAttribute("name");
                Map extraParams = new HashMap();
                extraParams.put("fieldName", fieldName);

                NodeList validatorNodes = fieldElement.getElementsByTagName("field-validator");
                addValidators(validatorNodes, extraParams, validators);
            }

            NodeList validatorNodes = doc.getElementsByTagName("validator");
            addValidators(validatorNodes, new HashMap(), validators);
        }

        return validators;
    }

    public static void parseValidatorDefinitions(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList nodes = doc.getElementsByTagName("validator");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element validatorElement = (Element) nodes.item(i);
                String name = validatorElement.getAttribute("name");
                String className = validatorElement.getAttribute("class");

                try {
                    // catch any problems here
                    ObjectFactory.getObjectFactory().buildValidator(className, new HashMap());
                    ValidatorFactory.registerValidator(name, className);
                } catch (Exception e) {
                    log.error("Unable to load validator class " + className);
                }
            }
        } catch (Exception e) {
            log.error("Caught exception while parsing validator definitions.");
        }
    }

    private static void addValidators(NodeList validatorNodes, Map extraParams, List validators) {
        for (int j = 0; j < validatorNodes.getLength(); j++) {
            Element validatorElement = (Element) validatorNodes.item(j);
            String validatorType = validatorElement.getAttribute("type");
            Map params = new HashMap(extraParams);
            NodeList paramNodes = validatorElement.getElementsByTagName("param");

            for (int k = 0; k < paramNodes.getLength(); k++) {
                Element paramElement = (Element) paramNodes.item(k);
                String paramName = paramElement.getAttribute("name");
                String paramValue = paramElement.getFirstChild().getNodeValue();
                params.put(paramName, paramValue);
            }

            Validator validator = ValidatorFactory.getValidator(validatorType, params);

            if (validator instanceof ShortCircuitingValidator) {
                ((ShortCircuitingValidator) validator).setShortCircuit(Boolean.valueOf(validatorElement.getAttribute("short-circuit")).booleanValue());
            }

            NodeList messageNodes = validatorElement.getElementsByTagName("message");
            Element messageElement = (Element) messageNodes.item(0);
            String key = messageElement.getAttribute("key");

            if ((key != null) && (key.trim().length() > 0)) {
                validator.setMessageKey(key);
            }

            final Node defaultMessageNode = messageElement.getFirstChild();
            String defaultMessage = (defaultMessageNode == null) ? "" : defaultMessageNode.getNodeValue();
            validator.setDefaultMessage(defaultMessage);
            validators.add(validator);
        }
    }
}
