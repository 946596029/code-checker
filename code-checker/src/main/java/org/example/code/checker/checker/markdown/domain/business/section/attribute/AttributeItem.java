package org.example.code.checker.checker.markdown.domain.business.section.attribute;

import org.example.code.checker.checker.markdown.domain.business.Section;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.List;

public class AttributeItem implements Section {

    private String name;
    private String description;
    private List<StdBlock> stdBlockList;

    private SourceRange range;

    @Override
    public SourceRange getRange() {
        return range;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setRange(SourceRange range) {
        this.range = range;
    }
}