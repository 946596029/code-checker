package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Plain text inline node.
 */
public final class Text implements StdInline {
	private final String content;
	private final SourceRange range;

	public Text(String content, SourceRange range) {
		Objects.requireNonNull(content, "content");
		Objects.requireNonNull(range, "range");
		this.content = content;
		this.range = range;
	}

	public String getContent() {
		return content;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


