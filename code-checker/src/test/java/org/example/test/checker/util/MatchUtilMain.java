package org.example.test.checker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchUtilMain {

    public static void main(String[] args) {
        String mdContent =  """
                ---
                title: 第一个文档
                date: 2025-11-22
                ---
                """;

        Pattern FrontMatterDetector = Pattern.compile(
                "^---\\s*\\r?\\n([\\s\\S]*?)\\r?\\n---\\s*$",
                Pattern.MULTILINE);
        // 探测是否存在前置元信息
        Matcher matcher = FrontMatterDetector.matcher(mdContent);
        // 不存在则返回null
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}
