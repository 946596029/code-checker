package org.example.code.checker.checker.markdown.parser;

import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for searching and manipulating AST node trees.
 * Inspired by browser DOM API for node traversal and querying.
 */
public class MdAstNodeHelper {

    private MdAstNodeHelper() {
        // Utility class, prevent instantiation
    }

    // ==================== Query Selector Style APIs ====================

    /**
     * Find the first node that matches the predicate (like querySelector).
     * Traverses in depth-first order.
     *
     * @param root      the root node to search from
     * @param predicate the condition to match
     * @return the first matching node, or null if not found
     */
    public static MdAstNode findFirst(MdAstNode root, Predicate<MdAstNode> predicate) {
        if (root == null || predicate == null) {
            return null;
        }
        if (predicate.test(root)) {
            return root;
        }
        if (root.getChildren() != null) {
            for (MdAstNode child : root.getChildren()) {
                MdAstNode result = findFirst(child, predicate);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Find all nodes that match the predicate (like querySelectorAll).
     * Traverses in depth-first order.
     *
     * @param root      the root node to search from
     * @param predicate the condition to match
     * @return list of matching nodes (empty if none found)
     */
    public static List<MdAstNode> findAll(MdAstNode root, Predicate<MdAstNode> predicate) {
        List<MdAstNode> results = new ArrayList<>();
        if (root == null || predicate == null) {
            return results;
        }
        collectMatchingNodes(root, predicate, results);
        return results;
    }

    // ==================== Type-based Search APIs ====================

    /**
     * Find the first node with the specified type (like getElementsByTagName).
     *
     * @param root the root node to search from
     * @param type the node type to find
     * @return the first matching node, or null if not found
     */
    public static MdAstNode findByType(MdAstNode root, MdNodeType type) {
        return findFirst(root, node -> node.getNodeType() == type);
    }

    /**
     * Find all nodes with the specified type.
     *
     * @param root the root node to search from
     * @param type the node type to find
     * @return list of matching nodes (empty if none found)
     */
    public static List<MdAstNode> findAllByType(MdAstNode root, MdNodeType type) {
        return findAll(root, node -> node.getNodeType() == type);
    }

    /**
     * Check if a node has the specified type.
     *
     * @param node the node to check
     * @param type the type to check against
     * @return true if node type matches
     */
    public static boolean hasType(MdAstNode node, MdNodeType type) {
        return node != null && node.getNodeType() == type;
    }

    // ==================== ID-based Search APIs ====================

    /**
     * Find a node by its node ID (like getElementById).
     *
     * @param root   the root node to search from
     * @param nodeId the node ID to find
     * @return the matching node, or null if not found
     */
    public static MdAstNode findByNodeId(MdAstNode root, String nodeId) {
        if (nodeId == null) {
            return null;
        }
        return findFirst(root, node -> nodeId.equals(node.getNodeId()));
    }

    // ==================== Text-based Search APIs ====================

    /**
     * Find the first node with exact text match.
     *
     * @param root the root node to search from
     * @param text the exact text to match
     * @return the first matching node, or null if not found
     */
    public static MdAstNode findByText(MdAstNode root, String text) {
        if (text == null) {
            return null;
        }
        return findFirst(root, node -> text.equals(node.getText()));
    }

    /**
     * Find all nodes containing the specified text.
     *
     * @param root the root node to search from
     * @param text the text to search for (partial match)
     * @return list of matching nodes (empty if none found)
     */
    public static List<MdAstNode> findByTextContaining(MdAstNode root, String text) {
        if (text == null) {
            return Collections.emptyList();
        }
        return findAll(root, node -> {
            String nodeText = node.getText();
            return nodeText != null && nodeText.contains(text);
        });
    }

    /**
     * Get all text content from a node and its descendants (like textContent).
     *
     * @param node the node to extract text from
     * @return concatenated text content
     */
    public static String getTextContent(MdAstNode node) {
        if (node == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        collectTextContent(node, sb);
        return sb.toString().trim();
    }

    // ==================== Hierarchy Navigation APIs ====================

    /**
     * Get all ancestor nodes (parent chain to root).
     *
     * @param node the starting node
     * @return list of ancestors from direct parent to root (empty if node is root)
     */
    public static List<MdAstNode> getAncestors(MdAstNode node) {
        List<MdAstNode> ancestors = new ArrayList<>();
        if (node == null) {
            return ancestors;
        }
        MdAstNode parent = node.getParent();
        while (parent != null) {
            ancestors.add(parent);
            parent = parent.getParent();
        }
        return ancestors;
    }

    /**
     * Find the first ancestor node that matches the predicate.
     *
     * @param node      the starting node
     * @param predicate the condition to match
     * @return the first matching ancestor, or null if not found
     */
    public static MdAstNode findAncestor(MdAstNode node, Predicate<MdAstNode> predicate) {
        if (node == null || predicate == null) {
            return null;
        }
        MdAstNode parent = node.getParent();
        while (parent != null) {
            if (predicate.test(parent)) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Find the first ancestor node with the specified type.
     *
     * @param node the starting node
     * @param type the type to find
     * @return the first matching ancestor, or null if not found
     */
    public static MdAstNode findAncestorByType(MdAstNode node, MdNodeType type) {
        return findAncestor(node, n -> n.getNodeType() == type);
    }

    /**
     * Get prev sibling node.
     *
     * @param node the node whose sibling to get
     * @return the prev sibling node
     */
    public static MdAstNode prevSibling(MdAstNode node) {
        if (node == null || node.getParent() == null) {
            return null;
        }
        List<MdAstNode> children = node.getParent().getChildren();
        if (node.getIndexInParent() <= 0) {
            return null;
        }
        return children.get(node.getIndexInParent() - 1);
    }

    /**
     * Get next sibling node.
     *
     * @param node the node whose sibling to get
     * @return the next sibling node
     */
    public static MdAstNode nextSibling(MdAstNode node) {
        if (node == null || node.getParent() == null) {
            return null;
        }
        List<MdAstNode> children = node.getParent().getChildren();
        if (node.getIndexInParent() >= (children.size() - 1)) {
            return null;
        }
        return children.get(node.getIndexInParent() + 1);
    }

    /**
     * Get all sibling nodes (excluding self).
     *
     * @param node the node whose siblings to get
     * @return list of sibling nodes
     */
    public static List<MdAstNode> getSiblings(MdAstNode node) {
        if (node == null || node.getParent() == null) {
            return Collections.emptyList();
        }
        List<MdAstNode> siblings = new ArrayList<>();
        for (MdAstNode child : node.getParent().getChildren()) {
            if (child != node) {
                siblings.add(child);
            }
        }
        return siblings;
    }

    /**
     * Find sibling nodes that match the predicate.
     *
     * @param node      the node whose siblings to search
     * @param predicate the condition to match
     * @return list of matching siblings
     */
    public static List<MdAstNode> findSiblings(MdAstNode node, Predicate<MdAstNode> predicate) {
        if (node == null || predicate == null) {
            return Collections.emptyList();
        }
        List<MdAstNode> siblings = new ArrayList<>();
        if (node.getParent() != null) {
            for (MdAstNode sibling : node.getParent().getChildren()) {
                if (sibling != node && predicate.test(sibling)) {
                    siblings.add(sibling);
                }
            }
        }
        return siblings;
    }

    /**
     * Get all descendant nodes (children, grandchildren, etc.).
     *
     * @param node the starting node
     * @return list of all descendants
     */
    public static List<MdAstNode> getDescendants(MdAstNode node) {
        List<MdAstNode> descendants = new ArrayList<>();
        if (node == null) {
            return descendants;
        }
        collectDescendants(node, descendants);
        return descendants;
    }

    /**
     * Get the first child node.
     *
     * @param node the parent node
     * @return the first child, or null if no children
     */
    public static MdAstNode getFirstChild(MdAstNode node) {
        if (node == null || node.getChildren() == null || node.getChildren().isEmpty()) {
            return null;
        }
        return node.getChildren().get(0);
    }

    /**
     * Get the last child node.
     *
     * @param node the parent node
     * @return the last child, or null if no children
     */
    public static MdAstNode getLastChild(MdAstNode node) {
        if (node == null || node.getChildren() == null || node.getChildren().isEmpty()) {
            return null;
        }
        List<MdAstNode> children = node.getChildren();
        return children.get(children.size() - 1);
    }

    /**
     * Find the first child node that matches the predicate.
     *
     * @param node      the parent node
     * @param predicate the condition to match
     * @return the first matching child, or null if not found
     */
    public static MdAstNode findChild(MdAstNode node, Predicate<MdAstNode> predicate) {
        if (node == null || predicate == null || node.getChildren() == null) {
            return null;
        }
        for (MdAstNode child : node.getChildren()) {
            if (predicate.test(child)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Find all child nodes that match the predicate.
     *
     * @param node      the parent node
     * @param predicate the condition to match
     * @return list of matching children
     */
    public static List<MdAstNode> findChildren(MdAstNode node, Predicate<MdAstNode> predicate) {
        if (node == null || predicate == null || node.getChildren() == null) {
            return Collections.emptyList();
        }
        List<MdAstNode> results = new ArrayList<>();
        for (MdAstNode child : node.getChildren()) {
            if (predicate.test(child)) {
                results.add(child);
            }
        }
        return results;
    }

    // ==================== Path and Navigation APIs ====================

    /**
     * Get the path from root to the specified node.
     *
     * @param root   the root node
     * @param target the target node
     * @return list of nodes from root to target (including both)
     */
    public static List<MdAstNode> getPath(MdAstNode root, MdAstNode target) {
        List<MdAstNode> path = new ArrayList<>();
        if (root == null || target == null) {
            return path;
        }
        if (buildPath(root, target, path)) {
            Collections.reverse(path);
            return path;
        }
        return Collections.emptyList();
    }

    /**
     * Get the depth of a node (0 for root).
     *
     * @param node the node to check
     * @return the depth of the node
     */
    public static int getDepth(MdAstNode node) {
        if (node == null) {
            return -1;
        }
        return node.getDepth();
    }

    // ==================== Helper Methods ====================

    private static void collectMatchingNodes(MdAstNode node, Predicate<MdAstNode> predicate,
            List<MdAstNode> results) {
        if (predicate.test(node)) {
            results.add(node);
        }
        if (node.getChildren() != null) {
            for (MdAstNode child : node.getChildren()) {
                collectMatchingNodes(child, predicate, results);
            }
        }
    }

    private static void collectTextContent(MdAstNode node, StringBuilder sb) {
        if (node.getText() != null) {
            sb.append(node.getText());
        }
        if (node.getChildren() != null) {
            for (MdAstNode child : node.getChildren()) {
                collectTextContent(child, sb);
            }
        }
    }

    private static void collectDescendants(MdAstNode node, List<MdAstNode> descendants) {
        if (node.getChildren() != null) {
            for (MdAstNode child : node.getChildren()) {
                descendants.add(child);
                collectDescendants(child, descendants);
            }
        }
    }

    private static boolean buildPath(MdAstNode current, MdAstNode target, List<MdAstNode> path) {
        if (current == null) {
            return false;
        }
        if (current == target) {
            path.add(current);
            return true;
        }
        if (current.getChildren() != null) {
            for (MdAstNode child : current.getChildren()) {
                if (buildPath(child, target, path)) {
                    path.add(current);
                    return true;
                }
            }
        }
        return false;
    }
}
