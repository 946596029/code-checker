package org.example.code.checker.checker.markdown.domain.business;

import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.List;
import java.util.Objects;

public class Feature implements Section {

    private List<Section> sectionList;
    private SourceRange range;

    @Override
    public SourceRange getRange() { return range; }

    public void setRange(SourceRange range) {
        this.range = range;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
        this.range = null;
    }
}