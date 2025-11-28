package org.example.code.checker.checker.markdown.task.structure.front.matter;

import org.example.code.checker.checker.Checker;
import org.example.code.checker.checker.common.CheckError;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.parser.ast.MdNodeType;
import org.example.code.checker.checker.markdown.task.structure.front.matter.FrontMatter;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.node.TaskData;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrontMatterChecker extends Checker {

    private static final Set<String> ALLOWED_PROPERTIES = Set.of(
            "subcategory",
            "layout",
            "page_title",
            "description"
    );

    private static final Pattern FRONT_MATTER_PATTERN = Pattern.compile(
            "^---\\s*\\r?\\n([\\s\\S]*?)\\r?\\n---\\s*$",
            Pattern.MULTILINE);

    @Override
    public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
        List<CheckError> errors = new ArrayList<>();

        // Get frontMatterSection from input
        TaskData<?> frontMatterSectionData = input.get("frontMatterSection");
        if (frontMatterSectionData == null) {
            throw new IllegalArgumentException("Missing required input: frontMatterSection");
        }

        @SuppressWarnings("unchecked")
        List<TreeNode<MdAstNode>> frontMatterSection = (List<TreeNode<MdAstNode>>) frontMatterSectionData.getPayload();

        if (frontMatterSection == null || frontMatterSection.isEmpty()) {
            throw new IllegalArgumentException("frontMatterSection is empty");
        }

        // Get fileId for error reporting
        TaskData<?> fileIdData = input.get("fileId");
        String fileId = fileIdData != null ? (String) fileIdData.getPayload() : null;

        // Extract front matter node
        TreeNode<MdAstNode> frontMatterNode = frontMatterSection.get(0);
        MdAstNode nodeData = frontMatterNode.getData();

        if (nodeData == null || nodeData.getNodeType() != MdNodeType.FRONT_MATTER) {
            String message = buildErrorMessage(
                    "FrontMatterChecker.InvalidNodeType",
                    "Expected FRONT_MATTER node type",
                    fileId,
                    nodeData != null ? nodeData.getSourceRange() : null,
                    nodeData != null ? nodeData.getNodeId() : null,
                    nodeData != null ? nodeData.getNodeType().name() : null);
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        // Extract YAML content from raw string
        String rawStr = nodeData.getRawStr();
        if (rawStr == null || rawStr.trim().isEmpty()) {
            String message = buildErrorMessage(
                    "FrontMatterChecker.EmptyFrontMatter",
                    "Front matter content is empty",
                    fileId,
                    nodeData.getSourceRange(),
                    nodeData.getNodeId(),
                    "FRONT_MATTER");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
            setErrorList(errors);
            setNeedStop(true);
            return null;
        }

        // Extract YAML content (remove --- markers)
        Matcher matcher = FRONT_MATTER_PATTERN.matcher(rawStr);
        String yamlContent;
        if (matcher.find()) {
            yamlContent = matcher.group(1);
        } else {
            // Try to extract content between --- markers manually
            String trimmed = rawStr.trim();
            if (trimmed.startsWith("---")) {
                int firstEnd = trimmed.indexOf('\n', 3);
                if (firstEnd > 0) {
                    int lastStart = trimmed.lastIndexOf("---");
                    if (lastStart > firstEnd) {
                        yamlContent = trimmed.substring(firstEnd + 1, lastStart).trim();
                    } else {
                        yamlContent = trimmed.substring(firstEnd + 1).trim();
                    }
                } else {
                    yamlContent = trimmed.substring(3).replaceFirst("---\\s*$", "").trim();
                }
            } else {
                yamlContent = trimmed;
            }
        }

        // Parse YAML (no need to validate format, frontMatterNode already detected complete yaml fragment)
        Yaml yaml = new Yaml();
        Map<String, Object> frontMatterData = yaml.load(yamlContent);
        if (frontMatterData == null) {
            frontMatterData = Map.of();
        }

        // Check for missing required properties
        for (String requiredProperty : ALLOWED_PROPERTIES) {
            if (!frontMatterData.containsKey(requiredProperty) || 
                frontMatterData.get(requiredProperty) == null ||
                frontMatterData.get(requiredProperty).toString().trim().isEmpty()) {
                String message = buildErrorMessage(
                        "FrontMatterChecker.MissingProperty",
                        String.format("Front matter is missing required property: %s", requiredProperty),
                        fileId,
                        nodeData.getSourceRange(),
                        nodeData.getNodeId(),
                        "FRONT_MATTER");
                errors.add(CheckError.builder()
                        .message(message)
                        .severity(CheckError.Severity.ERROR)
                        .build());
            }
        }

        // Validate that only allowed properties are present
        Set<String> actualProperties = frontMatterData.keySet();
        List<String> invalidProperties = new ArrayList<>();
        for (String property : actualProperties) {
            if (!ALLOWED_PROPERTIES.contains(property)) {
                invalidProperties.add(property);
            }
        }

        if (!invalidProperties.isEmpty()) {
            String invalidPropsStr = String.join(", ", invalidProperties);
            String message = buildErrorMessage(
                    "FrontMatterChecker.InvalidProperties",
                    String.format("Front matter contains invalid properties: %s. Only allowed properties are: %s",
                            invalidPropsStr, String.join(", ", ALLOWED_PROPERTIES)),
                    fileId,
                    nodeData.getSourceRange(),
                    nodeData.getNodeId(),
                    "FRONT_MATTER");
            errors.add(CheckError.builder()
                    .message(message)
                    .severity(CheckError.Severity.ERROR)
                    .build());
        }

        // Validate description format: "Use this resource / data source to do ...."
        Object descriptionObj = frontMatterData.get("description");
        if (descriptionObj != null) {
            String description = descriptionObj.toString().trim();
            if (!description.isEmpty()) {
                // Check if description starts with "Use/Using this resource" or "Use/Using this data source"
                String normalizedDescription = description.replaceAll("\\s+", " ").trim();
                if (!normalizedDescription.matches("(?i)^(Use|Using) this (resource|data source) to .*")) {
                    String message = buildErrorMessage(
                            "FrontMatterChecker.InvalidDescriptionFormat",
                            "Description must start with 'Use this resource to', 'Using this resource to', 'Use this data source to', or 'Using this data source to'",
                            fileId,
                            nodeData.getSourceRange(),
                            nodeData.getNodeId(),
                            "FRONT_MATTER");
                    errors.add(CheckError.builder()
                            .message(message)
                            .severity(CheckError.Severity.ERROR)
                            .build());
                }
            }
        }

        // Create FrontMatter value object
        FrontMatter result = new FrontMatter(frontMatterData);

        // Return result
        List<TaskData<?>> output = new ArrayList<>();
        if (errors.size() > 0) {
            setErrorList(errors);
            setNeedStop(true);
            // Still return the result for analysis
            output.add(new TaskData<>("frontMatterResult", result));
            return output;
        }

        output.add(new TaskData<>("frontMatterResult", result));
        return output;
    }


}
