package org.example.code.checker.checker.markdown.task.checker.structrue.attributes;

import org.example.code.checker.checker.CheckError;
import org.example.code.checker.checker.markdown.domain.StdNode;
import org.example.code.checker.checker.markdown.domain.standard.StandardNodeType;
import org.example.code.checker.checker.markdown.domain.standard.block.Document;
import org.example.code.checker.checker.markdown.domain.standard.block.Heading;
import org.example.code.checker.checker.markdown.domain.standard.block.ListBlock;
import org.example.code.checker.checker.markdown.domain.standard.block.ListItem;
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
 * Check "Attributes" section structure and return parsed {@link AttributesInfo}
 * when it is structurally complete.
 * <p>
 * Rules (structure only):
 * <ul>
 *     <li>Document must contain a level-2 heading whose text is exactly
 *     {@code "Attribute Reference"}.</li>
 *     <li>The first non-empty paragraph after that heading is treated as the
 *     section description and must be present.</li>
 *     <li>All list items (bullet or ordered) between the description paragraph
 *     and the next heading of level &lt;= 2 are collected as attribute entries.
 *     The list itself may be empty.</li>
 * </ul>
 * <p>
 * On success this task returns {@code TaskData<AttributesInfo>}.
 * On failure it returns {@code TaskData<CheckError>} describing the problem.
 */
public class AttributesChecker extends TaskNode {

    private static final String EXPECTED_HEADING_TEXT = "Attribute Reference";

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
                "Attributes.DocumentMissing",
                "Document domain node is missing in input",
                CheckError.Severity.ERROR,
                fileId,
                null,
                null,
                "Attributes"
            );
            return new TaskData<>(
                CheckError.class,
                AttributesChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        Heading sectionHeading = findSectionHeading(document, EXPECTED_HEADING_TEXT);
        if (sectionHeading == null) {
            CheckError error = new CheckError(
                "Attributes.MissingHeading",
                "Heading 'Attribute Reference' is missing in document",
                CheckError.Severity.ERROR,
                fileId,
                document.getRange(),
                document.getNodeId(),
                "Attributes"
            );
            return new TaskData<>(
                CheckError.class,
                AttributesChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        Paragraph descParagraph = findFirstParagraphAfter(document, sectionHeading);
        if (descParagraph == null) {
            CheckError error = new CheckError(
                "Attributes.MissingDescription",
                "Description paragraph after 'Attribute Reference' heading is missing",
                CheckError.Severity.ERROR,
                fileId,
                sectionHeading.getRange(),
                sectionHeading.getNodeId(),
                "Attributes"
            );
            return new TaskData<>(
                CheckError.class,
                AttributesChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        String descriptionText = collectInlineText(descParagraph);
        if (descriptionText == null || descriptionText.trim().isEmpty()) {
            CheckError error = new CheckError(
                "Attributes.EmptyDescription",
                "Description paragraph after 'Attribute Reference' heading is empty",
                CheckError.Severity.ERROR,
                fileId,
                descParagraph.getRange(),
                descParagraph.getNodeId(),
                "Attributes"
            );
            return new TaskData<>(
                CheckError.class,
                AttributesChecker.class.getSimpleName(),
                System.currentTimeMillis(),
                error
            );
        }

        List<AttributeItem> items = collectAttributeItems(document, sectionHeading, descParagraph);

        AttributesInfo info = new AttributesInfo(
            collectInlineText(sectionHeading).trim(),
            descriptionText.trim(),
            items
        );

        return new TaskData<>(
            AttributesInfo.class,
            AttributesChecker.class.getSimpleName(),
            System.currentTimeMillis(),
            info
        );
    }

    private Heading findSectionHeading(Document document, String expectedText) {
        List<StdNode> children = document.getChildren();
        if (children == null || children.isEmpty()) {
            return null;
        }
        for (StdNode child : children) {
            if (child instanceof Heading) {
                Heading h = (Heading) child;
                if (h.getLevel() == 2) {
                    String text = collectInlineText(h);
                    if (text != null && text.trim().equals(expectedText)) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    private Paragraph findFirstParagraphAfter(Document document, Heading heading) {
        List<StdNode> children = document.getChildren();
        if (children == null || children.isEmpty() || heading == null) {
            return null;
        }
        boolean seenHeading = false;
        for (StdNode child : children) {
            if (child == heading) {
                seenHeading = true;
                continue;
            }
            if (!seenHeading) {
                continue;
            }
            if (child instanceof Paragraph) {
                return (Paragraph) child;
            }
            if (child instanceof Heading) {
                // Reached next section before description
                return null;
            }
        }
        return null;
    }

    private List<AttributeItem> collectAttributeItems(
        Document document,
        Heading heading,
        Paragraph descParagraph
    ) {
        List<AttributeItem> items = new ArrayList<>();
        List<StdNode> children = document.getChildren();
        if (children == null || children.isEmpty()) {
            return items;
        }

        boolean seenDesc = false;
        for (StdNode child : children) {
            if (!seenDesc) {
                if (child == descParagraph) {
                    seenDesc = true;
                }
                continue;
            }

            if (child instanceof Heading) {
                // Stop at the next section heading (level <= 2).
                Heading h = (Heading) child;
                if (h.getLevel() <= 2) {
                    break;
                }
            }

            if (child instanceof ListBlock) {
                ListBlock list = (ListBlock) child;
                if (list.hasChildren()) {
                    for (StdNode li : list.getChildren()) {
                        if (li instanceof ListItem) {
                            String text = collectInlineText(li);
                            if (text != null && !text.trim().isEmpty()) {
                                AttributeItem parsed = parseAttributeItem(text.trim());
                                items.add(parsed);
                            }
                        }
                    }
                }
            }
        }

        return items;
    }

    private AttributeItem parseAttributeItem(String raw) {
        if (raw == null) {
            return new AttributeItem(null, null, new ArrayList<>(), null);
        }

        String text = raw.trim();
        // Remove leading bullet markers like "* " or "- "
        text = text.replaceFirst("^[*+-]\\s+", "");

        String name = null;
        String metaPart = null;
        String description = null;

        int dashIdx = text.indexOf(" - ");
        if (dashIdx >= 0) {
            name = text.substring(0, dashIdx).trim();
            String afterDash = text.substring(dashIdx + 3).trim();
            if (afterDash.startsWith("(")) {
                int closeIdx = afterDash.indexOf(')');
                if (closeIdx > 0) {
                    metaPart = afterDash.substring(1, closeIdx).trim();
                    description = afterDash.substring(closeIdx + 1).trim();
                } else {
                    description = afterDash;
                }
            } else {
                description = afterDash;
            }
        } else {
            description = text;
        }

        List<String> modifiers = new ArrayList<>();
        if (metaPart != null && !metaPart.isEmpty()) {
            String[] tokens = metaPart.split(",");
            for (String token : tokens) {
                String t = token.trim();
                if (!t.isEmpty()) {
                    modifiers.add(t);
                }
            }
        }

        return new AttributeItem(
            name,
            description,
            modifiers,
            raw
        );
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


