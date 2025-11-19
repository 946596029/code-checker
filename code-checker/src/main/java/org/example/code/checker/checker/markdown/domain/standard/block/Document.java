package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Root document node, containing a list of block-level children.
 */
public class Document extends MdDomain {

    public Document(SourceRange range) {
        super(StandardNodeType.DOCUMENT, true, range);
    }

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Document build() {
            return new Document(range);
        }
    }
}


