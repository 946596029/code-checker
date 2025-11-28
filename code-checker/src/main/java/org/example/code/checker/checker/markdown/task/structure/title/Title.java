package org.example.code.checker.checker.markdown.task.structure.title;

import java.util.List;

public class Title {
    
    private String title;
    private String description;
    private List<String> otherParagraphs;

    public Title(String title, String description, List<String> otherParagraphs) {
        this.title = title;
        this.description = description;
        this.otherParagraphs = otherParagraphs;
    }

    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }

    public List<String> getOtherParagraphs() {
        return otherParagraphs;
    }
}
