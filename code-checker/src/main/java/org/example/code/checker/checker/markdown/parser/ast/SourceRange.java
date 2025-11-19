package org.example.code.checker.checker.markdown.parser.ast;

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
}
