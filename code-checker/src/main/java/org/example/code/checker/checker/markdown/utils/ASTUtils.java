package org.example.code.checker.checker.markdown.utils;

import org.commonmark.node.Node;
import org.commonmark.node.Heading;
import org.commonmark.node.Text;
import org.commonmark.node.Code;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Paragraph;

import java.util.ArrayDeque;
import java.util.Deque;

public class ASTUtils {

    public static void walk(Node root) {
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            visit(cur);

            // 把子节点“倒序”压栈，保证先序顺序
            Node next = cur.getFirstChild();
            Node[] tmp = new Node[16];  // 一般深度不会太大
            int cnt = 0;
            while (next != null) {      // 收集子节点
                tmp[cnt++] = next;
                next = next.getNext();
            }
            for (int i = cnt - 1; i >= 0; i--) {
                stack.push(tmp[i]);
            }
        }
    }

    private static void visit(Node n) {
        System.out.println(repeat("  ", depth(n)) + n.getClass().getSimpleName());
    }

    // 计算节点深度（根为 0）
    private static int depth(Node n) {
        int d = 0;
        while ((n = n.getParent()) != null) d++;
        return d;
    }

    private static String repeat(String s, int count) {
        return new String(new char[count]).replace("\0", s);
    }

    // Collect plain text from a node by traversing its children.
    public static String collectText(Node node) {
        StringBuilder sb = new StringBuilder();
        collectTextRecursive(node, sb);
        return sb.toString().trim();
    }

    private static void collectTextRecursive(Node node, StringBuilder out) {
        if (node == null) return;
        if (node instanceof Text) {
            out.append(((Text) node).getLiteral());
        } else if (node instanceof Code) {
            out.append(((Code) node).getLiteral());
        } else if (node instanceof SoftLineBreak || node instanceof HardLineBreak) {
            out.append(' ');
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
            collectTextRecursive(child, out);
        }
    }

    // Find the first non-empty paragraph text after the first level-1 heading.
    public static String extractDescriptionAfterH1(Node document) {
        boolean seenH1 = false;
        for (Node n = document.getFirstChild(); n != null; n = n.getNext()) {
            if (n instanceof Heading) {
                Heading h = (Heading) n;
                if (h.getLevel() == 1) {
                    seenH1 = true;
                    continue;
                }
            }
            if (!seenH1) continue;
            if (n instanceof Paragraph) {
                String text = collectText(n);
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }
        return "";
    }
}
