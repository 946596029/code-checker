package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Root document node, containing a list of block-level children.
 */
public final class Document implements StdBlock {
	private final List<StdBlock> children;
	private final SourceRange range;

	public Document(List<StdBlock> children, SourceRange range) {
		Objects.requireNonNull(range, "range");
		this.range = range;
		if (children == null || children.isEmpty()) {
			this.children = Collections.emptyList();
		} else {
			this.children = List.copyOf(children);
		}
	}

	public List<StdBlock> getChildren() {
		return children;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


