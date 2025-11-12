package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Inline code span.
 */
public final class CodeSpan extends StdNode {
	private final String code;

    public CodeSpan(
        String nodeId,
        SourceRange range,
        String parentId,
        String code
    ) {
        super(nodeId, range, StandardNodeType.CODE_SPAN, parentId, null);
        this.code = code;
    }

	public String getCode() {
		return code;
	}
}


