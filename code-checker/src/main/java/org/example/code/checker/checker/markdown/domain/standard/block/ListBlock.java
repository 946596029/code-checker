package org.example.code.checker.checker.markdown.domain.standard.block;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * List block, can be ordered or unordered.
 */
public class ListBlock extends MdDomain {
	private final boolean ordered;
	private final int startNumber;

    public ListBlock(SourceRange range, boolean ordered, int startNumber) {
        super(StandardNodeType.LIST_BLOCK, true, range);

        this.ordered = ordered;
        this.startNumber = Math.max(1, startNumber);
    }

	public boolean isOrdered() {
		return ordered;
	}

	public int getStartNumber() {
		return startNumber;
	}

    public static class Builder {
        private SourceRange range;
        private boolean ordered;
        private int startNumber;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Builder ordered(boolean ordered) {
            this.ordered = ordered;
            return this;
        }

        public Builder startNumber(int startNumber) {
            this.startNumber = startNumber;
            return this;
        }

        public ListBlock build() {
            return new ListBlock(range, ordered, startNumber);
        }
    }
}


