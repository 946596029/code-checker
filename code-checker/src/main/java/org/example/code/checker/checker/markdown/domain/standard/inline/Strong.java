package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Strong emphasis (bold) inline.
 */
public final class Strong extends StdNode {

    public Strong(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> children
    ) {
        super(nodeId, range, StandardNodeType.STRONG, parentId, children);
    }
}


