package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.MdDomainVisitor;
import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;
import org.example.code.checker.checker.utils.TreeNode;

/**
 * Inline code span.
 */
public class CodeSpan extends MdDomain {
	private String code;

    public CodeSpan(
        StandardNodeType nodeType,
        boolean isBlock,
        SourceRange range,
        String code
    ) {
        super(nodeType, isBlock, range);
        this.code = code;
    }

    @Override
    public <R> R accept(MdDomainVisitor<R> visitor) {
        return visitor.visit(this);
    }

	public String getCode() {
		return code;
	}
}

