package org.example.code.checker.checker.markdown.task.checker.structrue.attributes;

import org.example.code.checker.checker.markdown.domain.StdNode;

import java.util.Collections;
import java.util.List;

/**
 * Parsed single attribute definition item, typically from the
 * "Attribute Reference" section.
 */
public final class Attribute {

    private final String name;
    private final List<String> modifiers;
    private final String description;
    private final List<StdNode> blockChildren;
    private final String raw;

    public Attribute(
        String name,
        String description,
        List<String> modifiers,
        List<StdNode> blockChildren,
        String raw
    ) {
        this.name = name;
        this.description = description;
        this.modifiers = modifiers == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(modifiers);
        this.blockChildren = blockChildren == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(blockChildren);
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

    public List<StdNode> getBlockChildren() { return blockChildren; }

    /**
     * The full, flattened text of the original list item, useful for debugging.
     */
    public String getRaw() {
        return raw;
    }
}


