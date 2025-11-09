package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * List block, can be ordered or unordered.
 */
public final class ListBlock implements StdBlock {
	private final boolean ordered;
	private final int startNumber;
	private final List<ListItem> items;
	private final SourceRange range;

	public ListBlock(boolean ordered, int startNumber, List<ListItem> items, SourceRange range) {
		Objects.requireNonNull(range, "range");
		this.ordered = ordered;
		this.startNumber = Math.max(1, startNumber);
		this.range = range;
		if (items == null || items.isEmpty()) {
			this.items = Collections.emptyList();
		} else {
			this.items = Collections.unmodifiableList(new ArrayList<>(items));
		}
	}

	public boolean isOrdered() {
		return ordered;
	}

	public int getStartNumber() {
		return startNumber;
	}

	public List<ListItem> getItems() {
		return items;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


