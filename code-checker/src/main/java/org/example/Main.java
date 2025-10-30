package org.example;

import org.example.code.checker.parser.markdown.preprocess.PreProcessor;

public class Main {
    public static void main(String[] args) {
        String rawMarkdown = "## Markdown\n"
                + "### Markdown 预处理\n"
                + "" + "\n" // blank line
                + "    - list item with spaces\n"
                + "\t- list item with tab\n"
                + "Text after list";

        System.out.println(System.getProperty("file.encoding"));

        System.out.println("=== Raw ===");
        System.out.println(rawMarkdown);

        PreProcessor preProcessor = new PreProcessor(rawMarkdown);
        String processed = preProcessor.process();

        System.out.println("\n=== Processed ===");
        System.out.println(processed);
    }
}
