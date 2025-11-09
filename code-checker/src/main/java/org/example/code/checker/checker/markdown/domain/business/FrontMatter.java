package org.example.code.checker.checker.markdown.domain.business;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Front matter view, if present (e.g., YAML at the top of the file).
 */
public final class FrontMatter implements Locatable {
	private final Map<String, String> entries;
	private final SourceRange range;

	public FrontMatter(Map<String, String> entries, SourceRange range) {
		Objects.requireNonNull(range, "range");
		this.range = range;
		if (entries == null || entries.isEmpty()) {
			this.entries = Collections.emptyMap();
		} else {
			this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
		}
	}

	public Map<String, String> getEntries() {
		return entries;
	}

	public Optional<String> get(String key) {
		return Optional.ofNullable(entries.get(key));
	}

	@Override
	public SourceRange getRange() {
		return range;
	}
}
