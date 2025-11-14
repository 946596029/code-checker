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
                System.err.printf("unsupported block types: %s", t);
                System.err.println(node);
                return null;
//                // Fallback: for unsupported block types, try to render text into a paragraph
//                if (node.getText() != null) {
//                    List<StdInline> inlines = Collections.singletonList(new Text(node.getText(), rangeOf(node)));
//                    return new Paragraph(inlines, rangeOf(node));
//                }
//                // Or flatten children paragraphs
//                List<StdBlock> children = toBlockChildren(node);
//                if (!children.isEmpty()) {
//                    SourceRange r = rangeOf(node);
//                    if (r == null) r = unionRange(children);
//                    return new Document(children, r == null ? new SourceRange() : r);
//                }
//                return null;
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
        switch(t) {
            case TEXT: {
                return buildText(node);
            }
            case CODE: {
                return buildCodeSpan(node);
            }
            case EMPHASIS: {
                return buildEmphasis(node);
            }
            case STRONG: {
                return buildStrong(node);
            }
            case LINK: {
                return buildLink(node);
            }
            case IMAGE: {
                return buildImage(node);
            }
            case HTML_INLINE:
            default: {
                return new Text(
                        node.getNodeId(),
                        SourceRange.rangeOf(node),
                        node.getParent().getNodeId(),
                        MarkdownTextUtil.aggregateText(node));
            }
        }
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
