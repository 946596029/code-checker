package org.example.code.checker.checker.markdown.task.rule;

import org.commonmark.node.Heading;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.utils.TreeNode;

import java.util.List;
import java.util.Optional;

/**
 * Utility methods for markdown checking.
 */
public class MarkdownCheckerUtils {

    /**
     * Finds a heading node by level and optional text content.
     *
     * @param document The document root node
     * @param level    The heading level (1-6)
     * @param text     Optional text content to match (case-insensitive). If null,
     *                 only level is matched
     * @return Optional containing the found heading node, or empty if not found
     */
    public static Optional<TreeNode<MdAstNode>> findHeadingByLevelAndText(
            TreeNode<MdAstNode> document, int level, String text) {
        List<TreeNode<MdAstNode>> headingNodes = document.query()
                .all()
                .ofType(MdAstNode.class)
                .filter(MdAstNode.class, data -> data != null && data.getNodeType() == MdNodeType.HEADING)
                .list();

        return headingNodes.stream()
                .filter(node -> {
                    MdAstNode data = node.getData();
                    Heading heading = (Heading) data.getCommonMarkNode();
                    if (heading == null || heading.getLevel() != level) {
                        return false;
                    }
                    if (text != null) {
                        String headingText = extractHeadingText(node);
                        return headingText != null && headingText.trim().equalsIgnoreCase(text);
                    }
                    return true;
                })
                .findFirst();
    }

    /**
     * Gets the position of a node in the document's children list.
     * Returns -1 if not found.
     */
    public static int getNodePosition(TreeNode<MdAstNode> document, TreeNode<MdAstNode> targetNode) {
        List<TreeNode<MdAstNode>> children = document.getChildren();
        if (children == null) {
            return -1;
        }

        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == targetNode) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Extracts text from heading node by traversing its children.
     */
    public static String extractHeadingText(TreeNode<MdAstNode> node) {
        StringBuilder sb = new StringBuilder();
        extractTextRecursive(node, sb);
        return sb.toString().trim();
    }

    /**
     * Recursively extracts text from a node and its children.
     */
    private static void extractTextRecursive(TreeNode<MdAstNode> node, StringBuilder sb) {
        MdAstNode data = node.getData();
        if (data == null) {
            return;
        }

        if (data.getNodeType() == MdNodeType.TEXT) {
            if (data.getText() != null) {
                sb.append(data.getText());
            }
        } else if (data.getNodeType() == MdNodeType.CODE) {
            if (data.getText() != null) {
                sb.append(data.getText());
            }
        }

        List<TreeNode<MdAstNode>> children = node.getChildren();
        if (children != null) {
            for (TreeNode<MdAstNode> child : children) {
                extractTextRecursive(child, sb);
            }
        }
    }
}

