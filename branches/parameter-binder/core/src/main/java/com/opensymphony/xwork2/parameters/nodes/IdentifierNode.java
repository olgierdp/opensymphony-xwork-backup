package com.opensymphony.xwork2.parameters.nodes;

public class IdentifierNode implements Node{
    private String identifier;

    public IdentifierNode(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
