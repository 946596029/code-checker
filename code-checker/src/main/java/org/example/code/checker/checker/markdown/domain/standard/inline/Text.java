package org.example.code.checker.checker.markdown.domain.standard.inline;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Plain text inline node.
 */
public class Text extends MdDomain {
	private final String content;

    public Text(SourceRange range, String content) {
        super(StandardNodeType.TEXT, false, range);
        this.content = content;
    }

	public String getContent() {
		return content;
	}

    public static class Builder {
        private SourceRange range;
        private String content;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Text build() {
            return new Text(range, content != null ? content : "");
        }
    }
}


