package org.example.code.checker.checker.markdown.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.code.checker.checker.markdown.domain.standard.StdBlock;
import org.example.code.checker.checker.markdown.domain.standard.StdInline;
import org.example.code.checker.checker.markdown.domain.standard.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.block.BlockQuote;
import org.example.code.checker.checker.markdown.domain.standard.block.CodeBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.block.ListBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.ListItem;
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph;
import org.example.code.checker.checker.markdown.domain.standard.block.ThematicBreak;
import org.example.code.checker.checker.markdown.domain.standard.inline.CodeSpan;
import org.example.code.checker.checker.markdown.domain.standard.inline.Emphasis;
import org.example.code.checker.checker.markdown.domain.standard.inline.Strong;
import org.example.code.checker.checker.markdown.domain.standard.inline.Text;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourcePosition;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Build Standard Markdown domain nodes (blocks & inlines) from MdAstNode tree.
 * Best-effort mapping based on MdNodeType and raw text; some extended details
 * (e.g. link destination) are not available in MdAstNode and will be omitted.
 */
public final class MarkdownDomainBuilder {

	private MarkdownDomainBuilder() {}

	public static Document buildDocument(MdAstNode root) {
		if (root == null || root.getNodeType() != MdNodeType.DOCUMENT) {
			return new Document(Collections.emptyList(), new SourceRange());
		}
		List<StdBlock> children = toBlockChildren(root);
		SourceRange range = rangeOf(root);
		if (range == null) range = unionRange(children);
		return new Document(children, range == null ? new SourceRange() : range);
	}

	private static List<StdBlock> toBlockChildren(MdAstNode node) {
		List<MdAstNode> ch = node.getChildren();
		if (ch == null || ch.isEmpty()) return Collections.emptyList();
		List<StdBlock> out = new ArrayList<>();
		for (MdAstNode c : ch) {
			StdBlock b = toBlock(c);
			if (b != null) out.add(b);
		}
		return out;
	}

	private static StdBlock toBlock(MdAstNode node) {
		if (node == null) return null;
		MdNodeType t = node.getNodeType();
		if (t == null) return null;
		switch (t) {
			case DOCUMENT: {
				List<StdBlock> children = toBlockChildren(node);
				SourceRange r = rangeOf(node);
				if (r == null) r = unionRange(children);
				return new Document(children, r == null ? new SourceRange() : r);
			}
			case HEADING: {
				int level = detectHeadingLevel(node.getRawStr());
				List<StdInline> inlines = toInlines(node);
				SourceRange r = rangeOf(node);
				return new Heading(Math.max(1, level), inlines, r == null ? new SourceRange() : r);
			}
			case PARAGRAPH: {
				List<StdInline> inlines = toInlines(node);
				SourceRange r = rangeOf(node);
				return new Paragraph(inlines, r == null ? new SourceRange() : r);
			}
			case LIST: {
				boolean ordered = detectListOrdered(node.getRawStr());
				int start = detectListStartNumber(node.getRawStr());
				List<ListItem> items = new ArrayList<>();
				List<MdAstNode> ch = node.getChildren();
				if (ch != null) {
					for (MdAstNode c : ch) {
						if (c.getNodeType() == MdNodeType.LIST_ITEM) {
							List<StdBlock> liBlocks = toBlockChildren(c);
							SourceRange lr = rangeOf(c);
							items.add(new ListItem(liBlocks, lr == null ? new SourceRange() : lr));
						}
					}
				}
				SourceRange r = rangeOf(node);
				if (r == null) r = unionRange(items);
				return new ListBlock(ordered, start, items, r == null ? new SourceRange() : r);
			}
			case CODE_BLOCK: {
				String content = node.getText() == null ? "" : node.getText();
				String lang = detectFencedInfo(node.getRawStr());
				SourceRange r = rangeOf(node);
				return new CodeBlock(content, lang.isEmpty() ? null : lang, r == null ? new SourceRange() : r);
			}
			case BLOCK_QUOTE: {
				List<StdBlock> children = toBlockChildren(node);
				SourceRange r = rangeOf(node);
				if (r == null) r = unionRange(children);
				return new BlockQuote(children, r == null ? new SourceRange() : r);
			}
			case THEMATIC_BREAK: {
				SourceRange r = rangeOf(node);
				return new ThematicBreak(r == null ? new SourceRange() : r);
			}
			default: {
				// Fallback: for unsupported block types, try to render text into a paragraph
				if (node.getText() != null) {
					List<StdInline> inlines = Collections.singletonList(new Text(node.getText(), rangeOf(node)));
					return new Paragraph(inlines, rangeOf(node));
				}
				// Or flatten children paragraphs
				List<StdBlock> children = toBlockChildren(node);
				if (!children.isEmpty()) {
					SourceRange r = rangeOf(node);
					if (r == null) r = unionRange(children);
					return new Document(children, r == null ? new SourceRange() : r);
				}
				return null;
			}
		}
	}

