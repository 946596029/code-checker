package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * List block, can be ordered or unordered.
 */
public final class ListBlock extends StdNode {
	private final boolean ordered;
	private final int startNumber;

    public ListBlock(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> items,
        boolean ordered,
        int startNumber
    ) {
        super(nodeId, range, StandardNodeType.LIST_BLOCK, parentId, items);

        this.ordered = ordered;
        this.startNumber = Math.max(1, startNumber);
    }

	public boolean isOrdered() {
		return ordered;
	}

	public int getStartNumber() {
		return startNumber;
	}
}


