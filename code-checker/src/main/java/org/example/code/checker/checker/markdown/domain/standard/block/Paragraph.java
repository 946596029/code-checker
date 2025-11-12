package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Paragraph block, containing inline children.
 */
public final class Paragraph extends StdNode {

    public Paragraph(
        String nodeId,
        SourceRange range,
        String parentId,
        List<StdNode> inlines
    ) {
        super(nodeId, range, StandardNodeType.PARAGRAPH, parentId, inlines);
    }
}


