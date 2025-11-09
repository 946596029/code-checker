package org.example.code.checker.checker.markdown.domain.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.example.code.checker.checker.markdown.domain.standard.block.CodeBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.inline.Link;

/**
 * Business-level view with indices for efficient rule queries.
 */
public final class BusinessDocument {
	private final Document stdDocument;
	private final Title title; // may be null
	private final FrontMatter frontMatter; // may be null
	private final List<Section> sections;
	private final Map<Integer, List<Heading>> headingsByLevel;
	private final Map<String, Heading> anchorToHeading;
	private final Map<String, List<CodeBlock>> codeBlocksByLanguage;
	private final Map<String, List<Link>> linksByHost;

	public BusinessDocument(
		Document stdDocument,
		Title title,
		FrontMatter frontMatter,
		List<Section> sections,
		Map<Integer, List<Heading>> headingsByLevel,
		Map<String, Heading> anchorToHeading,
		Map<String, List<CodeBlock>> codeBlocksByLanguage,
		Map<String, List<Link>> linksByHost
	) {
		Objects.requireNonNull(stdDocument, "stdDocument");
		this.stdDocument = stdDocument;
		this.title = title;
		this.frontMatter = frontMatter;
		this.sections = sections == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(sections));
		this.headingsByLevel = unmodifiableMapOfLists(headingsByLevel);
		this.anchorToHeading = anchorToHeading == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(anchorToHeading));
		this.codeBlocksByLanguage = unmodifiableMapOfLists(codeBlocksByLanguage);
		this.linksByHost = unmodifiableMapOfLists(linksByHost);
	}

	private static <K, V> Map<K, List<V>> unmodifiableMapOfLists(Map<K, List<V>> input) {
		if (input == null || input.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<K, List<V>> copy = new LinkedHashMap<>();
		for (Map.Entry<K, List<V>> e : input.entrySet()) {
			List<V> value = e.getValue() == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(e.getValue()));
			copy.put(e.getKey(), value);
		}
		return Collections.unmodifiableMap(copy);
	}

	public Document getStdDocument() {
		return stdDocument;
	}

	public Optional<Title> getTitle() {
		return Optional.ofNullable(title);
	}

	public Optional<FrontMatter> getFrontMatter() {
		return Optional.ofNullable(frontMatter);
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Heading> getHeadingsByLevel(int level) {
		List<Heading> list = headingsByLevel.get(level);
		return list == null ? Collections.emptyList() : list;
	}

	public Optional<Heading> getHeadingByAnchor(String anchor) {
		return Optional.ofNullable(anchorToHeading.get(anchor));
	}

	public List<CodeBlock> getCodeBlocksByLanguage(String language) {
		List<CodeBlock> list = codeBlocksByLanguage.get(language == null ? "" : language.toLowerCase());
		return list == null ? Collections.emptyList() : list;
	}

	public List<Link> getLinksByHost(String host) {
		List<Link> list = linksByHost.get(host == null ? "" : host.toLowerCase());
		return list == null ? Collections.emptyList() : list;
	}
}


