package org.example.code.checker.checker.markdown.parser;


import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.node.*;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.commonmark.parser.IncludeSourceSpans;
//import org.commonmark.ext.source.pos.SourcePosExtension;
//import org.commonmark.ext.source.pos.SourceSpan;
import java.util.List;

public class MdAstGenerator {

    public static Node generateAst(String mdContent) {
        // 1. 创建解析器
        Parser parser = Parser.builder()
                .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES) // 仅块级节点
                .build();
        // 2. 解析 AST
        return parser.parse(mdContent);
    }

    public static MdAstNode generateStandardAst(String mdContent, String fileId) {
        Node root = generateAst(mdContent);
        MdAstNode stdRoot = convert(root, null, 0, 0, fileId, mdContent);
        return stdRoot;
    }

    private static MdAstNode convert(Node node, MdAstNode parent, int depth, int indexInParent, String fileId, String mdContent) {
        MdAstNode current = new MdAstNode();
        current.setFileId(fileId);
        current.setParent(parent);
        current.setDepth(depth);
        current.setIndexInParent(indexInParent);
        current.setNodeType(mapType(node));

        // 设置文本类节点的文本
        if (node instanceof Text) {
            current.setText(((Text) node).getLiteral());
        } else if (node instanceof Code) {
            current.setText(((Code) node).getLiteral());
        } else if (node instanceof HtmlInline) {
            current.setText(((HtmlInline) node).getLiteral());
        } else if (node instanceof HtmlBlock) {
            current.setText(((HtmlBlock) node).getLiteral());
        } else if (node instanceof FencedCodeBlock) {
            current.setText(((FencedCodeBlock) node).getLiteral());
        } else if (node instanceof IndentedCodeBlock) {
            current.setText(((IndentedCodeBlock) node).getLiteral());
        } else {
            current.setText(null);
        }

        // 通过 SourceSpan 填充原始源码与偏移
        List<SourceSpan> spans = node.getSourceSpans();
        if (spans != null && !spans.isEmpty()) {
            int start = Integer.MAX_VALUE;
            int end = -1;
            for (SourceSpan span : spans) {
                int s = span.getInputIndex();
                int e = s + span.getLength();
                if (s < start) start = s;
                if (e > end) end = e;
            }
            if (start >= 0 && end >= start && end <= mdContent.length()) {
                current.setStartOffset(start);
                current.setEndOffset(end);
                current.setRawStr(mdContent.substring(start, end));
            }
        }

        // 遍历子节点
        int childIndex = 0;
        for (Node child = node.getFirstChild(); child != null; child = child.getNext(), childIndex++) {
            MdAstNode stdChild = convert(child, current, depth + 1, childIndex, fileId, mdContent);
            current.addChild(stdChild);
        }

        return current;
    }

    private static MdNodeType mapType(Node node) {
        if (node instanceof Document) return MdNodeType.DOCUMENT;
        if (node instanceof Heading) return MdNodeType.HEADING;
        if (node instanceof Paragraph) return MdNodeType.PARAGRAPH;
        if (node instanceof Text) return MdNodeType.TEXT;
        if (node instanceof Emphasis) return MdNodeType.EMPHASIS;
        if (node instanceof StrongEmphasis) return MdNodeType.STRONG;
        if (node instanceof Link) return MdNodeType.LINK;
        if (node instanceof Image) return MdNodeType.IMAGE;
        if (node instanceof BulletList || node instanceof OrderedList) return MdNodeType.LIST;
        if (node instanceof ListItem) return MdNodeType.LIST_ITEM;
        if (node instanceof Code) return MdNodeType.CODE;
        if (node instanceof FencedCodeBlock || node instanceof IndentedCodeBlock) return MdNodeType.CODE_BLOCK;
        if (node instanceof BlockQuote) return MdNodeType.BLOCK_QUOTE;
        if (node instanceof ThematicBreak) return MdNodeType.THEMATIC_BREAK;
        if (node instanceof HtmlInline) return MdNodeType.HTML_INLINE;
        if (node instanceof HtmlBlock) return MdNodeType.HTML_BLOCK;
        // 若有扩展（表格等）未启用，则不会出现；兜底为 CUSTOM
        return MdNodeType.CUSTOM;
    }
}