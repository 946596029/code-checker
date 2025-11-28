package org.example.code.checker.checker.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TreeNode<T> {
    private String nodeId;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;
    private Integer depth;
    private Integer indexInParent;

    private T data;

    public TreeNode() {
        this.nodeId = null;
        this.parent = null;
        this.children = new ArrayList<>();
        this.depth = 0;
        this.indexInParent = 0;

        this.data = null;
    }

    public TreeNode(String nodeId, T data, TreeNode<T> parent, List<TreeNode<T>> children, Integer depth, Integer indexInParent) {
        this.nodeId = nodeId;
        this.parent = parent;
        this.children = children != null ? children : new ArrayList<>();
        this.depth = depth;
        this.indexInParent = indexInParent;

        this.data = data;
    }

    // structure operation

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children != null ? children : new ArrayList<>();
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void addChild(TreeNode<T> child) {
        child.parent = this;
        child.depth = this.getDepth() + 1;
        child.indexInParent = this.getChildCount();
        children.add(child);
    }

    public boolean removeChild(TreeNode<T> child) {
        if (children.contains(child)) {
            children.remove(child);
            for (int i=0; i<this.children.size(); i++) {
                children.get(i).indexInParent = i;
            }
            return true;
        }
        return false;
    }

    public int getChildCount() {
        return children.size();
    }

    // property operation

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public void setIndexInParent(int indexInParent) {
        this.indexInParent = indexInParent;
    }

    public int getIndexInParent() { return indexInParent; }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

    /**
     * ✅ 开始构建查询
     */
    public Query<T> query() {
        return new Query<T>(this);
    }

    /**
     * ✅ 查询构建器（链式调用）
     */
    public static class Query<T> {
        private final TreeNode<T> root;
        private List<TreeNode<T>> results = new ArrayList<>();

        private Query(TreeNode<T> root) {
            this.root = root;
        }

        /**
         * ✅ 递归查询所有节点
         */
        public Query<T> all() {
            results.clear();
            root.traverse(results::add); // 收集所有节点
            return this;
        }

        /**
         * ✅ 只查直接子节点
         */
        public Query<T> children() {
            results = new ArrayList<>(root.getChildren());
            return this;
        }

        /**
         * ✅ 按类型过滤（核心！）
         * 示例：.ofType(Heading.class)
         */
        public <C extends T> Query<T> ofType(Class<C> type) {
            results.removeIf(node -> !type.isInstance(node.getData()));
            return this;
        }

        /**
         * ✅ 按条件过滤（Lambda 中直接访问具体类型）
         * 示例：.filter(Heading.class, h -> h.getLevel() == 1)
         */
        public <C extends T> Query<T> filter(Class<C> type, Predicate<C> condition) {
            results.removeIf(node -> {
                if (!type.isInstance(node.getData()))
                    return true;
                C concrete = type.cast(node.getData());
                return !condition.test(concrete); // 测试具体类型属性
            });
            return this;
        }

        /**
         * ✅ 取第一个结果
         */
        public Optional<TreeNode<T>> first() {
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }

        /**
         * ✅ 取所有结果
         */
        public List<TreeNode<T>> list() {
            return new ArrayList<>(results);
        }

        /**
         * ✅ 遍历结果
         */
        public void forEach(Consumer<TreeNode<T>> action) {
            results.forEach(action);
        }
    }

    /**
     * ✅ 遍历辅助方法
     */
    private void traverse(Consumer<TreeNode<T>> action) {
        action.accept(this);
        for (TreeNode<T> child : this.children) {
            child.traverse(action);
        }
    }
}
