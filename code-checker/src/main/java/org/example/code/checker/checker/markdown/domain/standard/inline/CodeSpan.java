package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Inline code span.
 */
public class CodeSpan extends MdDomain {
	private String code;

    public CodeSpan(
        SourceRange range,
        String code
    ) {
        super(StandardNodeType.CODE_SPAN, false, range);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static class Builder {
        private SourceRange range;
        private String code;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public CodeSpan build() {
            return new CodeSpan(range, code != null ? code : "");
        }
    }
}

