package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Horizontal rule.
 */
public final class ThematicBreak implements StdBlock {
	private final SourceRange range;

	public ThematicBreak(SourceRange range) {
		Objects.requireNonNull(range, "range");
		this.range = range;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


