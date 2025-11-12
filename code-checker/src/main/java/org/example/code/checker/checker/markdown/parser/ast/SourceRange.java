package org.example.code.checker.checker.markdown.parser.ast;

import org.example.code.checker.checker.markdown.domain.StdNode;

import java.util.List;

public final class SourceRange {
    public SourcePosition start;
    public SourcePosition end;

    public static SourceRange rangeOf(MdAstNode node) {
        if (node == null) return null;
        Integer s = node.getStartOffset();
        Integer e = node.getEndOffset();
        if (s == null || e == null || s < 0 || e < s) return null;
        SourcePosition sp = new SourcePosition();
        sp.offset = s; sp.line = 0; sp.column = 0;
        SourcePosition ep = new SourcePosition();
        ep.offset = e; ep.line = 0; ep.column = 0;
        SourceRange r = new SourceRange();
        r.start = sp; r.end = ep;
        return r;
    }

    public static SourceRange unionRange(List<? extends StdNode> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        Integer start = null;
        Integer end = null;
        for (StdNode n : nodes) {
            if (n == null || n.getRange() == null || n.getRange().start == null || n.getRange().end == null) continue;
            int s = n.getRange().start.offset;
            int e = n.getRange().end.offset;
            if (start == null || s < start) start = s;
            if (end == null || e > end) end = e;
        }
        if (start == null) return null;
        SourcePosition sp = new SourcePosition();
        sp.offset = start; sp.line = 0; sp.column = 0;
        SourcePosition ep = new SourcePosition();
        ep.offset = end; ep.line = 0; ep.column = 0;
        SourceRange r = new SourceRange();
        r.start = sp; r.end = ep;
        return r;
    }

    public static SourceRange unionRangeInline(List<? extends StdNode> nodes) {
        return unionRange(nodes);
    }
}
