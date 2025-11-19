package org.example.code.checker.checker.markdown.domain.builder;

import java.util.List;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.block.*;
import org.example.code.checker.checker.markdown.domain.standard.inline.*;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;
import org.example.code.checker.checker.utils.TreeNode;

/**
 * Build Standard Markdown domain nodes (blocks & inlines) from MdAstNode tree.
 * Best-effort mapping based on MdNodeType and raw text; some extended details
 * (e.g. link destination) are not available in MdAstNode and will be omitted.
 */
public final class MarkdownDomainBuilder {
	private MarkdownDomainBuilder() {}

    // block

    public static TreeNode<MdDomain> buildBlockQuote(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.BLOCK_QUOTE) {
            throw new RuntimeException("node type is not `BLOCK_QUOTE`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new BlockQuote(SourceRange.rangeOf(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildBlockChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildCodeBlock(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.CODE_BLOCK) {
            throw new RuntimeException("node type is not `CODE_BLOCK`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new CodeBlock(
                        SourceRange.rangeOf(node),
                        node.getText() == null ? "" : node.getText(),
                        MarkdownTextUtil.detectFencedInfo(node.getRawStr())),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        return treeNode;
    }

    public static TreeNode<MdDomain> buildDocument(MdAstNode node) {
		if (node == null || node.getNodeType() != MdNodeType.DOCUMENT) {
            throw new RuntimeException("node type is not `DOCUMENT`, can not build.");
		}

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Document(SourceRange.rangeOf(node)),
                null,
                null,
                0,
                null);

        buildBlockChildren(node, treeNode);

        return treeNode;
	}

    public static TreeNode<MdDomain> buildHeading(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.HEADING) {
            throw new RuntimeException("node type is not `HEADING`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Heading(
                        SourceRange.rangeOf(node),
                        MarkdownTextUtil.detectHeadingLevel(node.getRawStr())),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildInlineChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildListBlock(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.LIST) {
            throw new RuntimeException("node type is not `LIST`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new ListBlock(
                        SourceRange.rangeOf(node),
                        MarkdownTextUtil.detectListOrdered(node.getRawStr()),
                        MarkdownTextUtil.detectListStartNumber(node.getRawStr())),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildBlockChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildListItem(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.LIST_ITEM) {
            throw new RuntimeException("node type is not `LIST_ITEM`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new ListItem(SourceRange.rangeOf(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildBlockChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildParagraph(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.PARAGRAPH) {
            throw new RuntimeException("node type is not `PARAGRAPH`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Paragraph(SourceRange.rangeOf(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildInlineChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildThematicBreak(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.THEMATIC_BREAK) {
            throw new RuntimeException("node type is not `THEMATIC_BREAK`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new ThematicBreak(SourceRange.rangeOf(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        return treeNode;
    }

    // inline

    public static TreeNode<MdDomain> buildCodeSpan(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.CODE) {
            throw new RuntimeException("node type is not `CODE`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new CodeSpan(
                        SourceRange.rangeOf(node),
                        node.getText() == null ? "" : node.getText()),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        return treeNode;
    }

    public static TreeNode<MdDomain> buildEmphasis(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.EMPHASIS) {
            throw new RuntimeException("node type is not `EMPHASIS`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Emphasis(SourceRange.rangeOf(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildInlineChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildImage(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.IMAGE) {
            throw new RuntimeException("node type is not `IMAGE`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Image(
                        SourceRange.rangeOf(node),
                        "",
                        MarkdownTextUtil.aggregateText(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        return treeNode;
    }

    public static TreeNode<MdDomain> buildLink(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.LINK) {
            throw new RuntimeException("node type is not `LINK`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Link(
                        SourceRange.rangeOf(node),
                        "",
                        MarkdownTextUtil.aggregateText(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildInlineChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildStrong(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.STRONG) {
            throw new RuntimeException("node type is not `STRONG`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Strong(SourceRange.rangeOf(node)),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        buildInlineChildren(node, treeNode);

        return treeNode;
    }

    public static TreeNode<MdDomain> buildText(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null || node.getNodeType() != MdNodeType.TEXT) {
            throw new RuntimeException("node type is not `TEXT`, can not build.");
        }

        TreeNode<MdDomain> treeNode = new TreeNode<MdDomain>(
                node.getNodeId(),
                new Text(
                        SourceRange.rangeOf(node),
                        node.getText() == null ? "" : node.getText()),
                parent,
                null,
                parent != null ? parent.getDepth() + 1 : 0,
                parent != null ? parent.getChildCount() : null);

        if (parent != null) {
            parent.addChild(treeNode);
        }

        return treeNode;
    }

    // children

    private static void buildBlockChildren(MdAstNode node, TreeNode<MdDomain> parent) {
        List<MdAstNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (MdAstNode child : children) {
                toBlock(child, parent);
            }
        }
    }

    private static void buildInlineChildren(MdAstNode node, TreeNode<MdDomain> parent) {
        List<MdAstNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (MdAstNode child : children) {
                toInline(child, parent);
            }
        }
    }

    public static TreeNode<MdDomain> toBlock(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null) return null;
        MdNodeType t = node.getNodeType();
        if (t == null) return null;
        switch (t) {
            case DOCUMENT: {
                return buildDocument(node);
            }
            case HEADING: {
                return buildHeading(node, parent);
            }
            case PARAGRAPH: {
                return buildParagraph(node, parent);
            }
            case LIST: {
                return buildListBlock(node, parent);
            }
            case LIST_ITEM: {
                return buildListItem(node, parent);
            }
            case CODE_BLOCK: {
                return buildCodeBlock(node, parent);
            }
            case BLOCK_QUOTE: {
                return buildBlockQuote(node, parent);
            }
            case THEMATIC_BREAK: {
                return buildThematicBreak(node, parent);
            }
            default: {
                System.err.printf("unsupported block types: %s", t);
                System.err.println(node);
                return null;
            }
        }
    }

    public static TreeNode<MdDomain> toInline(MdAstNode node, TreeNode<MdDomain> parent) {
        if (node == null) return null;
        MdNodeType t = node.getNodeType();
        if (t == null) return null;
        switch(t) {
            case TEXT: {
                return buildText(node, parent);
            }
            case CODE: {
                return buildCodeSpan(node, parent);
            }
            case EMPHASIS: {
                return buildEmphasis(node, parent);
            }
            case STRONG: {
                return buildStrong(node, parent);
            }
            case LINK: {
                return buildLink(node, parent);
            }
            case IMAGE: {
                return buildImage(node, parent);
            }
            case HTML_INLINE:
            default: {
                return buildText(node, parent);
            }
        }
    }
}
