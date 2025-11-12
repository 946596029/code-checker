package org.example.code.checker.checker.markdown.domain.standard.inline;

import java.util.List;
import java.util.Optional;

import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.parser.ast.SourceRange;

/**
 * Image inline node. Alt text is represented by child inlines.
 */
public final class Image extends StdNode {
    private final String title;
    private final String destination;
	private final List<StdNode> alt;

    public Image(
        String nodeId,
        SourceRange range,
        String parentId,
        String title,
        String destination,
        List<StdNode> alt
    ) {
        super(nodeId, range, StandardNodeType.IMAGE, parentId, null);
        this.title = title;
        this.destination = destination;
        this.alt = alt;
    }

	public String getDestination() {
		return destination;
	}

	public Optional<String> getTitle() {
		return Optional.ofNullable(title);
	}

	public List<StdNode> getAlt() {
		return alt;
	}
}