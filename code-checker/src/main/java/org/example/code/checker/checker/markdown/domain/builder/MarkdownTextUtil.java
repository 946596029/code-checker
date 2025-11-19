package org.example.code.checker.checker.markdown.domain.builder;

import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;

import java.util.List;

public final class MarkdownTextUtil {
    private MarkdownTextUtil() {}

    public static String aggregateText(MdAstNode node) {
        StringBuilder sb = new StringBuilder();
        collectText(node, sb);
        return sb.toString();
    }

    public static void collectText(MdAstNode node, StringBuilder out) {
        if (node == null) return;
        String t = node.getText();
        if (t != null) out.append(t);
        List<MdAstNode> children = node.getChildren();
        if (children == null) return;
        for (MdAstNode c : children) collectText(c, out);
    }

    public static int detectHeadingLevel(String raw) {
        if (raw == null) return 0;
        String line = raw;
        int nl = raw.indexOf('\n');
        if (nl >= 0) line = raw.substring(0, nl);
        int i = 0;
        while (i < line.length() && line.charAt(i) == '#') i++;
        return i;
    }

    public static String detectFencedInfo(String raw) {
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

    public static boolean detectListOrdered(String raw) {
        if (raw == null) return false;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (Character.isWhitespace(c)) continue;
            return Character.isDigit(c);
        }
        return false;
    }

    public static int detectListStartNumber(String raw) {
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
}
