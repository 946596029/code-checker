
package org.example.code.checker.checker.markdown.task.checker.structrue.front.matter;

public class FrontMatter {

    private final String subcategory;
    private final String layout;
    private final String pageTitle;
    private final String description;

    public FrontMatter(
        String subcategory,
        String layout,
        String pageTitle,
        String description
    ) {
        this.subcategory = subcategory;
        this.layout = layout;
        this.pageTitle = pageTitle;
        this.description = description;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getLayout() {
        return layout;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "FrontMatter{"
            + "subcategory='" + subcategory + '\''
            + ", layout='" + layout + '\''
            + ", pageTitle='" + pageTitle + '\''
            + ", description='" + description + '\''
            + '}';
    }
}