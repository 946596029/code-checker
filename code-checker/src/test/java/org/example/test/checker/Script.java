package org.example.test.checker;

import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.task.rule.ContentNumberRule;
import org.example.code.checker.checker.markdown.task.structure.arguments.ArgumentListChecker;
import org.example.code.checker.checker.markdown.task.structure.attributes.AttributeListChecker;
import org.example.code.checker.checker.markdown.task.structure.example.ExampleChecker;
import org.example.code.checker.checker.markdown.task.structure.front.matter.FrontMatterChecker;
import org.example.code.checker.checker.markdown.task.structure.StructureChecker;
import org.example.code.checker.checker.markdown.task.structure.title.TitleChecker;
import org.example.code.checker.checker.utils.FileUtils;
import org.example.code.checker.checker.utils.TreeNode;
import org.example.flow.engine.analyzer.FlowEngine;
import org.example.flow.engine.node.TaskData;
import org.example.flow.engine.node.TaskNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Script {

    /**
     * Simple DocumentParser that parses markdown file into AST.
     */
    static class DocumentParser extends TaskNode {
        @Override
        public List<TaskData<?>> task(Map<String, TaskData<?>> input) {
            TaskData<?> filePathData = input.get("filePath");
            if (filePathData == null) {
                throw new IllegalArgumentException("Missing required input: filePath");
            }
            String filePath = (String) filePathData.getPayload();

            try {
                String mdContent = FileUtils.getFileContent(filePath);
                TreeNode<MdAstNode> document = MdAstGenerator.generate(mdContent, filePath);

                List<TaskData<?>> output = new ArrayList<>();
                output.add(new TaskData<>("rawCode", mdContent));
                output.add(new TaskData<>("originalDocument", document));
                output.add(new TaskData<>("workingDocument", document));
                output.add(new TaskData<>("fileId", filePath));

                return output;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse markdown file: " + filePath, e);
            }
        }
    }

    public static String getFilePathFromParam(String[] args) {
        String filePath;
        if (args != null && args.length > 0) {
            filePath = args[0];
        } else {
            // Default markdown file for demo; can be overridden by CLI arg
            filePath =
                "src/test/java/org/example/test/checker/markdown/resource/huaweicloud_cdn_ip_information.md";
        }
        return filePath;
    }

    public static TaskNode createDocumentParser(String filePath) {
        DocumentParser parseMarkdown = new DocumentParser();
        parseMarkdown.setId("parseMarkdown");
        parseMarkdown.setDependencies(Collections.emptyList());

        TaskData<String> filePathData = new TaskData<>("filePath", filePath);
        parseMarkdown.input = Map.of("filePath", filePathData);

        return parseMarkdown;
    }

    /**
     * Create FrontMatterChecker node that depends on the output of DocumentParser.
     *
     * Graph: parseMarkdown -> checkFrontMatter
     */
    public static TaskNode createFrontMatterChecker(TaskNode markdownParser, String filePath) {
        FrontMatterChecker frontMatterChecker = new FrontMatterChecker();
        frontMatterChecker.setId("checkFrontMatter");
        frontMatterChecker.setDependencies(List.of("parseMarkdown"));

        List<TaskData<?>> outputs = markdownParser.output;
        if (outputs == null || outputs.isEmpty()) {
            throw new IllegalStateException("DocumentParser produced no outputs");
        }

        // DocumentParser outputs:
        // 0: rawCode
        // 1: originalDocument
        // 2: workingDocument
        // 3: fileId
        TaskData<?> rawCodeData = outputs.get(0);
        TaskData<?> originalDocumentData = outputs.get(1);
        TaskData<?> workingDocumentData = outputs.get(2);

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);

        frontMatterChecker.input = Map.of(
            "rawCode", rawCodeData,
            "originalDocument", originalDocumentData,
            "workingDocument", workingDocumentData,
            "fileId", fileIdData
        );

        return frontMatterChecker;
    }

    /**
     * Create TitleChecker node that depends on DocumentParser.
     *
     * Graph: parseMarkdown -> checkTitle
     */
    public static TaskNode createTitleChecker(TaskNode markdownParser, String filePath) {
        TitleChecker titleChecker = new TitleChecker();
        titleChecker.setId("checkTitle");
        titleChecker.setDependencies(List.of("parseMarkdown"));

        List<TaskData<?>> outputs = markdownParser.output;
        if (outputs == null || outputs.isEmpty()) {
            throw new IllegalStateException("DocumentParser produced no outputs");
        }

        TaskData<?> originalDocumentData = outputs.get(1);

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);

        titleChecker.input = Map.of(
            "originalDocument", originalDocumentData,
            "fileId", fileIdData
        );

        return titleChecker;
    }

    /**
     * Create StructureChecker node that depends on DocumentParser.
     *
     * Graph: parseMarkdown -> checkStructure
     */
    public static TaskNode createStructureChecker(TaskNode markdownParser, String filePath) {
        StructureChecker structureChecker = new StructureChecker();
        structureChecker.setId("checkStructure");
        structureChecker.setDependencies(List.of("parseMarkdown"));

        List<TaskData<?>> outputs = markdownParser.output;
        if (outputs == null || outputs.isEmpty()) {
            throw new IllegalStateException("DocumentParser produced no outputs");
        }

        TaskData<?> originalDocumentData = outputs.get(1);

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);

        structureChecker.input = Map.of(
            "originalDocument", originalDocumentData,
            "fileId", fileIdData
        );

        return structureChecker;
    }

    /**
     * Create ExampleChecker node that depends on DocumentParser.
     *
     * Graph: parseMarkdown -> checkExample
     */
    public static TaskNode createExampleChecker(TaskNode markdownParser, String filePath) {
        ExampleChecker exampleChecker = new ExampleChecker();
        exampleChecker.setId("checkExample");
        exampleChecker.setDependencies(List.of("parseMarkdown"));

        List<TaskData<?>> outputs = markdownParser.output;
        if (outputs == null || outputs.isEmpty()) {
            throw new IllegalStateException("DocumentParser produced no outputs");
        }

        TaskData<?> originalDocumentData = outputs.get(1);

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);

        exampleChecker.input = Map.of(
            "originalDocument", originalDocumentData,
            "fileId", fileIdData
        );

        return exampleChecker;
    }

    /**
     * Create ArgumentListChecker node that depends on DocumentParser.
     *
     * Graph: parseMarkdown -> checkArgumentList
     */
    public static TaskNode createArgumentListChecker(TaskNode markdownParser, String filePath) {
        ArgumentListChecker argumentListChecker = new ArgumentListChecker();
        argumentListChecker.setId("checkArgumentList");
        argumentListChecker.setDependencies(List.of("parseMarkdown"));

        List<TaskData<?>> outputs = markdownParser.output;
        if (outputs == null || outputs.isEmpty()) {
            throw new IllegalStateException("DocumentParser produced no outputs");
        }

        TaskData<?> originalDocumentData = outputs.get(1);

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);

        argumentListChecker.input = Map.of(
            "originalDocument", originalDocumentData,
            "fileId", fileIdData
        );

        return argumentListChecker;
    }

    /**
     * Create AttributeListChecker node that depends on DocumentParser.
     *
     * Graph: parseMarkdown -> checkAttributeList
     */
    public static TaskNode createAttributeListChecker(TaskNode markdownParser, String filePath) {
        AttributeListChecker attributeListChecker = new AttributeListChecker();
        attributeListChecker.setId("checkAttributeList");
        attributeListChecker.setDependencies(List.of("parseMarkdown"));

        List<TaskData<?>> outputs = markdownParser.output;
        if (outputs == null || outputs.isEmpty()) {
            throw new IllegalStateException("DocumentParser produced no outputs");
        }

        TaskData<?> originalDocumentData = outputs.get(1);

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);

        attributeListChecker.input = Map.of(
            "originalDocument", originalDocumentData,
            "fileId", fileIdData
        );

        return attributeListChecker;
    }

    /**
     * Create ContentNumberRule node that depends on TitleChecker, ArgumentListChecker, and AttributeListChecker.
     *
     * Graph: checkTitle, checkArgumentList, checkAttributeList -> checkContentNumber
     */
    public static TaskNode createContentNumberRule(
            TaskNode titleChecker,
            TaskNode argumentListChecker,
            TaskNode attributeListChecker,
            String filePath) {
        ContentNumberRule contentNumberRule = new ContentNumberRule();
        contentNumberRule.setId("checkContentNumber");
        contentNumberRule.setDependencies(List.of("checkTitle", "checkArgumentList", "checkAttributeList"));

        Map<String, TaskData<?>> inputMap = new HashMap<>();

        // Get titleResult from TitleChecker
        if (titleChecker.output != null && !titleChecker.output.isEmpty()) {
            for (TaskData<?> output : titleChecker.output) {
                if ("titleResult".equals(output.getName())) {
                    inputMap.put("titleResult", output);
                    break;
                }
            }
        }

        // Get argumentListResult from ArgumentListChecker
        if (argumentListChecker.output != null && !argumentListChecker.output.isEmpty()) {
            for (TaskData<?> output : argumentListChecker.output) {
                if ("argumentListResult".equals(output.getName())) {
                    inputMap.put("argumentListResult", output);
                    break;
                }
            }
        }

        // Get attributeListResult from AttributeListChecker
        if (attributeListChecker.output != null && !attributeListChecker.output.isEmpty()) {
            for (TaskData<?> output : attributeListChecker.output) {
                if ("attributeListResult".equals(output.getName())) {
                    inputMap.put("attributeListResult", output);
                    break;
                }
            }
        }

        TaskData<String> fileIdData = new TaskData<>("fileId", filePath);
        inputMap.put("fileId", fileIdData);

        contentNumberRule.input = inputMap;

        return contentNumberRule;
    }

    public static void main(String[] args) {
        String filePath = getFilePathFromParam(args);

        // Create all task nodes
        TaskNode markdownParser = createDocumentParser(filePath);
        TaskNode frontMatterChecker = createFrontMatterChecker(markdownParser, filePath);
        TaskNode titleChecker = createTitleChecker(markdownParser, filePath);
        TaskNode structureChecker = createStructureChecker(markdownParser, filePath);
        TaskNode exampleChecker = createExampleChecker(markdownParser, filePath);
        TaskNode argumentListChecker = createArgumentListChecker(markdownParser, filePath);
        TaskNode attributeListChecker = createAttributeListChecker(markdownParser, filePath);
        TaskNode contentNumberRule = createContentNumberRule(
            titleChecker, argumentListChecker, attributeListChecker, filePath);

        // Collect all nodes
        List<TaskNode> allNodes = List.of(
            markdownParser,
            frontMatterChecker,
            titleChecker,
            structureChecker,
            exampleChecker,
            argumentListChecker,
            attributeListChecker,
            contentNumberRule
        );

        // Execute all nodes using FlowEngine (which handles needStop propagation)
        FlowEngine engine = new FlowEngine();
        engine.execute(allNodes);

        // Print dependency graph as Mermaid
        String mermaid = engine.exportMermaid(allNodes);
        System.out.println("Dependency Graph:");
        System.out.println(mermaid);

        // Print errors from all checkers
        System.out.println("\n=== Check Results ===");
        for (TaskNode node : allNodes) {
            if (node instanceof org.example.code.checker.checker.Checker) {
                org.example.code.checker.checker.Checker checker = 
                    (org.example.code.checker.checker.Checker) node;
                if (checker.getErrorList() != null && !checker.getErrorList().isEmpty()) {
                    System.out.println("\nErrors from " + node.getId() + ":");
                    for (org.example.code.checker.checker.common.CheckError error : checker.getErrorList()) {
                        System.out.println("  - " + error.getMessage());
                    }
                }
            }
        }
    }
}
