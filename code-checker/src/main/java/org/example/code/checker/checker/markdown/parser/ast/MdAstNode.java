package org.example.code.checker.checker.markdown.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MdAstNode {

    private String fileId;
    private String nodeId;

    private Integer startOffset;
    private Integer endOffset;
    private SourcePosition startPosition;
    private SourcePosition finishPosition;

    private MdNodeType nodeType;
    private String text;
    private String rawStr;

    private MdAstNode parent;
    private List<MdAstNode> children;
    private int depth;
    private Integer indexInParent;

    public MdAstNode() {
        this.nodeId = UUID.randomUUID().toString();
        this.children = new ArrayList<>();
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
    }

    public Integer getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Integer endOffset) {
        this.endOffset = endOffset;
    }

    public SourcePosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(SourcePosition startPosition) {
        this.startPosition = startPosition;
    }

    public SourcePosition getFinishPosition() {
        return finishPosition;
    }

    public void setFinishPosition(SourcePosition finishPosition) {
        this.finishPosition = finishPosition;
    }

    public MdNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(MdNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRawStr() {
        return rawStr;
    }

    public void setRawStr(String rawStr) {
        this.rawStr = rawStr;
    }

    public MdAstNode getParent() {
        return parent;
    }

    public void setParent(MdAstNode parent) {
        this.parent = parent;
    }

    public List<MdAstNode> getChildren() {
        return children;
    }

    public void setChildren(List<MdAstNode> children) {
        this.children = children;
    }

    public void addChild(MdAstNode child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Integer getIndexInParent() {
        return indexInParent;
    }

    public void setIndexInParent(Integer indexInParent) {
        this.indexInParent = indexInParent;
    }
}
