package com.opensymphony.xwork2.parameters.nodes;

public class IndexedNode implements Node {
    private String identifier;
    private Object index;

    public IndexedNode(String identifier, Object index) {
        this.identifier = identifier;
        this.index = index;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Object getIndex() {
        return index;
    }
}
