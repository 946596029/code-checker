package org.example.code.checker.checker.utils;

/**
 * Utility methods for working with {@link TreeNode}.
 */
public final class TreeNodeUtils {

    private TreeNodeUtils() {}

    /**
     * Create a structural deep copy of the given tree.
     * <p>
     * The {@code data} objects stored in each {@link TreeNode} are reused
     * (shallow copy for payload), while the tree structure itself is fully
     * duplicated so that callers can safely mutate the copy.
     *
     * @param root original tree root (must not be {@code null})
     * @param <T>  node payload type
     * @return root of the copied tree
     */
    public static <T> TreeNode<T> deepCopy(TreeNode<T> root) {
        if (root == null) {
            throw new IllegalArgumentException("root must not be null");
        }
        TreeNode<T> copyRoot = new TreeNode<>(
            root.getNodeId(),
            root.getData(),
            null,
            null,
            0,
            null
        );
        if (root.getChildren() == null || root.getChildren().isEmpty()) {
            return copyRoot;
        }
        for (TreeNode<T> child : root.getChildren()) {
            TreeNode<T> childCopy = deepCopySubtree(child);
            copyRoot.addChild(childCopy);
        }
        return copyRoot;
    }

    private static <T> TreeNode<T> deepCopySubtree(TreeNode<T> node) {
        TreeNode<T> copy = new TreeNode<>(
            node.getNodeId(),
            node.getData(),
            null,
            null,
            0,
            null
        );
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return copy;
        }
        for (TreeNode<T> child : node.getChildren()) {
            TreeNode<T> childCopy = deepCopySubtree(child);
            copy.addChild(childCopy);
        }
        return copy;
    }
}

