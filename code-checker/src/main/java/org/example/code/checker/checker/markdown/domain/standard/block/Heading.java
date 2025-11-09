package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Heading block, levels typically in [1,6].
 */
public final class Heading implements StdBlock {
	private final int level;
	private final List<StdInline> inlines;
	private final SourceRange range;

	public Heading(int level, List<StdInline> inlines, SourceRange range) {
		if (level < 1) {
			throw new IllegalArgumentException("level must be >= 1");
		}
		Objects.requireNonNull(range, "range");
		this.level = level;
		this.range = range;
		if (inlines == null || inlines.isEmpty()) {
			this.inlines = Collections.emptyList();
		} else {
			this.inlines = Collections.unmodifiableList(new ArrayList<>(inlines));
		}
	}

	public int getLevel() {
		return level;
	}

	public List<StdInline> getInlines() {
		return inlines;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


