package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.List;
import java.util.Objects;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Heading block, levels typically in [1,6].
 */
public final class Heading extends StdNode {
	private final int level;

	public Heading(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> inlines,
        int level
    ) {
		super(nodeId, range, StandardNodeType.HEADING, parentId, inlines);

        if (level < 1) {
			throw new IllegalArgumentException("level must be >= 1");
		}
		Objects.requireNonNull(range, "range");

		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}