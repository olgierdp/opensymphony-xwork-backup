package com.opensymphony.xwork2.parameters.nodes;

public class CollectionNode implements Node{
   private String identifier;
    private Object index;

    public CollectionNode(String identifier, Object index) {
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