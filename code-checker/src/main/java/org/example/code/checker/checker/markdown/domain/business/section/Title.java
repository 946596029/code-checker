package org.example.code.checker.checker.markdown.domain.business.section;

import org.example.code.checker.checker.markdown.domain.business.Section;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.List;

public class Title implements Section {

    private Heading heading;
    private List<StdBlock> stdBlockList;
    private SourceRange range;

    @Override
    public SourceRange getRange() { return range; }

    public void setRange(SourceRange range) {
        this.range = range;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public List<StdBlock> getStdBlockList() {
        return stdBlockList;
    }

    public void setStdBlockList(List<StdBlock> stdBlockList) {
        this.stdBlockList = stdBlockList;
    }

    public static Title of(Document document) {
        Title title = new Title();

        return null;
    }
}