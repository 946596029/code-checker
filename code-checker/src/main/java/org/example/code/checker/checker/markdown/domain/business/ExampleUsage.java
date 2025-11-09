package org.example.code.checker.checker.markdown.domain.business;

import java.util.Objects;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Example usage snippet in the document.
 */
public final class ExampleUsage implements Locatable {
	private final String name;
	private final String code;
	private final SourceRange range;

	public ExampleUsage(String name, String code, SourceRange range) {
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(code, "code");
		Objects.requireNonNull(range, "range");
		this.name = name;
		this.code = code;
		this.range = range;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}
