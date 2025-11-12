package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Plain text inline node.
 */
public final class Text extends StdNode {
	private final String content;

    public Text(
        String nodeId,
        SourceRange range,
        String parentId,
        String content
    ) {
        super(nodeId, range, StandardNodeType.TEXT, parentId, null);
        this.content = content;
    }

	public String getContent() {
		return content;
	}
}


