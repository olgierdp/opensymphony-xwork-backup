/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * XML utilities.
 *
 * @author Mike
 */
public class XmlHelper {


    /**
     * This method will find all the parameters under this <code>paramsElement</code> and return them as
     * Map<String, String>. For example,
     * <pre>
     *   <result ... >
     *      <param name="param1">value1</param>
     *      <param name="param2">value2</param>
     *      <param name="param3">value3</param>
     *   </result>
     * </pre>
     * will returns a Map<String, String> with the following key, value pairs :-
     * <ul>
     * <li>param1 - value1</li>
     * <li>param2 - value2</li>
     * <li>param3 - value3</li>
     * </ul>
     *
     * @param paramsElement
     * @return
     */
    public static Map<String, String> getParams(Element paramsElement) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        if (paramsElement == null) {
            return params;
        }

        NodeList childNodes = paramsElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE) && "param".equals(childNode.getNodeName())) {
                Element paramElement = (Element) childNode;
                String paramName = paramElement.getAttribute("name");

                String val = getContent(paramElement);
                if (val.length() > 0) {
                    params.put(paramName, val);
                }
            }
        }

        return params;
    }

    /**
     * This method will return the content of this particular <code>element</code>.
     * For example,
     * <p/>
     * <pre>
     *    <result>something_1</result>
     * </pre>
     * When the {@link org.w3c.dom.Element} <code>&lt;result&gt;</code> is passed in as
     * argument (<code>element</code> to this method, it returns the content of it,
     * namely, <code>something_1</code> in the example above.
     *
     * @return
     */
    public static String getContent(Element element) {
        StringBuilder paramValue = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node currentNode = childNodes.item(j);
            if (currentNode != null &&
                    currentNode.getNodeType() == Node.TEXT_NODE) {
                String val = currentNode.getNodeValue();
                if (val != null) {
                    paramValue.append(val.trim());
                }
            }
        }
        return paramValue.toString().trim();
    }

    /**
     * Return the value of the "order" attribute from the root element
     */
     public static Integer getLoadOrder(Document doc) {
        Element rootElement = doc.getDocumentElement();
        String number = rootElement.getAttribute("order");
        if (StringUtils.isNotBlank(number)) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE;
            }
        } else {
            //no order specified
            return Integer.MAX_VALUE;
        }
    }
}
