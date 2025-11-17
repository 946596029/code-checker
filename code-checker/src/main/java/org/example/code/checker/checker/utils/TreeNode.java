package org.example.code.checker.checker.utils;

import java.util.*;
import java.util.function.Predicate;

public class TreeNode<T> {
    private String nodeId;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;
    private Integer depth;
    private Integer indexInParent;

    private T data;

    public TreeNode(String nodeId, T data, TreeNode<T> parent, List<TreeNode<T>> children, Integer depth, Integer indexInParent) {
        this.nodeId = nodeId;
        this.parent = parent;
        this.children = children;
        this.depth = depth;
        this.indexInParent = indexInParent;

        this.data = data;
    }

    // structure operation

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }

    public void addChild(TreeNode<T> child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public boolean removeChild(TreeNode<T> child) {
        if (children.contains(child)) {
            children.remove(child);
            return true;
        }
        return false;
    }

    public int getChildCount() {
        return children.size();
    }

    // property operation

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

    public int getDepth() {
        return depth;
    }

    public static <T> List<TreeNode<T>> findAll(TreeNode<T> root, Predicate<TreeNode<T>> condition) {
        List<TreeNode<T>> result = new ArrayList<>();
        Deque<TreeNode<T>> waitQueue = new LinkedList<>();
        waitQueue.add(root);
        while (!waitQueue.isEmpty()) {
            int len = waitQueue.size();
            for (int i=0; i<len; i++) {
                TreeNode<T> node = waitQueue.pop();
                if (condition.test(node)) {
                    result.add(node);
                }
                for (TreeNode<T> child : node.getChildren()) {
                    waitQueue.push(child);
                }
            }
        }
        return result;
    }

    public static <T> Optional<TreeNode<T>> findFirst(TreeNode<T> root, Predicate<TreeNode<T>> condition) {
        if (root == null || condition == null) {
            return null;
        }
        if (condition.test(root)) {
            return Optional.of(root);
        }
        if (root.getChildren() != null) {
            for (TreeNode<T> child : root.getChildren()) {
                Optional<TreeNode<T>> result = findFirst(child, condition);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        return null;
    }
}
