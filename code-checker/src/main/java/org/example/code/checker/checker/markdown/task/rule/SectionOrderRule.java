package org.example.code.checker.checker.markdown.task.rule;

import org.example.code.checker.checker.common.BaseCheckRule;
import org.example.code.checker.checker.common.CheckContext;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.code.checker.checker.markdown.task.rule.MarkdownCheckerUtils.*;

/**
 * Rule for checking section order: Title -> Example Usage -> Argument Reference -> Attribute Reference
 */
public class SectionOrderRule extends BaseCheckRule {

    private static class SectionConfig {
        final int level;
        final String text;
        final String errorCode;
        final String description;

        SectionConfig(int level, String text, String errorCode, String description) {
            this.level = level;
            this.text = text;
            this.errorCode = errorCode;
            this.description = description;
        }
    }

    private static class SectionInfo {
        final SectionConfig config;
        final TreeNode<MdAstNode> node;

        SectionInfo(SectionConfig config, TreeNode<MdAstNode> node) {
            this.config = config;
            this.node = node;
        }
    }

    private static final List<SectionConfig> SECTIONS = List.of(
            new SectionConfig(1, null, "MissingTitle", "Title (level 1 heading)"),
            new SectionConfig(2, "Example Usage", "MissingExampleUsage", "'Example Usage' section"),
            new SectionConfig(2, "Argument Reference", "MissingArgumentReference", "'Argument Reference' section"),
            new SectionConfig(2, "Attribute Reference", "MissingAttributeReference", "'Attribute Reference' section")
    );

    @Override
    public List<CheckError> check(CheckContext context) {
        List<CheckError> errors = new ArrayList<>();
        TreeNode<MdAstNode> document = context.getDocument();

        // Find all required sections
        List<SectionInfo> foundSections = new ArrayList<>();
        for (SectionConfig config : SECTIONS) {
            Optional<TreeNode<MdAstNode>> heading = findHeadingByLevelAndText(document, config.level, config.text);
            if (heading.isEmpty()) {
                errors.add(createError(context, config.errorCode,
                        "Document is missing required " + config.description,
                        null, null, "DOCUMENT"));
                return errors; // If any section is missing, skip order check
            }
            foundSections.add(new SectionInfo(config, heading.get()));
        }

        // Check section order
        checkSectionsOrder(foundSections, document, context, errors);

        return errors;
    }

    private void checkSectionsOrder(List<SectionInfo> sections, TreeNode<MdAstNode> document,
                                    CheckContext context, List<CheckError> errors) {
        // Get positions of all sections
        List<Integer> positions = new ArrayList<>();
        for (SectionInfo section : sections) {
            positions.add(getNodePosition(document, section.node));
        }

        // Check order between adjacent sections
        for (int i = 0; i < sections.size() - 1; i++) {
            if (positions.get(i) >= positions.get(i + 1)) {
                SectionInfo current = sections.get(i);
                SectionInfo next = sections.get(i + 1);
                addOrderError(current, next, context, errors);
            }
        }
    }

    private void addOrderError(SectionInfo current, SectionInfo next, CheckContext context,
                               List<CheckError> errors) {
        String currentName = current.config.text != null ? current.config.text : "Title";
        String nextName = next.config.text != null ? next.config.text : "Title";

        errors.add(createError(context, "InvalidSectionOrder",
                String.format("Section order is incorrect. %s must come before %s", currentName, nextName),
                current.node.getData() != null ? current.node.getData().getSourceRange() : null,
                current.node.getData() != null ? current.node.getData().getNodeId() : null,
                "HEADING"));
    }

    @Override
    public String getRuleName() {
        return "SectionOrder";
    }
}

