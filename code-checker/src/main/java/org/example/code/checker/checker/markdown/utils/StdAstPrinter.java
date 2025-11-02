package org.example.code.checker.checker.markdown.utils;

import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;

import java.util.List;

public final class StdAstPrinter {

    private StdAstPrinter() {}

    public static void print(MdAstNode root) {
        if (root == null) return;
        // Print root without branch symbols
        System.out.println(buildNodeLabel(root));
        List<MdAstNode> children = root.getChildren();
        if (children == null || children.isEmpty()) return;
        for (int i = 0; i < children.size(); i++) {
            boolean last = (i == children.size() - 1);
            printNode(children.get(i), "", last);
        }
    }

    private static void printNode(MdAstNode node, String prefix, boolean isTail) {
        String branch = prefix + (isTail ? "└── " : "├── ");
        System.out.println(branch + buildNodeLabel(node));

        List<MdAstNode> children = node.getChildren();
        if (children == null || children.isEmpty()) return;

        String childPrefix = prefix + (isTail ? "    " : "│   ");
        for (int i = 0; i < children.size(); i++) {
            boolean last = (i == children.size() - 1);
            printNode(children.get(i), childPrefix, last);
        }
    }

    private static String buildNodeLabel(MdAstNode node) {
        StringBuilder sb = new StringBuilder();
        MdNodeType type = node.getNodeType();
        sb.append(type != null ? type.name() : "UNKNOWN");

        String extras = buildExtras(node);
        if (!extras.isEmpty()) {
            sb.append("[").append(extras).append("]");
        }

        String text = buildDisplayText(node);
        if (!text.isEmpty()) {
            sb.append(" {").append(text).append("}");
        }
        return sb.toString();
    }

    private static String buildExtras(MdAstNode node) {
        MdNodeType type = node.getNodeType();
        if (type == null) return "";
        String raw = node.getRawStr();
        if (raw == null) raw = "";

        switch (type) {
            case HEADING:
                int level = detectHeadingLevel(raw);
                if (level > 0) return String.valueOf(level);
                return "";
            case CODE_BLOCK:
                String info = detectFencedInfo(raw);
                return info.isEmpty() ? "" : ("info=" + info);
            default:
                return "";
        }
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
        // first token before space
        int sp = after.indexOf(' ');
        String info = sp >= 0 ? after.substring(0, sp) : after;
        // filter backticks-only fence (no info)
        return info.replace("`", "").replace("~", "").trim();
    }

    private static String buildDisplayText(MdAstNode node) {
        MdNodeType type = node.getNodeType();
        if (type == MdNodeType.TEXT || type == MdNodeType.CODE || type == MdNodeType.HTML_BLOCK || type == MdNodeType.HTML_INLINE || type == MdNodeType.CODE_BLOCK) {
            return summarize(node.getText());
        }
        if (type == MdNodeType.PARAGRAPH || type == MdNodeType.HEADING) {
            String aggregated = aggregateText(node);
            return summarize(aggregated);
        }
        return "";
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

    private static String summarize(String s) {
        if (s == null) return "";
        String normalized = s.replace("\r", "");
        if (normalized.length() > 200) {
            return normalized.substring(0, 200) + "...";
        }
        return normalized;
    }
}


