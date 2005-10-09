package com.opensymphony.xwork.util;

import com.uwyn.rife.continuations.ContinuationConfig;

/**
 * User: patrick
 * Date: Sep 22, 2005
 * Time: 7:39:30 AM
 */
public class XWorkContinuationConfig extends ContinuationConfig {
    public static final String CONTINUE_PARAM = "continue";
    public static final String CONTINUE_KEY = "__continue";

    public String getContinuableClassInternalName() {
        return "com.opensymphony.xwork.ActionSupport";
    }

    public String getContinuableInterfaceInternalName() {
        return "com.opensymphony.xwork.Action";
    }

    public String getEntryMethod() {
        return "execute()Ljava/lang/String;";
    }

    public String getContinuableClassOrInterfaceName() {
        return "com.opensymphony.xwork.ActionSupport";
    }

    public String getPauseSignature() {
        return "(Ljava/lang/String;)V";
    }
}
