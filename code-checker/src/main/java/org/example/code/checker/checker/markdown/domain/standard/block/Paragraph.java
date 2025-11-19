package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Paragraph block, containing inline children.
 */
public class Paragraph extends MdDomain {

    public Paragraph(SourceRange range) {
        super(StandardNodeType.PARAGRAPH, true, range);
    }

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Paragraph build() {
            return new Paragraph(range);
        }
    }
}


