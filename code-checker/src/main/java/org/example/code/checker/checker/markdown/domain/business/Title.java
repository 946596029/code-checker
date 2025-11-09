package org.example.code.checker.checker.markdown.domain.business;

import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Document-level title built from the first level-1 heading.
 */
public final class Title implements Locatable {
	private final Heading source;
	private final String text;

	public Title(Heading source, String text) {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(text, "text");
		this.source = source;
		this.text = text;
	}

	public Heading getSource() {
		return source;
	}

	public String getText() {
		return text;
	}

	@Override
	public SourceRange getRange() {
		return source.getRange();
	}
}