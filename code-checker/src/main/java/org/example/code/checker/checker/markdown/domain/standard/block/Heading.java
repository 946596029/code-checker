package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.Objects;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Heading block, levels typically in [1,6].
 */
public class Heading extends MdDomain {
	private final int level;

	public Heading(SourceRange range, int level) {
		super(StandardNodeType.HEADING, true, range);

        if (level < 1) {
			throw new IllegalArgumentException("level must be >= 1");
		}
		Objects.requireNonNull(range, "range");

		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public static class Builder {
		private SourceRange range;
		private int level;

		public Builder range(SourceRange range) {
			this.range = range;
			return this;
		}

		public Builder level(int level) {
			this.level = level;
			return this;
		}

		public Heading build() {
			return new Heading(range, level);
		}
	}
}