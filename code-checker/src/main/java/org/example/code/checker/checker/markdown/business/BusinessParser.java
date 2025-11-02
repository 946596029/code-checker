package org.example.code.checker.checker.markdown.business;

import org.example.code.checker.checker.markdown.business.domain.*;
import org.example.code.checker.checker.markdown.business.domain.ExampleUsage;
import org.example.code.checker.checker.markdown.business.domain.FrontMatter;
import org.example.code.checker.checker.markdown.business.domain.Title;
import org.example.code.checker.checker.markdown.business.domain.arguement.Argument;
import org.example.code.checker.checker.markdown.business.domain.arguement.ArgumentList;
import org.example.code.checker.checker.markdown.business.domain.attribute.Attribute;
import org.example.code.checker.checker.markdown.business.domain.attribute.AttributeList;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourcePosition;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse MdAstNode to domain objects based on the AST structure (no inline markers required).
 */
public class BusinessParser {

    public static class ParsedDoc {
        public FrontMatter frontMatter;
        public Title title;
        public ExampleUsage exampleUsage;
        public ArgumentList argumentList;
        public AttributeList attributeList;
    }

    public static ParsedDoc parse(MdAstNode root) {
        ParsedDoc doc = new ParsedDoc();
        if (root == null) return doc;

        // Front matter by raw text if available
        String content = root.getRawStr();
        if (content != null && content.startsWith("---\n")) {
            List<String> lines = Arrays.asList(content.split("\n", -1));
            doc.frontMatter = parseFrontMatter(lines);
            // attach approximate source: nodes before first H1
            MdAstNode firstH1 = findFirstHeading(root, 1);
            if (doc.frontMatter != null && firstH1 != null) {
                List<MdAstNode> pre = new ArrayList<>();
                List<MdAstNode> rootChildren = root.getChildren();
                if (rootChildren != null) {
                    for (MdAstNode c : rootChildren) {
                        if (c == firstH1) break;
                        pre.add(c);
                    }
                }
                setSource(doc.frontMatter, pre);
            }
        }

        // Title (first H1) and its following paragraph as description
        MdAstNode h1 = findFirstHeading(root, 1);
        if (h1 != null) {
            Title title = new Title();
            title.name = aggregateText(h1).trim();
            MdAstNode h1Desc = findNextSiblingOfType(h1, MdNodeType.PARAGRAPH);
            if (h1Desc != null) title.description = aggregateText(h1Desc).trim();
            List<MdAstNode> src = new ArrayList<>();
            src.add(h1);
            if (h1Desc != null) src.add(h1Desc);
            setSource(title, src);
            doc.title = title;
        }

        // Example Usage (H2 with exact text match), then next code block
        MdAstNode h2Example = findHeadingByText(root, 2, "Example Usage");
        if (h2Example != null) {
            ExampleUsage ex = new ExampleUsage();
            ex.name = aggregateText(h2Example).trim();
            MdAstNode code = findNextSiblingCodeBlock(h2Example);
            if (code != null) ex.exampleCode = code.getText();
            List<MdAstNode> src = new ArrayList<>();
            src.add(h2Example);
            if (code != null) src.add(code);
            setSource(ex, src);
            doc.exampleUsage = ex;
        }

        // Argument Reference section
        MdAstNode h2Args = findHeadingByText(root, 2, "Argument Reference");
        if (h2Args != null) {
            ArgumentList argList = new ArgumentList();
            argList.name = aggregateText(h2Args).trim();
            MdAstNode desc = findNextSiblingOfType(h2Args, MdNodeType.PARAGRAPH);
            if (desc != null) argList.description = aggregateText(desc).trim();
            MdAstNode list = findNextSiblingOfType(h2Args, MdNodeType.LIST);
            if (list != null) argList.arguments = parseArguments(list);
            List<MdAstNode> src = new ArrayList<>();
            src.add(h2Args);
            if (desc != null) src.add(desc);
            if (list != null) src.add(list);
            setSource(argList, src);
            doc.argumentList = argList;
        }

        // Attribute Reference section
        MdAstNode h2Attr = findHeadingByText(root, 2, "Attribute Reference");
        if (h2Attr != null) {
            AttributeList attrList = new AttributeList();
            attrList.name = aggregateText(h2Attr).trim();
            MdAstNode desc = findNextSiblingOfType(h2Attr, MdNodeType.PARAGRAPH);
            if (desc != null) attrList.description = aggregateText(desc).trim();
            MdAstNode list = findNextSiblingOfType(h2Attr, MdNodeType.LIST);
            if (list != null) attrList.attributes = parseAttributes(list);
            List<MdAstNode> src = new ArrayList<>();
            src.add(h2Attr);
            if (desc != null) src.add(desc);
            if (list != null) src.add(list);
            setSource(attrList, src);
            doc.attributeList = attrList;
        }

        return doc;
    }