	private static List<StdInline> toInlines(MdAstNode container) {
		List<MdAstNode> ch = container.getChildren();
		if (ch == null || ch.isEmpty()) return Collections.emptyList();
		List<StdInline> out = new ArrayList<>();
		for (MdAstNode c : ch) {
			MdNodeType t = c.getNodeType();
			if (t == null) continue;
			switch (t) {
				case TEXT: {
					SourceRange r = rangeOf(c);
					out.add(new Text(c.getText() == null ? "" : c.getText(), r == null ? new SourceRange() : r));
					break;
				}
				case CODE: {
					SourceRange r = rangeOf(c);
					out.add(new CodeSpan(c.getText() == null ? "" : c.getText(), r == null ? new SourceRange() : r));
					break;
				}
				case EMPHASIS: {
					List<StdInline> kids = toInlines(c);
					SourceRange r = rangeOf(c);
					if (r == null) r = unionRangeInline(kids);
					out.add(new Emphasis(kids, r == null ? new SourceRange() : r));
					break;
				}
				case STRONG: {
					List<StdInline> kids = toInlines(c);
					SourceRange r = rangeOf(c);
					if (r == null) r = unionRangeInline(kids);
					out.add(new Strong(kids, r == null ? new SourceRange() : r));
					break;
				}
				// LINK / IMAGE info is unavailable in MdAstNode; degrade to plain text
				case LINK:
				case IMAGE:
				case HTML_INLINE: {
					SourceRange r = rangeOf(c);
					String text = aggregateText(c);
					out.add(new Text(text, r == null ? new SourceRange() : r));
					break;
				}
				default: {
					// For unexpected inline nodes, aggregate as text
					SourceRange r = rangeOf(c);
					String text = aggregateText(c);
					if (text == null) text = "";
					out.add(new Text(text, r == null ? new SourceRange() : r));
				}
			}
		}
		return out;
	}

	private static String aggregateText(MdAstNode node) {
		StringBuilder sb = new StringBuilder();
		collectText(node, sb);
		return sb.toString();
	}

	private static void collectText(MdAstNode node, StringBuilder out) {
		if (node == null) return;
		String t = node.getText();
		if (t != null) out.append(t);
		List<MdAstNode> children = node.getChildren();
		if (children == null) return;
		for (MdAstNode c : children) collectText(c, out);
	}

	private static int detectHeadingLevel(String raw) {
		if (raw == null) return 0;
		String line = raw;
		int nl = raw.indexOf('\n');
		if (nl >= 0) line = raw.substring(0, nl);
		int i = 0;
		while (i < line.length() && line.charAt(i) == '#') i++;
		return i > 0 ? i : 0;
	}

	private static String detectFencedInfo(String raw) {
		if (raw == null) return "";
		String line = raw;
		int nl = raw.indexOf('\n');
		if (nl >= 0) line = raw.substring(0, nl);
		line = line.trim();
		if (!line.startsWith("```") && !line.startsWith("~~~")) return "";
		String after = line.substring(3).trim();
		int sp = after.indexOf(' ');
		String info = sp >= 0 ? after.substring(0, sp) : after;
		return info.replace("`", "").replace("~", "").trim();
	}

	private static boolean detectListOrdered(String raw) {
		if (raw == null) return false;
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (Character.isWhitespace(c)) continue;
			if (Character.isDigit(c)) return true;
			return false;
		}
		return false;
	}

	private static int detectListStartNumber(String raw) {
		if (raw == null) return 1;
		int i = 0;
		while (i < raw.length() && Character.isWhitespace(raw.charAt(i))) i++;
		int j = i;
		while (j < raw.length() && Character.isDigit(raw.charAt(j))) j++;
		if (j > i && j < raw.length() && (raw.charAt(j) == '.' || raw.charAt(j) == ')')) {
			try {
				return Math.max(1, Integer.parseInt(raw.substring(i, j)));
			} catch (NumberFormatException ignore) { return 1; }
		}
		return 1;
	}

	private static SourceRange rangeOf(MdAstNode node) {
		if (node == null) return null;
		Integer s = node.getStartOffset();
		Integer e = node.getEndOffset();
		if (s == null || e == null || s < 0 || e < s) return null;
		SourcePosition sp = new SourcePosition();
		sp.offset = s; sp.line = 0; sp.column = 0;
		SourcePosition ep = new SourcePosition();
		ep.offset = e; ep.line = 0; ep.column = 0;
		SourceRange r = new SourceRange();
		r.start = sp; r.end = ep;
		return r;
	}

	private static SourceRange unionRange(List<? extends StdNode> nodes) {
		if (nodes == null || nodes.isEmpty()) return null;
		Integer start = null;
		Integer end = null;
		for (StdNode n : nodes) {
			if (n == null || n.getRange() == null || n.getRange().start == null || n.getRange().end == null) continue;
			int s = n.getRange().start.offset;
			int e = n.getRange().end.offset;
			if (start == null || s < start) start = s;
			if (end == null || e > end) end = e;
		}
		if (start == null || end == null) return null;
		SourcePosition sp = new SourcePosition();
		sp.offset = start; sp.line = 0; sp.column = 0;
		SourcePosition ep = new SourcePosition();
		ep.offset = end; ep.line = 0; ep.column = 0;
		SourceRange r = new SourceRange();
		r.start = sp; r.end = ep;
		return r;
	}

	private static SourceRange unionRangeInline(List<? extends StdNode> nodes) {
		return unionRange(nodes);
	}
}

