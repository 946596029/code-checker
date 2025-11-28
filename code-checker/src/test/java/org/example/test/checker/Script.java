//package org.example.test.checker;
//
//import org.example.code.checker.checker.markdown.task.checker.structrue.front.matter.FrontMatterChecker;
//import org.example.code.checker.checker.markdown.task.checker.structrue.title.TitleChecker;
//import org.example.code.checker.checker.markdown.task.parser.DocumentParser;
//import org.example.flow.engine.analyzer.FlowEngine;
//import org.example.flow.engine.node.TaskData;
//import org.example.flow.engine.node.TaskNode;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//public class Script {
//
//    public static String getFilePathFromParam(String[] args) {
//        String filePath;
//        if (args != null && args.length > 0) {
//            filePath = args[0];
//        } else {
//            // Default markdown file for demo; can be overridden by CLI arg
//            filePath =
//                "src/test/java/org/example/test/checker/markdown/resource/huaweicloud_cdn_ip_information.md";
//        }
//        return filePath;
//    }
//
//    public static TaskNode createDocumentParser(String filePath) {
//        DocumentParser parseMarkdown = new DocumentParser();
//        parseMarkdown.setId("parseMarkdown");
//        parseMarkdown.setDependencies(Collections.emptyList());
//
//        TaskData<String> filePathData = new TaskData<>(
//            String.class,
//            "Script.main",
//            System.currentTimeMillis(),
//            filePath
//        );
//        parseMarkdown.input = Map.of("filePath", filePathData);
//
//        return parseMarkdown;
//    }
//
//    /**
//     * Create FrontMatterChecker node that depends on the output of DocumentParser.
//     *
//     * Graph: parseMarkdown -> checkFrontMatter
//     */
//    public static TaskNode createFrontMatterChecker(TaskNode markdownParser, String filePath) {
//        FrontMatterChecker frontMatterChecker = new FrontMatterChecker();
//        frontMatterChecker.setId("checkFrontMatter");
//        frontMatterChecker.setDependencies(List.of("parseMarkdown"));
//
//        List<TaskData<?>> outputs = markdownParser.output;
//        if (outputs == null || outputs.isEmpty()) {
//            throw new IllegalStateException("DocumentParser produced no outputs");
//        }
//
//        // DocumentParser outputs:
//        // 0: rawCode
//        // 1: originalDocument
//        // 2: workingDocument
//        // 3: whitespaceErrors
//        TaskData<?> rawCodeData = outputs.get(0);
//        TaskData<?> originalDocumentData = outputs.get(1);
//        TaskData<?> workingDocumentData = outputs.get(2);
//
//        TaskData<String> fileIdData = new TaskData<>(
//            String.class,
//            "Script.main",
//            System.currentTimeMillis(),
//            filePath
//        );
//
//        frontMatterChecker.input = Map.of(
//            "rawCode", rawCodeData,
//            "originalDocument", originalDocumentData,
//            "workingDocument", workingDocumentData,
//            "fileId", fileIdData
//        );
//
//        return frontMatterChecker;
//    }
//
//    /**
//     * Create TitleChecker node that depends on the output of FrontMatterChecker.
//     *
//     * Graph: parseMarkdown -> checkFrontMatter -> checkTitle
//     */
//    public static TaskNode createTitleChecker(TaskNode frontMatterChecker, String filePath) {
//        TitleChecker titleChecker = new TitleChecker();
//        titleChecker.setId("checkTitle");
//        titleChecker.setDependencies(List.of("checkFrontMatter"));
//
//        List<TaskData<?>> outputs = frontMatterChecker.output;
//        if (outputs == null || outputs.isEmpty()) {
//            throw new IllegalStateException("FrontMatterChecker produced no outputs");
//        }
//
//        // FrontMatterChecker's first output is the updated working document.
//        TaskData<?> workingDocumentData = outputs.get(0);
//
//        TaskData<String> fileIdData = new TaskData<>(
//            String.class,
//            "Script.main",
//            System.currentTimeMillis(),
//            filePath
//        );
//
//        titleChecker.input = Map.of(
//            "workingDocument", workingDocumentData,
//            "fileId", fileIdData
//        );
//
//        return titleChecker;
//    }
//
//    public static void main(String[] args) {
//        String filePath = getFilePathFromParam(args);
//
//        // 1. Run DocumentParser to build both original and working document trees.
//        TaskNode markdownParser = Script.createDocumentParser(filePath);
//        markdownParser.execute();
//
//        // 2. Run FrontMatterChecker to validate and consume front matter.
//        TaskNode frontMatterChecker = Script.createFrontMatterChecker(markdownParser, filePath);
//        frontMatterChecker.execute();
//
//        // 3. Wire TitleChecker to depend on FrontMatterChecker and reuse its outputs.
//        TaskNode titleChecker = Script.createTitleChecker(frontMatterChecker, filePath);
//        titleChecker.execute();
//
//        // 4. Optional: export dependency graph as Mermaid.
//        FlowEngine engine = new FlowEngine();
//        String mermaid = engine.exportMermaid(List.of(markdownParser, frontMatterChecker, titleChecker));
//        System.out.println(mermaid);
//    }
//}
