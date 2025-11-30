package org.example.code.checker.checker.markdown.task.structure.attributes;

import java.util.List;

public class Attribute {    

    private String name;
    private List<String> tags;
    private String description;

    public Attribute(String name, List<String> tags, String description) {
        this.name = name;
        this.tags = tags;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }
}