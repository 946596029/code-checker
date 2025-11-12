package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Horizontal rule.
 */
public final class ThematicBreak extends StdNode {

	public ThematicBreak(
            String nodeId,
            SourceRange range,
            String parentId
    ) {
        super(nodeId, range, StandardNodeType.THEMATIC_BREAK, parentId, null);
	}
}


