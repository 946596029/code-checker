package org.example.code.checker.checker.markdown.task.checker.structrue.arguments;

import java.util.Collections;
import java.util.List;

/**
 * Parsed "Arguments" section information.
 */
public final class ArgumentsInfo {

    private final String heading;
    private final String description;
    private final List<ArgumentItem> items;

    public ArgumentsInfo(
        String heading,
        String description,
        List<ArgumentItem> items
    ) {
        this.heading = heading;
        this.description = description;
        this.items = items == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(items);
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public List<ArgumentItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "ArgumentsInfo{"
            + "heading='" + heading + '\''
            + ", description='" + description + '\''
            + ", items=" + items
            + '}';
    }
}


