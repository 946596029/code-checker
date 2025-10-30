package org.example.code.checker.parser.markdown.preprocess.rule;

public class HeaderRule extends PreProcessRule {

    public HeaderRule() {
        super("将标题头全部转换为 <header:n>");
    }

    @Override
    public String preProcess(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] lines = text.split("\n", -1);
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(convertHeader(line)).append("\n");
        }
        return result.toString().trim();
    }

    private String convertHeader(String line) {
        int level = 0;
        for (char c : line.toCharArray()) {
            if (c == '#') {
                level++;
            } else {
                break;
            }
        }
        if (level == 0) {
            return line;
        } else {
            return "<header:" + level + ">" + line.substring(level);
        }
    }
}