package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Emphasis inline (single emphasis).
 */
public final class Emphasis extends StdNode {

    public Emphasis(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> children
    ) {
        super(nodeId, range, StandardNodeType.EMPHASIS, parentId, children);
    }
}