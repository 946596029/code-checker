package org.example.code.checker.checker.markdown.task.structure.arguments;

import java.util.List;

public class ArgumentList {

    private String title;
    private String description;
    private List<Argument> arguments;

    public ArgumentList(String title, String description, List<Argument> arguments) {
        this.title = title;
        this.description = description;
        this.arguments = arguments;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Argument> getArguments() {
        return arguments;
    }
}