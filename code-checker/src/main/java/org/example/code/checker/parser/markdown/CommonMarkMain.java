package org.example.code.checker.parser.markdown;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;

public class CommonMarkMain {

    public static void main(String[] args) {
        Parser parser = Parser.builder().build();
        Node doc = parser.parse("" +
                "# huaweicloud_cdn_top_referrer_statistics\n" +
                "\n" +
                "Use this data source to get the TOP100 referrer statistics of CDN domain within HuaweiCloud.\n" +
                "\n" +
                "-> The statistic data is obtained by scanning the service's offline logs and is subject\n" +
                "   to a delay of at least `6` hours.\n" +
                "\n" +
                "## Example Usage\n" +
                "\n" +
                "```hcl\n" +
                "variable \"domain_name\" {}\n" +
                "variable \"start_time\" {}\n" +
                "variable \"end_time\" {}\n" +
                "\n" +
                "data \"huaweicloud_cdn_top_referrer_statistics\" \"test\" {\n" +
                "  domain_name = var.domain_name\n" +
                "  start_time  = var.start_time\n" +
                "  end_time    = var.end_time\n" +
                "  stat_type   = \"req_num\"\n" +
                "}\n" +
                "```");

        // 1. 使用 Node 自带的 walker
        Node current = doc;
        int depth = 0;
        while (current != null) {
            System.out.println(printNode(current, depth));
            if (current.getFirstChild() == null) {
                // 无孩子节点，遍历兄弟节点
                Node next = current.getNext();
                if (next != null) {
                    current = next;
                } else {
                    depth --;
                    current = current.getParent();
                }
            } else {
                // 有孩子节点，先遍历孩子节点
                depth ++;
                current = current.getFirstChild();
            }
        }

    }

    public static String printIndent(int depth) {
        return " ".repeat(Math.max(0, depth));
    }

    public static String printNode(Node node, int depth) {
        String indent = printIndent(depth);
        String content = "[" + node.getClass().getSimpleName() + "  " + (node instanceof Text ? ((Text) node).getLiteral() : "") + "]";
        return indent + content;
    }
}
