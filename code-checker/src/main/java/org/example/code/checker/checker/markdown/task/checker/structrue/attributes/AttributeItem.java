package org.example.code.checker.checker.markdown.task.checker.structrue.attributes;

import java.util.Collections;
import java.util.List;

/**
 * Parsed single attribute definition item, typically from the
 * "Attribute Reference" section.
 */
public final class AttributeItem {

    private final String name;
    private final String description;
    private final List<String> modifiers;
    private final String raw;

    public AttributeItem(
        String name,
        String description,
        List<String> modifiers,
        String raw
    ) {
        this.name = name;
        this.description = description;
        this.modifiers = modifiers == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(modifiers);
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    /**
     * The full, flattened text of the original list item, useful for debugging.
     */
    public String getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        return "AttributeItem{"
            + "name='" + name + '\''
            + ", description='" + description + '\''
            + ", modifiers=" + modifiers
            + '}';
    }
}


