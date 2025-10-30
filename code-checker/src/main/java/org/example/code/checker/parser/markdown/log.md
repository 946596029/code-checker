## Markdown
### Markdown 预处理
1. 将 markdown 文本中的 符号 转换为 token
    + 将标题头全部转换为 <header:n>
    + 将缩进空格转换为 <indent>
    + 将制表符转换为 <tab>
    + 将航模空格转换为 <space>
    + 将空行转换为 <black:n>


### 清理构建产物并编译
1. mvn clean package
2. mvn -q exec:java