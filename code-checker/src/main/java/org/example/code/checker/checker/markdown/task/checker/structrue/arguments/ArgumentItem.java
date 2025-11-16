package org.example.code.checker.checker.markdown.task.checker.structrue.arguments;

import java.util.Collections;
import java.util.List;

/**
 * Parsed single argument definition item, e.g.
 * <pre>
 * * `region` - (Optional, String, ForceNew) Specifies the region ...
 * </pre>
 */
public final class ArgumentItem {

    /**
     * Requirement flag parsed from the first token inside parentheses.
     * For example "Required" or "Optional". If it cannot be determined,
     * {@link #UNKNOWN} will be used.
     */
    public enum Requirement {
        REQUIRED,
        OPTIONAL,
        UNKNOWN
    }

    private final String name;
    private final Requirement requirement;
    private final String type;
    private final List<String> modifiers;
    private final String description;
    private final String raw;

    public ArgumentItem(
        String name,
        Requirement requirement,
        String type,
        List<String> modifiers,
        String description,
        String raw
    ) {
        this.name = name;
        this.requirement = requirement == null ? Requirement.UNKNOWN : requirement;
        this.type = type;
        this.modifiers = modifiers == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(modifiers);
        this.description = description;
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getType() {
        return type;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public String getDescription() {
        return description;
    }

    /**
     * The full, flattened text of the original list item, useful for debugging.
     */
    public String getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        return "ArgumentItem{"
            + "name='" + name + '\''
            + ", requirement=" + requirement
            + ", type='" + type + '\''
            + ", modifiers=" + modifiers
            + ", description='" + description + '\''
            + '}';
    }
}


