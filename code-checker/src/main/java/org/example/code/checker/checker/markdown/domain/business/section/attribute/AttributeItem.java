package org.example.code.checker.checker.markdown.domain.business.section.attribute;

import org.example.code.checker.checker.markdown.domain.business.Section;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.List;

public class AttributeItem implements Section {

    private Paragraph detail;
    private List<StdBlock> stdBlockList;

    private SourceRange range;

    @Override
    public SourceRange getRange() {
        return range;
    }

    public void setRange(SourceRange range) {
        this.range = range;
    }


}