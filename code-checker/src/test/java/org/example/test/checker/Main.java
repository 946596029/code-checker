package org.example.test.checker;

import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.utils.FileUtils;

public class Main {
    public static void main(String[] args) {
//        String filePath;
//        if (args != null && args.length > 0) {
//            filePath = args[0];
//        } else {
//            // Default path for convenience; can be overridden by CLI arg
//            filePath = "C:\\Users\\Administrator\\Desktop\\cdn_ip_information.md";
//        }
//
//        try {
//            String md = FileUtils.getFileContent(filePath);
//            MdAstNode root = MdAstGenerator.generateStandardAst(md, filePath);
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
        String msg = new org.example.demo.KotlinSample().greet("World");
        System.out.println(msg);
    }
}
