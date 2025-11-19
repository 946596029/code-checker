package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Strong emphasis (bold) inline.
 */
public class Strong extends MdDomain {

    public Strong(SourceRange range) {
        super(StandardNodeType.STRONG, false, range);
    }

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Strong build() {
            return new Strong(range);
        }
    }
}


