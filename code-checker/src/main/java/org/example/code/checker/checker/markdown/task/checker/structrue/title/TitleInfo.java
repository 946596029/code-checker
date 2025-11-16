package org.example.code.checker.checker.markdown.task.checker.structrue.title;

import java.util.Collections;
import java.util.List;

/**
 * Parsed title information for a markdown document.
 */
public final class TitleInfo {

    private final String title;
    private final String description;
    /**
     * Optional extra paragraphs that appear after the main title description,
     * for example additional tips or notes. Can be empty.
     */
    private final List<String> extraParagraphs;

    public TitleInfo(String title, String description) {
        this(title, description, Collections.emptyList());
    }

    public TitleInfo(String title, String description, List<String> extraParagraphs) {
        this.title = title;
        this.description = description;
        this.extraParagraphs = extraParagraphs == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(extraParagraphs);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExtraParagraphs() {
        return extraParagraphs;
    }

    @Override
    public String toString() {
        return "TitleInfo{"
            + "title='" + title + '\''
            + ", description='" + description + '\''
            + ", extraParagraphs=" + extraParagraphs
            + '}';
    }
}


