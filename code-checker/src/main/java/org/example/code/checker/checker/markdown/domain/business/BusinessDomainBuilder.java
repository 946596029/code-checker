package org.example.code.checker.checker.markdown.domain.business;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.domain.standard.block.BlockQuote;
import org.example.code.checker.checker.markdown.domain.standard.block.CodeBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.block.ListBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.ListItem;
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph;
import org.example.code.checker.checker.markdown.domain.standard.inline.Emphasis;
import org.example.code.checker.checker.markdown.domain.standard.inline.Image;
import org.example.code.checker.checker.markdown.domain.standard.inline.Link;
import org.example.code.checker.checker.markdown.domain.standard.inline.Strong;
import org.example.code.checker.checker.markdown.domain.standard.inline.Text;

/**
 * Builds business-level document view and indices from a Standard Document.
 */
public final class BusinessDomainBuilder {

	public BusinessDocument build(Document document) {
		Objects.requireNonNull(document, "document");

		List<Heading> headings = extractHeadings(document);
		Title title = buildTitle(headings);
		List<Section> sections = buildSections(headings);

		Map<Integer, List<Heading>> headingsByLevel = groupHeadingsByLevel(headings);
		Map<String, Heading> anchorToHeading = buildAnchorIndex(headings);

		Map<String, List<CodeBlock>> codeByLang = groupCodeBlocksByLanguage(document);
		Map<String, List<Link>> linksByHost = groupLinksByHost(document);

		// FrontMatter is optional; it may be built elsewhere and injected later.
		FrontMatter frontMatter = null;

		return new BusinessDocument(
			document,
			title,
			frontMatter,
			sections,
			headingsByLevel,
			anchorToHeading,
			codeByLang,
			linksByHost
		);
	}

	private static List<Heading> extractHeadings(Document document) {
		List<Heading> result = new ArrayList<>();
		for (StdBlock block : document.getChildren()) {
			collectHeadings(block, result);
		}
		return result;
	}

	private static void collectHeadings(StdBlock block, List<Heading> out) {
		if (block instanceof Heading) {
			out.add((Heading) block);
			return;
		}
		if (block instanceof Paragraph) {
			return;
		}
		if (block instanceof CodeBlock) {
			return;
		}
		if (block instanceof ListBlock) {
			for (ListItem item : ((ListBlock) block).getItems()) {
				for (StdBlock child : item.getChildren()) {
					collectHeadings(child, out);
				}
			}
			return;
		}
		if (block instanceof BlockQuote) {
			for (StdBlock child : ((BlockQuote) block).getChildren()) {
				collectHeadings(child, out);
			}
			return;
		}
	}

	private static Title buildTitle(List<Heading> headings) {
		for (Heading h : headings) {
			if (h.getLevel() == 1) {
				String text = plainText(h);
				return new Title(h, text);
			}
		}
		return null;
	}

	private static List<Section> buildSections(List<Heading> headings) {
		List<Section> roots = new ArrayList<>();
		Deque<Section> stack = new ArrayDeque<>();

		for (Heading h : headings) {
			Section current = new Section(h, new ArrayList<>());
			int level = h.getLevel();
			while (!stack.isEmpty() && stack.peek().getHeading().getLevel() >= level) {
				stack.pop();
			}
			if (stack.isEmpty()) {
				roots.add(current);
			} else {
				((ArrayList<Section>) stack.peek().getChildren()).add(current);
			}
			stack.push(current);
		}
		return roots;
	}

	private static Map<Integer, List<Heading>> groupHeadingsByLevel(List<Heading> headings) {
		Map<Integer, List<Heading>> map = new LinkedHashMap<>();
		for (Heading h : headings) {
			map.computeIfAbsent(h.getLevel(), k -> new ArrayList<>()).add(h);
		}
		return map;
	}

	private static Map<String, Heading> buildAnchorIndex(List<Heading> headings) {
		Map<String, Heading> map = new LinkedHashMap<>();
		for (Heading h : headings) {
			String anchor = slugify(plainText(h));
			if (!anchor.isEmpty() && !map.containsKey(anchor)) {
				map.put(anchor, h);
			}
		}
		return map;
	}

	private static Map<String, List<CodeBlock>> groupCodeBlocksByLanguage(Document document) {
		Map<String, List<CodeBlock>> map = new LinkedHashMap<>();
		for (StdBlock block : document.getChildren()) {
			collectCodeBlocks(block, map);
		}
		return map;
	}

