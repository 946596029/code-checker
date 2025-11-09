package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.Objects;
import java.util.Optional;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Code block (fenced or indented). Language is optional.
 */
public final class CodeBlock implements StdBlock {
	private final String content;
	private final String language;
	private final SourceRange range;

	public CodeBlock(String content, String language, SourceRange range) {
		Objects.requireNonNull(content, "content");
		Objects.requireNonNull(range, "range");
		this.content = content;
		this.language = language;
		this.range = range;
	}

	public String getContent() {
		return content;
	}

	public Optional<String> getLanguage() {
		return Optional.ofNullable(language);
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


