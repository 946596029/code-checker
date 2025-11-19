package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Emphasis inline (single emphasis).
 */
public class Emphasis extends MdDomain {

    public Emphasis(SourceRange range) {
        super(StandardNodeType.EMPHASIS, false, range);
    }

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Emphasis build() {
            return new Emphasis(range);
        }
    }
}