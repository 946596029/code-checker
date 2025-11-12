package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.Optional;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Hyperlink inline with child text/inline nodes.
 */
public final class Link extends StdNode {
    private final String title;
    private final String destination;

    public Link(
        String nodeId,
        SourceRange range,
        String parentId,
        String title,
        String destination
    ) {
        super(nodeId, range, StandardNodeType.LINK, parentId, null);
        this.title = title;
        this.destination = destination;
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

	public String getDestination() {
		return destination;
	}
}


