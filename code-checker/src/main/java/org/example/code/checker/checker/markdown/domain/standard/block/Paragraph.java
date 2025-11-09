package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Paragraph block, containing inline children.
 */
public final class Paragraph implements StdBlock {
	private final List<StdInline> inlines;
	private final SourceRange range;

	public Paragraph(List<StdInline> inlines, SourceRange range) {
		this.range = range;
		if (inlines == null || inlines.isEmpty()) {
			this.inlines = Collections.emptyList();
		} else {
			this.inlines = Collections.unmodifiableList(new ArrayList<>(inlines));
		}
	}

	public List<StdInline> getInlines() {
		return inlines;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


