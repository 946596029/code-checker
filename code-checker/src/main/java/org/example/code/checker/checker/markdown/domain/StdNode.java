package org.example.code.checker.checker.markdown.domain;

import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;
import org.example.code.checker.checker.utils.TreeNode;

import java.util.List;
import java.util.Objects;

public class MdDomain {
    private final StandardNodeType nodeType;
    private final boolean isBlock;
    protected SourceRange range;

    public MdDomain(StandardNodeType nodeType, boolean isBlock, SourceRange range) {
        this.nodeType = nodeType;
        this.isBlock = isBlock;
        this.range = range;
    }
}

public class MdDomainNode extends TreeNode<MdDomain> {

    public MdDomainNode
}

/**
 * Base interface for all Standard domain nodes (blocks and inlines).
 * Nodes are immutable and always carry their source range.
 */
public class StdNode {

    private final String nodeId;
    private final StandardNodeType nodeType;
    private final boolean isBlock;
    protected String parentId;
    protected List<StdNode> children;
    protected SourceRange range;

    public StdNode(
        String nodeId,
        SourceRange range,
        StandardNodeType nodeType,
        String parentId,
        List<StdNode> children
    ) {
        Objects.requireNonNull(nodeType);

        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.isBlock = nodeType.isBlock();
        this.parentId = parentId;
        this.children = children;
        this.range = range;
    }

    public String getNodeId() {
        return nodeId;
    }

    public StandardNodeType getNodeType() {
        return nodeType;
    }

    public boolean isBlock() {
        return isBlock;
    }

	// 结构信息
	public boolean hasParent() {
        return parentId != null;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

	public String getParentId() {
        return parentId;
    }

	public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public void setChildren(List<StdNode> children) {
        this.children = children;
    }

	public List<StdNode> getChildren() {
        return children;
    }

	// 位置信息
    public void setRange(SourceRange range) {
        this.range = range;
    }

	public SourceRange getRange() {
        return range;
    }
}


