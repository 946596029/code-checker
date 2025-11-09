package org.example.code.checker.checker.markdown.domain.business.section.argument;

import org.example.code.checker.checker.markdown.domain.business.Section;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.List;

public class ArgumentItem implements Section {
    private String name;
    private List<String> tags;
    private String description;
    private SourceRange range;

    @Override
    public SourceRange getRange() {
        return range;
    }

    public void setRange(SourceRange range) {
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}