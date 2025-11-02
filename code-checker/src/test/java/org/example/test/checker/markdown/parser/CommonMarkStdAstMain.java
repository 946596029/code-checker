package org.example.test.checker.markdown.parser;

import org.example.code.checker.checker.markdown.parser.MdAstGenerator;
import org.example.code.checker.checker.markdown.parser.ast.MdAstNode;
import org.example.code.checker.checker.markdown.utils.FileUtils;
import org.example.code.checker.checker.markdown.utils.StdAstPrinter;

import java.io.IOException;

public class CommonMarkStdAstMain {

    public static final String FILE_PATH = "C:\\Users\\Administrator\\Desktop\\cdn_ip_information.md";

    public static void main(String[] args) {
        try {
            String path = FILE_PATH;
            if (args != null && args.length > 0 && args[0] != null && !args[0].isEmpty()) {
                path = args[0];
            }
            String mdContent = FileUtils.getFileContent(path);
            MdAstNode root = MdAstGenerator.generateStandardAst(mdContent, path);
            StdAstPrinter.print(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


