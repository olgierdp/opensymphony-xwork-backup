/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;


/**
 * User: Mike
 * Date: May 6, 2003
 * Time: 12:12:03 PM
 */
public class XmlHelper {

    public static Map getParams(Element paramsElement) {
        LinkedHashMap params = new LinkedHashMap();

        if (paramsElement == null) {
            return params;
        }

        NodeList childNodes = paramsElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE) && "param".equals(childNode.getNodeName())) {
                Element paramElement = (Element) childNode;
                String paramName = paramElement.getAttribute("name");

                if (paramElement.getChildNodes().item(0) != null) {
                    String paramValue = paramElement.getChildNodes().item(0).getNodeValue();
                    if (paramValue != null) paramValue = paramValue.trim();
                    params.put(paramName, paramValue);
                }
            }
        }

        return params;
    }
}
