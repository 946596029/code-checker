package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Root document node, containing a list of block-level children.
 */
public final class Document extends StdNode {

    public Document(String nodeId) {
        super(nodeId, null, StandardNodeType.DOCUMENT, null, null);
    }

	public Document(
        String nodeId,
        SourceRange range,
        List<StdNode> children
    ) {
        super(nodeId, range, StandardNodeType.DOCUMENT, null, children);
	}
}


