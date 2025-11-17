package org.example.code.checker.checker.markdown.task.checker.structrue.attributes;

import java.util.Collections;
import java.util.List;

/**
 * Parsed "Attributes" section information.
 */
public final class AttributeList {

    private final String heading;
    private final String description;
    private final List<Attribute> items;

    public AttributeList(
        String heading,
        String description,
        List<Attribute> items
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

    public List<Attribute> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "AttributesInfo{"
            + "heading='" + heading + '\''
            + ", description='" + description + '\''
            + ", items=" + items
            + '}';
    }
}