    // ---------- AST helpers ----------
    private static MdAstNode findFirstHeading(MdAstNode root, int level) {
        if (root == null) return null;
        if (root.getNodeType() == MdNodeType.HEADING && detectHeadingLevel(root) == level) return root;
        List<MdAstNode> children = root.getChildren();
        if (children == null) return null;
        for (MdAstNode c : children) {
            MdAstNode r = findFirstHeading(c, level);
            if (r != null) return r;
        }
        return null;
    }

    private static MdAstNode findHeadingByText(MdAstNode root, int level, String text) {
        if (root == null) return null;
        if (root.getNodeType() == MdNodeType.HEADING && detectHeadingLevel(root) == level) {
            String t = aggregateText(root).trim();
            if (t.equalsIgnoreCase(text)) return root;
        }
        List<MdAstNode> children = root.getChildren();
        if (children == null) return null;
        for (MdAstNode c : children) {
            MdAstNode r = findHeadingByText(c, level, text);
            if (r != null) return r;
        }
        return null;
    }

    private static int detectHeadingLevel(MdAstNode heading) {
        String raw = heading.getRawStr();
        if (raw == null) return 0;
        String line = raw;
        int nl = raw.indexOf('\n');
        if (nl >= 0) line = raw.substring(0, nl);
        int i = 0;
        while (i < line.length() && line.charAt(i) == '#') i++;
        return i;
    }

    private static MdAstNode findNextSiblingOfType(MdAstNode node, MdNodeType type) {
        if (node == null || node.getParent() == null) return null;
        List<MdAstNode> siblings = node.getParent().getChildren();
        if (siblings == null) return null;
        int idx = node.getIndexInParent() == null ? -1 : node.getIndexInParent();
        for (int i = idx + 1; i < siblings.size(); i++) {
            MdAstNode s = siblings.get(i);
            if (s.getNodeType() == type) return s;
            // stop at next heading of same or higher level when scanning a section
            if (type == MdNodeType.LIST || type == MdNodeType.PARAGRAPH) {
                if (s.getNodeType() == MdNodeType.HEADING) return null;
            }
        }
        return null;
    }

    private static MdAstNode findNextSiblingCodeBlock(MdAstNode node) {
        if (node == null || node.getParent() == null) return null;
        List<MdAstNode> siblings = node.getParent().getChildren();
        int idx = node.getIndexInParent() == null ? -1 : node.getIndexInParent();
        for (int i = idx + 1; i < siblings.size(); i++) {
            MdAstNode s = siblings.get(i);
            if (s.getNodeType() == MdNodeType.CODE_BLOCK) return s;
            if (s.getNodeType() == MdNodeType.HEADING) return null;
        }
        return null;
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

    // ---------- Front matter (YAML-like) from raw text ----------
    private static FrontMatter parseFrontMatter(List<String> lines) {
        if (lines.isEmpty()) return null;
        if (!lines.get(0).trim().equals("---")) return null;
        FrontMatter fm = new FrontMatter();
        int i = 1;
        for (; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().equals("---")) { i++; break; }
            if (line.startsWith("subcategory:")) fm.subcategory = unquote(valueOf(line));
            else if (line.startsWith("layout:")) fm.layout = unquote(valueOf(line));
            else if (line.startsWith("page_title:")) fm.pageTitle = unquote(valueOf(line));
            else if (line.startsWith("description:")) {
                // multi-line block ("|-" style)
                StringBuilder sb = new StringBuilder();
                i++; // skip current line; next lines are the block
                for (; i < lines.size(); i++) {
                    String l = lines.get(i);
                    if (l.trim().equals("---")) { break; }
                    if (l.startsWith("  ")) sb.append(l.substring(2)); else sb.append(l);
                    sb.append('\n');
                }
                fm.descriptionMultiline = sb.toString().trim();
                if (i < lines.size() && lines.get(i).trim().equals("---")) { i++; }
                break;
            }
        }
        return fm;
    }

    private static String valueOf(String line) {
        int idx = line.indexOf(':');
        return idx >= 0 ? line.substring(idx + 1).trim() : "";
    }

    private static String unquote(String s) {
        if (s == null) return null;
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        if (s.endsWith("  ")) s = s.substring(0, s.length() - 2);
        return s;
    }

