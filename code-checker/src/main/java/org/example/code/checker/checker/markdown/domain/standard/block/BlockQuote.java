package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Block quote containing block children.
 */
public final class BlockQuote extends StdNode {

    public BlockQuote(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> children
    ) {
        super(nodeId, range, StandardNodeType.BLOCK_QUOTE, parentId, children);
    }
}