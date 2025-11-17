package org.example.code.checker.checker.markdown.task.checker.structrue.arguments;

import org.example.code.checker.checker.markdown.domain.StdNode;

import java.util.Collections;
import java.util.List;

/**
 * Parsed "Arguments" section information.
 */
public final class ArgumentList {

    private final String heading;
    private final String description;
    private final List<Argument> items;

    public ArgumentList(
        String heading,
        String description,
        List<Argument> items
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

    public List<Argument> getItems() {
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


