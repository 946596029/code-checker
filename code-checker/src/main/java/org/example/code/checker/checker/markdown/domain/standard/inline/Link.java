package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Hyperlink inline with child text/inline nodes.
 */
public final class Link implements StdInline {
	private final String destination;
	private final String title;
	private final List<StdInline> children;
	private final SourceRange range;

	public Link(String destination, String title, List<StdInline> children, SourceRange range) {
		Objects.requireNonNull(destination, "destination");
		Objects.requireNonNull(range, "range");
		this.destination = destination;
		this.title = title;
		this.range = range;
		if (children == null || children.isEmpty()) {
			this.children = Collections.emptyList();
		} else {
			this.children = Collections.unmodifiableList(new ArrayList<>(children));
		}
	}

	public String getDestination() {
		return destination;
	}

	public Optional<String> getTitle() {
		return Optional.ofNullable(title);
	}

	public List<StdInline> getChildren() {
		return children;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


