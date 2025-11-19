package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * List item containing block children.
 */
public class ListItem extends MdDomain {

    public ListItem(SourceRange range) {
        super(StandardNodeType.LIST_ITEM, true, range);
    }

    public static class Builder {
        private SourceRange range;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public ListItem build() {
            return new ListItem(range);
        }
    }
}


