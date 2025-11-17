package org.example.code.checker.checker.markdown.task.checker.structrue.arguments;

import org.example.code.checker.checker.markdown.domain.StdNode;

import java.util.Collections;
import java.util.List;

/**
 * Parsed single argument definition item, e.g.
 * <pre>
 * * `region` - (Optional, String, ForceNew) Specifies the region ...
 * </pre>
 */
public final class Argument {

    private final String name;
    private final List<String> modifiers;
    private final String description;
    private final List<StdNode> blockChildren;
    private final String raw;

    public Argument(
        String name,
        List<String> modifiers,
        String description,
        List<StdNode> blockChildren,
        String raw
    ) {
        this.name = name;
        this.modifiers = modifiers == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(modifiers);
        this.description = description;
        this.blockChildren = blockChildren == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(blockChildren);
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public String getDescription() {
        return description;
    }

    public List<StdNode> getBlockChildren() { return blockChildren; }

    /**
     * The full, flattened text of the original list item, useful for debugging.
     */
    public String getRaw() {
        return raw;
    }
}


