package org.example.code.checker.checker.markdown.domain.standard.block;

import java.util.Objects;
import java.util.Optional;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Code block (fenced or indented). Language is optional.
 */
public class CodeBlock extends MdDomain {
	private final String content;
	private final String language;

    public CodeBlock(SourceRange range, String content, String language) {
        super(StandardNodeType.CODE_BLOCK, true, range);
        Objects.requireNonNull(content, "content");
        this.content = content;
        this.language = language;
    }

	public String getContent() {
		return content;
	}

	public Optional<String> getLanguage() {
		return Optional.ofNullable(language);
	}

    public static class Builder {
        private SourceRange range;
        private String content;
        private String language;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public CodeBlock build() {
            return new CodeBlock(range, content != null ? content : "", language);
        }
    }
}


