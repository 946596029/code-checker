package org.example.code.checker.checker.markdown.domain.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * A logical section starting at a heading, with nested sub-sections.
 */
public final class Section implements Locatable {
	private final Heading heading;
	private final List<Section> children;

	public Section(Heading heading, List<Section> children) {
		Objects.requireNonNull(heading, "heading");
		this.heading = heading;
		if (children == null || children.isEmpty()) {
			this.children = Collections.emptyList();
		} else {
			this.children = Collections.unmodifiableList(new ArrayList<>(children));
		}
	}

	public Heading getHeading() {
		return heading;
	}

	public List<Section> getChildren() {
		return children;
	}

	@Override
	public SourceRange getRange() {
		return heading.getRange();
	}
}


