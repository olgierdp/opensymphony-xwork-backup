/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.util.reflection.ReflectionProvider;


/**
 * GenericsObjectTypeDeterminer
 *
 * @author Patrick Lightbody
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * 
 * @deprecated Use DefaultObjectTypeDeterminer instead. Since XWork 2.0.4 the DefaultObjectTypeDeterminer handles the
 *             annotation processing.
 */
@Deprecated public class GenericsObjectTypeDeterminer extends DefaultObjectTypeDeterminer {

    public GenericsObjectTypeDeterminer(XWorkConverter conv,
            XWorkBasicConverter basicConv, ReflectionProvider prov) {
        super(conv, prov);
    }
}
