package org.example.code.checker.checker.markdown.domain;

import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

public abstract class MdDomain {

    private StandardNodeType nodeType;
    private boolean isBlock;
    private SourceRange range;

    public abstract <R> R accept(MdDomainVisitor<R> visitor);

    public MdDomain(
        StandardNodeType nodeType,
        boolean isBlock,
        SourceRange range
    ) {
        this.nodeType = nodeType;
        this.isBlock = isBlock;
        this.range = range;
    }

    public StandardNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(StandardNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public SourceRange getRange() {
        return range;
    }

    public void setRange(SourceRange range) {
        this.range = range;
    }
}
