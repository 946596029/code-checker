package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Inline code span.
 */
public final class CodeSpan implements StdInline {
	private final String code;
	private final SourceRange range;

	public CodeSpan(String code, SourceRange range) {
		Objects.requireNonNull(code, "code");
		Objects.requireNonNull(range, "range");
		this.code = code;
		this.range = range;
	}

	public String getCode() {
		return code;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


