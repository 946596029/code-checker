package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Strong emphasis (bold) inline.
 */
public final class Strong implements StdInline {
	private final List<StdInline> children;
	private final SourceRange range;

	public Strong(List<StdInline> children, SourceRange range) {
		this.range = range;
		if (children == null || children.isEmpty()) {
			this.children = Collections.emptyList();
		} else {
			this.children = Collections.unmodifiableList(new ArrayList<>(children));
		}
	}

	public List<StdInline> getChildren() {
		return children;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


