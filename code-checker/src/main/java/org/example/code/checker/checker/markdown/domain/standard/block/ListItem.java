package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * List item containing block children.
 */
public final class ListItem extends StdNode {

    public ListItem(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> children
    ) {
        super(nodeId, range, StandardNodeType.LIST_ITEM, parentId, children);
    }
}


