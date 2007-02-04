package com.opensymphony.xwork;

public class ProxyInvocationAction extends ActionSupport implements ProxyInvocationInterface {
    public String show() {
        return "proxyResult";
    }
}
