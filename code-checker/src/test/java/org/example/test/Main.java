package org.example.test;

import org.example.code.checker.checker.markdown.business.BusinessParser;
import org.example.code.checker.checker.markdown.business.BusinessParser.ParsedDoc;
import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.utils.FileUtils;
import org.example.code.checker.checker.markdown.business.domain.arguement.Argument;
import org.example.code.checker.checker.markdown.business.domain.attribute.Attribute;

public class Main {
    public static void main(String[] args) {
        String filePath;
        if (args != null && args.length > 0) {
            filePath = args[0];
        } else {
            // Default path for convenience; can be overridden by CLI arg
            filePath = "C:\\Users\\Administrator\\Desktop\\cdn_ip_information.md";
        }

        try {
            String md = FileUtils.getFileContent(filePath);
            MdAstNode root = MdAstGenerator.generateStandardAst(md, filePath);

            ParsedDoc doc = BusinessParser.parse(root);

            System.out.println("=== Title ===");
            if (doc.title != null) {
                System.out.println(doc.title.name);
                if (doc.title.description != null && !doc.title.description.isEmpty()) {
                    System.out.println(doc.title.description);
                }
            }

            System.out.println();
            System.out.println("=== Example Usage ===");
            if (doc.exampleUsage != null) {
                System.out.println(doc.exampleUsage.name);
                if (doc.exampleUsage.exampleCode != null) {
                    System.out.println(doc.exampleUsage.exampleCode);
                }
            }

            System.out.println();
            System.out.println("=== Arguments ===");
            if (doc.argumentList != null && doc.argumentList.arguments != null) {
                if (doc.argumentList.name != null) System.out.println(doc.argumentList.name);
                if (doc.argumentList.description != null) System.out.println(doc.argumentList.description);
                for (Argument a : doc.argumentList.arguments) {
                    String tags = (a.tags == null || a.tags.isEmpty()) ? "" : (" (" + String.join(", ", a.tags) + ")");
                    System.out.println("- " + a.name + tags + " - " + (a.description == null ? "" : a.description));
                }
            }

            System.out.println();
            System.out.println("=== Attributes ===");
            if (doc.attributeList != null && doc.attributeList.attributes != null) {
                if (doc.attributeList.name != null) System.out.println(doc.attributeList.name);
                if (doc.attributeList.description != null) System.out.println(doc.attributeList.description);
                for (Attribute at : doc.attributeList.attributes) {
                    System.out.println("- " + at.name + " - " + (at.description == null ? "" : at.description));
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
