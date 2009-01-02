package com.opensymphony.xwork2.mvel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MVELContext extends LinkedHashMap<String, Object> {
    public MVELContext(Map<String, Object> context) {
        super.putAll(context);
    }

    public MVELContext() {
    }
}
