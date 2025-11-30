package org.example.code.checker.checker.markdown.task.structure.attributes;

import java.util.List;

public class AttributeList {

    private String title;
    private String description;
    private List<Attribute> attributes;

    public AttributeList(String title, String description, List<Attribute> attributes) {
        this.title = title;
        this.description = description;
        this.attributes = attributes;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}