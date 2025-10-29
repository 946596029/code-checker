package org.example.code.checker.parser.markdown.preprocess.rule;

public class IndentRule extends PreProcessRule {

    public IndentRule(String name) {
        super(name);
    }

    @Override
    public String preProcess(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n", -1);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // 找到实际缩进字符的长度和等效缩进级别
            IndentInfo indentInfo = getIndentInfo(line);
            
            // 如果行首有缩进，将其替换为 token
            if (indentInfo.charCount > 0) {
                String content = line.substring(indentInfo.charCount);
                // 将缩进转换为 token 格式: <INDENT:count>
                result.append("<INDENT:").append(indentInfo.equivalentSpaces).append(">").append(content);
            } else {
                result.append(line);
            }
            
            // 如果不是最后一行，添加换行符
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    /**
     * 缩进信息类
     */
    private static class IndentInfo {
        int charCount;           // 实际缩进字符数（用于截取）
        int equivalentSpaces;   // 等效空格数（用于 token，Tab 按 4 个空格计算）
    }

    /**
     * 计算行首的缩进信息（包括空格和Tab）
     * Tab 按照标准约定转换为 4 个空格计算等效缩进
     */
    private IndentInfo getIndentInfo(String line) {
        IndentInfo info = new IndentInfo();
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                info.charCount++;
                info.equivalentSpaces++;
            } else if (c == '\t') {
                info.charCount++;
                // Tab 转换为 4 个空格
                info.equivalentSpaces += 4;
            } else {
                break;
            }
        }
        return info;
    }
}
