package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Image inline node. Alt text is represented by child inlines.
 */
public final class Image implements StdInline {
	private final String destination;
	private final String title;
	private final List<StdInline> alt;
	private final SourceRange range;

	public Image(String destination, String title, List<StdInline> alt, SourceRange range) {
		Objects.requireNonNull(destination, "destination");
		Objects.requireNonNull(range, "range");
		this.destination = destination;
		this.title = title;
		this.range = range;
		if (alt == null || alt.isEmpty()) {
			this.alt = Collections.emptyList();
		} else {
			this.alt = Collections.unmodifiableList(new ArrayList<>(alt));
		}
	}

	public String getDestination() {
		return destination;
	}

	public Optional<String> getTitle() {
		return Optional.ofNullable(title);
	}

	public List<StdInline> getAlt() {
		return alt;
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}


