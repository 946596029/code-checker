package org.example.code.checker.checker.markdown.parser;


import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.node.*;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.commonmark.parser.IncludeSourceSpans;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;
import org.example.code.checker.checker.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdAstGenerator {

    private static final Pattern FrontMatterDetector = Pattern.compile(
            "^(---\\s*\\r?\\n([\\s\\S]*?)\\r?\\n---\\s*)$",
            Pattern.MULTILINE);

    public static TreeNode<MdAstNode> generateFrontMatterAst(String mdContent) {
        // 探测是否存在前置元信息
        Matcher matcher = FrontMatterDetector.matcher(mdContent);
        // 不存在则返回null
        if (!matcher.find()) {
            return null;
        }
        // 存在则解析
        MdAstNode frontMatterNode = new MdAstNode();
        // 设置文本
        String rawText = matcher.group(1);
        frontMatterNode.setNodeType(MdNodeType.FRONT_MATTER);
        frontMatterNode.setRawStr(rawText);
        frontMatterNode.setText(rawText);
        // 设置目标偏移
        int line = 0;
        int column = 0;
        int inputIndex = 0;
        int length = rawText.length();
        frontMatterNode.setSourceRange(new SourceRange(line, column, inputIndex, length));
        // 返回节点
        return new TreeNode<>(null, frontMatterNode, null, null, 0, 0);
    }

    public static String removeFrontMatterStr(String mdContent) {
        // 探测是否存在前置元信息
        Matcher matcher = FrontMatterDetector.matcher(mdContent);
        // 不存在则返回null
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return mdContent;
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

    private static TreeNode<MdAstNode> convert(Node node, TreeNode<MdAstNode> parent, int depth, int indexInParent, String fileId, String mdContent) {
        TreeNode<MdAstNode> treeNode = new TreeNode<>();

        treeNode.setParent(parent);
        treeNode.setDepth(depth);
        treeNode.setIndexInParent(indexInParent);
//        current.setParent(parent);
//        current.setDepth(depth);
//        current.setIndexInParent(indexInParent);

        MdAstNode data = new MdAstNode();
        data.setCommonMarkNode(node);
        data.setFileId(fileId);
        data.setNodeType(mapType(node));

        // 设置文本类节点的文本
        if (node instanceof Text) {
            data.setText(((Text) node).getLiteral());
        } else if (node instanceof Code) {
            data.setText(((Code) node).getLiteral());
        } else if (node instanceof HtmlInline) {
            data.setText(((HtmlInline) node).getLiteral());
        } else if (node instanceof HtmlBlock) {
            data.setText(((HtmlBlock) node).getLiteral());
        } else if (node instanceof FencedCodeBlock) {
            data.setText(((FencedCodeBlock) node).getLiteral());
        } else if (node instanceof IndentedCodeBlock) {
            data.setText(((IndentedCodeBlock) node).getLiteral());
        } else {
            data.setText(null);
        }

        // 通过 SourceSpan 填充原始源码与偏移
        List<SourceSpan> spans = node.getSourceSpans();
        boolean isSpansNull = spans == null || spans.isEmpty();
        int line = isSpansNull ? 0 : spans.get(0).getLineIndex();
        int column = isSpansNull ? 0 : spans.get(0).getColumnIndex();
        int inputIndex = isSpansNull ? 0 : spans.get(0).getInputIndex();
        int length = isSpansNull ? 0 : spans.get(0).getLength();
        if (!isSpansNull && spans.size() > 1) {
            int spansSize = spans.size();
            SourceSpan start = spans.get(0);
            SourceSpan end = spans.get(spansSize-1);
            length = end.getInputIndex() - start.getInputIndex() + end.getLength();
        }
        data.setSourceRange(new SourceRange(line, column, inputIndex, length));
        data.setRawStr(mdContent.substring(inputIndex, inputIndex+length));
        treeNode.setData(data);

        // 遍历子节点
        int childIndex = 0;
        for (Node child = node.getFirstChild(); child != null; child = child.getNext(), childIndex++) {
            TreeNode<MdAstNode> stdChild = convert(child, treeNode, depth + 1, childIndex, fileId, mdContent);
            treeNode.addChild(stdChild);
        }

        return treeNode;
    }

    public static TreeNode<MdAstNode> generateMarkdownContentAst(String mdContent) {
        // 1. 创建解析器
        Parser parser = Parser.builder()
                .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES) // 仅块级节点
                .build();
        // 2. 解析 AST
        Node root = parser.parse(mdContent);

        return convert(root, null, 0, 0, "", mdContent);
    }

    private static TreeNode<MdAstNode> combine(TreeNode<MdAstNode> mdContentRoot, TreeNode<MdAstNode> frontMatterNode) throws RuntimeException {
        if (!MdNodeType.DOCUMENT.equals(mdContentRoot.getData().getNodeType())) {
            throw new RuntimeException("the root is not document, can not combine.");
        }

        // 对除Document的所有节点进行SourceRange累加操作
        List<TreeNode<MdAstNode>> waitList = new ArrayList<>(List.copyOf(mdContentRoot.getChildren()));
        SourceRange frontMatterRange = frontMatterNode.getData().getSourceRange();
        while (!waitList.isEmpty()) {
            // 记录本次缓冲的数量
            int length = waitList.size();
            for (int i=0; i<length; i++) {
                TreeNode<MdAstNode> astNode = waitList.get(i);
                MdAstNode data = astNode.getData();
                SourceRange oldRange = data.getSourceRange();
                int newLine = oldRange.getLine() + frontMatterRange.getLine();
                int newColumn = oldRange.getColumn();
                int newInputIndex = oldRange.getInputIndex() + frontMatterRange.getInputIndex();
                int newLength = oldRange.getLength();
                data.setSourceRange(new SourceRange(newLine, newColumn, newInputIndex, newLength));

                // 将孩子节点的孩子加入等待队列中
                waitList.addAll(astNode.getChildren());
            }
            // 将已经进行过累加的节点从缓冲队列中移除
            waitList.removeIf(new Predicate<TreeNode<MdAstNode>>() {
                @Override
                public boolean test(TreeNode<MdAstNode> mdAstNode) {
                    int index = waitList.indexOf(mdAstNode);
                    return index != -1 && index < length;
                }
            });
        }

        // 将 frontMatterNode 设置为第一个孩子节点
        mdContentRoot.getChildren().add(0, frontMatterNode);
        // 返回组装好的内容节点
        return mdContentRoot;
    }

    public static TreeNode<MdAstNode> generate(String mdContent, String fileId) {
        String rawMdContent = mdContent;
        TreeNode<MdAstNode> frontMatterNode = generateFrontMatterAst(mdContent);
        mdContent = removeFrontMatterStr(mdContent);
        TreeNode<MdAstNode> mdContentRoot = generateMarkdownContentAst(mdContent);
        // 处理根节点
        TreeNode<MdAstNode> root = frontMatterNode != null
                ? combine(mdContentRoot, frontMatterNode)
                : mdContentRoot;
        SourceRange rootRange = new SourceRange(0, 0, 0, rawMdContent.length());
        root.getData().setSourceRange(rootRange);
        root.getData().setRawStr(rawMdContent);
        root.getData().setText(mdContent);
        return root;
    }
}