package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Block quote containing block children.
 */
public class BlockQuote extends MdDomain {

    public BlockQuote(SourceRange range) {
        super(StandardNodeType.BLOCK_QUOTE, true, range);
    }

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public BlockQuote build() {
            return new BlockQuote(range);
        }
    }
}