    // ---------- Sections parsing from AST ----------
    private static List<Argument> parseArguments(MdAstNode list) {
        List<Argument> out = new ArrayList<>();
        List<MdAstNode> items = list.getChildren();
        if (items == null) return out;
        for (MdAstNode li : items) {
            if (li.getNodeType() != MdNodeType.LIST_ITEM) continue;
            MdAstNode para = firstChildOfType(li, MdNodeType.PARAGRAPH);
            if (para == null) continue;
            String text = aggregateText(para).trim();
            // Prefer inline CODE as name if present
            String name = firstInlineCode(para);
            if (name == null) name = parseNameBeforeDash(text);
            List<String> tags = parseTags(text);
            String desc = parseDescription(text);
            Argument a = new Argument();
            a.name = name;
            a.tags = tags;
            a.description = desc;
            // source: list item node, optionally paragraph
            List<MdAstNode> src = new ArrayList<>();
            src.add(li);
            setSource(a, src);
            out.add(a);
        }
        return out;
    }

    private static List<Attribute> parseAttributes(MdAstNode list) {
        List<Attribute> out = new ArrayList<>();
        List<MdAstNode> items = list.getChildren();
        if (items == null) return out;
        for (MdAstNode li : items) {
            if (li.getNodeType() != MdNodeType.LIST_ITEM) continue;
            MdAstNode para = firstChildOfType(li, MdNodeType.PARAGRAPH);
            if (para == null) continue;
            String text = aggregateText(para).trim();
            String name = firstInlineCode(para);
            if (name == null) name = parseNameBeforeDash(text);
            String desc = parseAttributeDescription(text);
            Attribute a = new Attribute();
            a.name = name;
            a.description = desc;
            List<MdAstNode> src = new ArrayList<>();
            src.add(li);
            setSource(a, src);
            out.add(a);
        }
        return out;
    }

    // ---------- source attachment ----------
    private static void setSource(Domain d, List<MdAstNode> nodes) {
        if (d == null) return;
        if (nodes == null) nodes = new ArrayList<>();
        d.sourceNodes = nodes;
        d.sourceRange = unionRange(nodes);
    }

    private static SourceRange unionRange(List<MdAstNode> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        int start = Integer.MAX_VALUE;
        int end = -1;
        for (MdAstNode n : nodes) {
            Integer s = n.getStartOffset();
            Integer e = n.getEndOffset();
            if (s != null && s >= 0 && s < start) start = s;
            if (e != null && e >= 0 && e > end) end = e;
        }
        if (start == Integer.MAX_VALUE || end < 0 || end < start) return null;
        SourcePosition sp = new SourcePosition(); sp.offset = start; sp.line = 0; sp.column = 0;
        SourcePosition ep = new SourcePosition(); ep.offset = end; ep.line = 0; ep.column = 0;
        SourceRange r = new SourceRange(); r.start = sp; r.end = ep;
        return r;
    }

    private static MdAstNode firstChildOfType(MdAstNode node, MdNodeType type) {
        List<MdAstNode> ch = node.getChildren();
        if (ch == null) return null;
        for (MdAstNode c : ch) if (c.getNodeType() == type) return c;
        return null;
    }

    private static String firstInlineCode(MdAstNode node) {
        if (node.getNodeType() == MdNodeType.CODE) return node.getText();
        List<MdAstNode> ch = node.getChildren();
        if (ch == null) return null;
        for (MdAstNode c : ch) {
            String r = firstInlineCode(c);
            if (r != null) return r;
        }
        return null;
    }

    private static String parseNameBeforeDash(String text) {
        int codeBack = text.indexOf('`');
        if (codeBack >= 0) {
            int end = text.indexOf('`', codeBack + 1);
            if (end > codeBack + 1) return text.substring(codeBack + 1, end);
        }
        int dash = text.indexOf(" - ");
        if (dash > 0) return text.substring(0, dash).trim().replace("*", "");
        return text;
    }

    private static List<String> parseTags(String text) {
        List<String> res = new ArrayList<>();
        Matcher m = Pattern.compile("\\(([^)]*)\\)").matcher(text);
        if (m.find()) {
            String inside = m.group(1);
            String[] parts = inside.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) res.add(t);
            }
        }
        return res;
    }

    private static String parseDescription(String text) {
        int idx = text.indexOf(") ");
        int dash = text.indexOf(" - ");
        if (idx >= 0) return text.substring(idx + 2).trim();
        if (dash >= 0) return text.substring(dash + 3).trim();
        return text;
    }

    private static String parseAttributeDescription(String text) {
        int dash = text.indexOf(" - ");
        if (dash >= 0) return text.substring(dash + 3).trim();
        return text;
    }
}
