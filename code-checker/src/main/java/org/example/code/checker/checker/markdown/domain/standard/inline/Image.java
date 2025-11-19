package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.Optional;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Image inline node. Alt text is represented by child inlines.
 */
public class Image extends MdDomain {
    private final String title;
    private final String destination;

    public Image(SourceRange range, String title, String destination) {
        super(StandardNodeType.IMAGE, false, range);
        this.title = title;
        this.destination = destination;
    }

	public String getDestination() {
		return destination;
	}

	public Optional<String> getTitle() {
		return Optional.ofNullable(title);
	}

    public static class Builder {
        private SourceRange range;
        private String title;
        private String destination;

        public Builder range(SourceRange range) {
            this.range = range;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Image build() {
            return new Image(range, title, destination);
        }
    }
}