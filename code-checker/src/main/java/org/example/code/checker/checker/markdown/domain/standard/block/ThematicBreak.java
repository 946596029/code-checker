package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Horizontal rule.
 */
public class ThematicBreak extends MdDomain {

	public ThematicBreak(SourceRange range) {
        super(StandardNodeType.THEMATIC_BREAK, true, range);
	}

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public ThematicBreak build() {
            return new ThematicBreak(range);
        }
    }
}


