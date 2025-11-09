package org.example.code.checker.checker.markdown.domain.business.section;

import org.example.code.checker.checker.markdown.domain.business.Section;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

public class FrontMatter implements Section {

    private String subcategory;
    private String layout;
    private String pageTitle;
    private String description;
    private SourceRange range;

    @Override
    public SourceRange getRange() {
        return range;
    }

    public void setRange(SourceRange range) {
        this.range = range;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}