	private static void collectCodeBlocks(StdBlock block, Map<String, List<CodeBlock>> out) {
		if (block instanceof CodeBlock) {
			CodeBlock cb = (CodeBlock) block;
			String key = cb.getLanguage().orElse("").toLowerCase(Locale.ROOT);
			out.computeIfAbsent(key, k -> new ArrayList<>()).add(cb);
			return;
		}
		if (block instanceof Paragraph) {
			return;
		}
		if (block instanceof Heading) {
			return;
		}
		if (block instanceof ListBlock) {
			for (ListItem item : ((ListBlock) block).getItems()) {
				for (StdBlock child : item.getChildren()) {
					collectCodeBlocks(child, out);
				}
			}
			return;
		}
		if (block instanceof BlockQuote) {
			for (StdBlock child : ((BlockQuote) block).getChildren()) {
				collectCodeBlocks(child, out);
			}
		}
	}

	private static Map<String, List<Link>> groupLinksByHost(Document document) {
		Map<String, List<Link>> map = new LinkedHashMap<>();
		for (StdBlock block : document.getChildren()) {
			collectLinks(block, map);
		}
		return map;
	}

	private static void collectLinks(StdBlock block, Map<String, List<Link>> out) {
		if (block instanceof Paragraph) {
			for (StdInline inline : ((Paragraph) block).getInlines()) {
				collectLinksInline(inline, out);
			}
			return;
		}
		if (block instanceof Heading) {
			for (StdInline inline : ((Heading) block).getInlines()) {
				collectLinksInline(inline, out);
			}
			return;
		}
		if (block instanceof ListBlock) {
			for (ListItem item : ((ListBlock) block).getItems()) {
				for (StdBlock child : item.getChildren()) {
					collectLinks(child, out);
				}
			}
			return;
		}
		if (block instanceof BlockQuote) {
			for (StdBlock child : ((BlockQuote) block).getChildren()) {
				collectLinks(child, out);
			}
		}
	}

	private static void collectLinksInline(StdInline inline, Map<String, List<Link>> out) {
		if (inline instanceof Link) {
			Link link = (Link) inline;
			String host = parseHost(link.getDestination());
			if (host != null) {
				String key = host.toLowerCase(Locale.ROOT);
				out.computeIfAbsent(key, k -> new ArrayList<>()).add(link);
			}
			return;
		}
		if (inline instanceof Text || inline instanceof Image) {
			return;
		}
		if (inline instanceof Emphasis) {
			for (StdInline child : ((Emphasis) inline).getChildren()) {
				collectLinksInline(child, out);
			}
			return;
		}
		if (inline instanceof Strong) {
			for (StdInline child : ((Strong) inline).getChildren()) {
				collectLinksInline(child, out);
			}
		}
	}

	private static String plainText(Heading heading) {
		StringBuilder sb = new StringBuilder();
		for (StdInline inline : heading.getInlines()) {
			collectPlainText(inline, sb);
		}
		return sb.toString().trim();
	}

	private static void collectPlainText(StdInline inline, StringBuilder out) {
		if (inline instanceof Text) {
			out.append(((Text) inline).getContent());
			return;
		}
		if (inline instanceof Link) {
			for (StdInline child : ((Link) inline).getChildren()) {
				collectPlainText(child, out);
			}
			return;
		}
		if (inline instanceof Emphasis) {
			for (StdInline child : ((Emphasis) inline).getChildren()) {
				collectPlainText(child, out);
			}
			return;
		}
		if (inline instanceof Strong) {
			for (StdInline child : ((Strong) inline).getChildren()) {
				collectPlainText(child, out);
			}
			return;
		}
		if (inline instanceof Image) {
			for (StdInline child : ((Image) inline).getAlt()) {
				collectPlainText(child, out);
			}
		}
	}

	private static String slugify(String text) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		String lower = text.trim().toLowerCase(Locale.ROOT);
		StringBuilder sb = new StringBuilder();
		boolean lastDash = false;
		for (int i = 0; i < lower.length(); i++) {
			char c = lower.charAt(i);
			boolean isAlnum = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
			if (isAlnum) {
				sb.append(c);
				lastDash = false;
			} else {
				if (!lastDash) {
					sb.append('-');
					lastDash = true;
				}
			}
		}
		String s = sb.toString();
		int start = 0;
		int end = s.length();
		while (start < end && s.charAt(start) == '-') start++;
		while (end > start && s.charAt(end - 1) == '-') end--;
		return s.substring(start, end);
	}

	private static String parseHost(String destination) {
		if (destination == null || destination.isEmpty()) {
			return null;
		}
		try {
			URI uri = new URI(destination);
			return uri.getHost();
		} catch (Exception e) {
			return null;
		}
	}
}


