package org.example.code.checker.checker.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String getFileContent(String filePath) throws IOException {
        Path p = Paths.get(filePath);
        // 按行拼回 \n，去掉末尾换行可再处理
        return String.join("\n", Files.readAllLines(p, StandardCharsets.UTF_8));
    }
}
