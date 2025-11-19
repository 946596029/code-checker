package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.Optional;

import org.example.code.checker.checker.markdown.domain.MdDomain;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Hyperlink inline with child text/inline nodes.
 */
public class Link extends MdDomain {
    private final String title;
    private final String destination;

    public Link(SourceRange range, String title, String destination) {
        super(StandardNodeType.LINK, false, range);
        this.title = title;
        this.destination = destination;
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

	public String getDestination() {
		return destination;
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

        public Link build() {
            return new Link(range, title, destination);
        }
    }
}


