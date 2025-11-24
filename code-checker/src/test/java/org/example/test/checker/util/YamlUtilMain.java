package org.example.test.checker.util;

import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

public class YamlUtilMain {

    public static void main(String[] args) {
        // 1. 任意一段 YAML 文本
        String yamlText = """
                title: 测试文档
                date: 2025-11-22
                tags:
                  - Java
                  - 正则
                  - SnakeYAML
                author:
                  name: Alice
                  email: alice@example.com
                """;

        // 2. 加载
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlText);

        // 3. 直接取值
        System.out.println("标题: " + data.get("title"));
        System.out.println("日期: " + data.get("date"));
        List<String> tags = (List<String>) data.get("tags");
        System.out.println("第一个标签: " + tags.get(0));

        Map<String, String> author = (Map<String, String>) data.get("author");
        System.out.println("作者邮箱: " + author.get("email"));
    }
}
