package org.example.code.checker.parser.markdown.preprocess.rule;

public class IndentRule extends PreProcessRule {

    public IndentRule() {
        super("将缩进空格转换为 <indent>");
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
            
            String replaceLine = "";
            int replaceCount = 0;
            int spaceCount = 0;
            for (char c : line.toCharArray()) {
                if (c == ' ') {
                    spaceCount ++;
                    replaceCount++;
                } else {
                    // 替换缩进空格为 <indent:n>
                    if (spaceCount > 0) {
                        replaceLine += "<indent:" + spaceCount + ">";
                        spaceCount = 0;
                    }
                
                    if (c == '\t') {
                        replaceLine += "<tab>";
                        replaceCount++;    
                    } else {
                        break;
                    }
                }
            }

            result.append(replaceLine).append(line.substring(replaceCount)).append("\n");
        }

        return result.toString().trim();
    }
}
