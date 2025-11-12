package org.example.code.checker.checker.markdown.domain.builder.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.block.*;
import org.example.code.checker.checker.markdown.domain.standard.inline.*;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Build Standard Markdown domain nodes (blocks & inlines) from MdAstNode tree.
 * Best-effort mapping based on MdNodeType and raw text; some extended details
 * (e.g. link destination) are not available in MdAstNode and will be omitted.
 */
public final class MarkdownDomainBuilder {
	private MarkdownDomainBuilder() {}

    // block

    public static BlockQuote buildBlockQuote(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.BLOCK_QUOTE) {
            throw new RuntimeException("node type is not `BLOCK_QUOTE`, can not build.");
        }

        return new BlockQuote(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toBlockChildren(node));
    }

    public static CodeBlock buildCodeBlock(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.CODE_BLOCK) {
            throw new RuntimeException("node type is not `CODE_BLOCK`, can not build.");
        }

        return new CodeBlock(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                MarkdownTextUtil.detectFencedInfo(node.getRawStr()),
                node.getText() == null ? "" : node.getText());
    }

	public static Document buildDocument(MdAstNode node) {
		if (node == null || node.getNodeType() != MdNodeType.DOCUMENT) {
            throw new RuntimeException("node type is not `DOCUMENT`, can not build.");
		}

        return new Document(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                toBlockChildren(node));
	}

    public static Heading buildHeading(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.HEADING) {
            throw new RuntimeException("node type is not `HEADING`, can not build.");
        }

        return new Heading(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toInlineChildren(node),
                MarkdownTextUtil.detectHeadingLevel(node.getRawStr()));
    }

    public static ListBlock buildListBlock(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.LIST) {
            throw new RuntimeException("node type is not `LIST`, can not build.");
        }

        return new ListBlock(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toBlockChildren(node),
                MarkdownTextUtil.detectListOrdered(node.getRawStr()),
                MarkdownTextUtil.detectListStartNumber(node.getRawStr()));
    }

    public static ListItem buildListItem(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.LIST_ITEM) {
            throw new RuntimeException("node type is not `LIST_ITEM`, can not build.");
        }

        return new ListItem(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toBlockChildren(node));
    }

    public static Paragraph buildParagraph(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.PARAGRAPH) {
            throw new RuntimeException("node type is not `PARAGRAPH`, can not build.");
        }

        return new Paragraph(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toInlineChildren(node));
    }

    public static ThematicBreak buildThematicBreak(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.THEMATIC_BREAK) {
            throw new RuntimeException("node type is not `THEMATIC_BREAK`, can not build.");
        }

        return new ThematicBreak(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId());
    }

    // inline

    public static CodeSpan buildCodeSpan(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.CODE) {
            throw new RuntimeException("node type is not `CODE`, can not build.");
        }

        return new CodeSpan(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                node.getText() == null ? "" : node.getText());
    }

    public static Emphasis buildEmphasis(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.EMPHASIS) {
            throw new RuntimeException("node type is not `EMPHASIS`, can not build.");
        }

        return new Emphasis(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toInlineChildren(node));
    }

    public static Image buildImage(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.IMAGE) {
            throw new RuntimeException("node type is not `IMAGE`, can not build.");
        }

        return new Image(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                "",
                MarkdownTextUtil.aggregateText(node),
                null);
    }

    public static Link buildLink(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.LINK) {
            throw new RuntimeException("node type is not `LINK`, can not build.");
        }

        // fixme has problem
        return new Link(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                "",
                MarkdownTextUtil.aggregateText(node));
    }

    public static Strong buildStrong(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.STRONG) {
            throw new RuntimeException("node type is not `STRONG`, can not build.");
        }

        return new Strong(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                toInlineChildren(node));
    }

    public static Text buildText(MdAstNode node) {
        if (node == null || node.getNodeType() != MdNodeType.TEXT) {
            throw new RuntimeException("node type is not `TEXT`, can not build.");
        }

        return new Text(
                node.getNodeId(),
                SourceRange.rangeOf(node),
                node.getParent().getNodeId(),
                node.getText() == null ? "" : node.getText());
    }

    // children

    public static StdNode toBlock(MdAstNode node) {
        if (node == null) return null;
        MdNodeType t = node.getNodeType();
        if (t == null) return null;
        switch (t) {
            case DOCUMENT: {
                return buildDocument(node);
            }
            case HEADING: {
                return buildHeading(node);
            }
            case PARAGRAPH: {
                return buildParagraph(node);
            }
            case LIST: {
                return buildListBlock(node);
            }
            case LIST_ITEM: {
                return buildListItem(node);
            }
            case CODE_BLOCK: {
                return buildCodeBlock(node);
            }
            case BLOCK_QUOTE: {
                return buildBlockQuote(node);
            }
            case THEMATIC_BREAK: {
                return buildThematicBreak(node);
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

    private static List<StdNode> toBlockChildren(MdAstNode node) {
        List<MdAstNode> children = node.getChildren();
        if (children == null || children.isEmpty()) return Collections.emptyList();

        List<StdNode> out = new ArrayList<>();
        for (MdAstNode child : children) {
            StdNode block = toBlock(child);
            if (block != null) out.add(block);
        }
        return out;
    }

    public static StdNode toInline(MdAstNode node) {
        if (node == null) return null;
        MdNodeType t = node.getNodeType();
        if (t == null) return null;
    }

    private static List<StdNode> toInlineChildren(MdAstNode node) {
        List<MdAstNode> children = node.getChildren();
        if (children == null || children.isEmpty()) return Collections.emptyList();

        List<StdNode> out = new ArrayList<>();
        for (MdAstNode child : children) {
            StdNode inline = toInline(child);
            if (child != null) out.add(inline);
        }
        return out;
    }
}

public final class MarkdownDomainParser {
    private MarkdownDomainParser() {}

    public static Document buildDocument(MdAstNode root) {
        if (root == null || root.getNodeType() != MdNodeType.DOCUMENT) {
            return new Document("none");
        }

        Document document = new Document(root.getNodeId());
        document.setChildren(toBlockChildren(root));
        document.setRange(rangeOf(root));
        if (document.getRange() != null)  {
            document.setRange(unionRange(document.getChildren()));
        }
        return document;
    }

    private static List<StdNode> toBlockChildren(MdAstNode node) {
        List<MdAstNode> ch = node.getChildren();
        if (ch == null || ch.isEmpty()) return Collections.emptyList();
        List<StdNode> out = new ArrayList<>();
        for (MdAstNode c : ch) {
            StdNode b = toBlock(c);
            if (b != null) out.add(b);
        }
        return out;
    }

    private static StdNode toBlock(MdAstNode node) {
        if (node == null) return null;
        MdNodeType t = node.getNodeType();
        if (t == null) return null;
        switch (t) {
            case DOCUMENT: {
                List<StdNode> children = toBlockChildren(node);
                SourceRange r = rangeOf(node);
                if (r == null) r = unionRange(children);
                return new Document(r == null ? new SourceRange() : r, children);
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
                    out.add(new Text(text, r == null ? new SourceRange() : r));
                }
            }
        }
        return out;
    }
}
