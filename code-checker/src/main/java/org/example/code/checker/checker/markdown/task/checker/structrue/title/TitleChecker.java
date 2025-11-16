package org.example.code.checker.checker.markdown.task.checker.structrue.title;

import org.example.code.checker.checker.CheckError;
import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.block.Paragraph;
import org.example.code.checker.checker.markdown.domain.standard.inline.CodeSpan;
import org.example.code.checker.checker.markdown.domain.standard.inline.Text;
import org.example.flow.engine.node.TaskData;
import org.example.flow.engine.node.TaskDataUtils;
import org.example.flow.engine.node.TaskNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Check title structure and completeness based on {@link Document} domain node.
 * <p>
 * Rules:
 * <ul>
 *     <li>Document must contain a level-1 heading as the title.</li>
 *     <li>The first non-empty paragraph after the title is treated as the title
 *     description and must be present.</li>
 * </ul>
 * <p>
 * On success this task returns {@code TaskData<TitleInfo>} with parsed title info.
 * On failure it returns {@code TaskData<CheckError>} describing the problem.
 */
public class TitleChecker extends TaskNode {

    @Override
    public TaskData<?> task(Map<String, TaskData<?>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

        Document document =
            TaskDataUtils.getPayload(input, "document", Document.class);
        String fileId = TaskDataUtils.getPayload(input, "fileId", String.class);

        if (document == null) {
            CheckError error = new CheckError(
                "Title.DocumentMissing",
                "Document domain node is missing in input",
                CheckError.Severity.ERROR,
                fileId,
                null,
                null,
                "Title"
            );
            return new TaskData<>(
                CheckError.class,
                TitleChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        Heading h1 = findFirstH1(document);
        if (h1 == null) {
            CheckError error = new CheckError(
                "Title.MissingHeading",
                "Level-1 heading (title) is missing in document",
                CheckError.Severity.ERROR,
                fileId,
                document.getRange(),
                document.getNodeId(),
                "Title"
            );
            return new TaskData<>(
                CheckError.class,
                TitleChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        String titleText = collectInlineText(h1);
        if (titleText == null || titleText.trim().isEmpty()) {
            CheckError error = new CheckError(
                "Title.EmptyHeadingText",
                "Level-1 heading (title) has empty text content",
                CheckError.Severity.ERROR,
                fileId,
                h1.getRange(),
                h1.getNodeId(),
                "Title"
            );
            return new TaskData<>(
                CheckError.class,
                TitleChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        Paragraph descParagraph = findFirstParagraphAfter(document, h1);
        if (descParagraph == null) {
            CheckError error = new CheckError(
                "Title.MissingDescription",
                "Title description paragraph after H1 is missing",
                CheckError.Severity.ERROR,
                fileId,
                document.getRange(),
                document.getNodeId(),
                "Title"
            );
            return new TaskData<>(
                CheckError.class,
                TitleChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        String descriptionText = collectInlineText(descParagraph);
        if (descriptionText == null || descriptionText.trim().isEmpty()) {
            CheckError error = new CheckError(
                "Title.EmptyDescription",
                "Title description paragraph after H1 is empty",
                CheckError.Severity.ERROR,
                fileId,
                descParagraph.getRange(),
                descParagraph.getNodeId(),
                "Title"
            );
            return new TaskData<>(
                CheckError.class,
                TitleChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        List<String> extraParagraphs = collectExtraParagraphs(document, descParagraph);

        TitleInfo info = new TitleInfo(
            titleText.trim(),
            descriptionText.trim(),
            extraParagraphs
        );

        return new TaskData<>(
            TitleInfo.class,
            TitleChecker.class.getSimpleName(),
            System.currentTimeMillis(),
            info
        );
    }

    private Heading findFirstH1(Document document) {
        List<StdNode> children = document.getChildren();
        if (children == null || children.isEmpty()) {
            return null;
        }
        for (StdNode child : children) {
            if (child instanceof Heading) {
                Heading h = (Heading) child;
                if (h.getLevel() == 1) {
                    return h;
                }
            }
        }
        return null;
    }

    private Paragraph findFirstParagraphAfter(Document document, Heading h1) {
        List<StdNode> children = document.getChildren();
        if (children == null || children.isEmpty() || h1 == null) {
            return null;
        }
        boolean seenH1 = false;
        for (StdNode child : children) {
            if (child == h1) {
                seenH1 = true;
                continue;
            }
            if (!seenH1) {
                continue;
            }
            if (child instanceof Paragraph) {
                return (Paragraph) child;
            }
        }
        return null;
    }

    /**
     * Collect optional extra paragraphs that appear after the main title description.
     * These are consecutive {@link Paragraph} blocks following the first description
     * paragraph, and stop when a non-paragraph block is encountered.
     */
    private List<String> collectExtraParagraphs(Document document, Paragraph descParagraph) {
        List<String> result = new ArrayList<>();
        List<StdNode> children = document.getChildren();
        if (children == null || children.isEmpty() || descParagraph == null) {
            return result;
        }

        boolean seenDesc = false;
        for (StdNode child : children) {
            if (child == descParagraph) {
                seenDesc = true;
                continue;
            }
            if (!seenDesc) {
                continue;
            }
            if (child instanceof Paragraph) {
                String text = collectInlineText(child);
                if (text != null && !text.trim().isEmpty()) {
                    result.add(text.trim());
                }
            } else {
                // Stop when encountering the first non-paragraph block
                break;
            }
        }

        return result;
    }

    private String collectInlineText(StdNode node) {
        if (node == null || !node.hasChildren()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (StdNode child : node.getChildren()) {
            appendInlineText(child, sb);
        }
        return sb.toString().trim();
    }

    private void appendInlineText(StdNode node, StringBuilder out) {
        if (node == null) {
            return;
        }
        if (node.getNodeType() == StandardNodeType.TEXT && node instanceof Text) {
            out.append(((Text) node).getContent());
        } else if (node.getNodeType() == StandardNodeType.CODE_SPAN && node instanceof CodeSpan) {
            out.append(((CodeSpan) node).getCode());
        } else if (node.hasChildren()) {
            for (StdNode child : node.getChildren()) {
                appendInlineText(child, out);
            }
        }
    }
}
