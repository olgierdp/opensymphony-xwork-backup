/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import com.opensymphony.xwork.xml.DelegatingHandler;
import com.opensymphony.xwork.xml.Path;

import org.xml.sax.ContentHandler;

import java.util.Map;


/**
 * SubHandler
 *
 * Created Oct 21, 2002 3:30:41 PM
 * @author Jason Carreira
 * @version 1.0
 */
public interface SubHandler extends ContentHandler {
    //~ Methods ////////////////////////////////////////////////////////////////

    void endingPath(Path path);

    void registerWith(DelegatingHandler rootHandler);

    void startingPath(Path path, Map attributeMap);

    void unregisterWith(DelegatingHandler rootHandler);
}
