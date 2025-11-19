# 功能设计

## 代码结构设计
处理过程分为以下几层
1. Raw Code => AST Node
将源代码解析为语法树
2. AST Node => Standard Domain
将语法树转换为针对语言建模的领域模型
方便业务处理
3. Standard Domain => Check Rule
使用 kotlin 编写
先检查结构完整性，再检查内容规范
   1. Structure Check Rule
   2. Content Check Rule

## 待做事项
[x] 将具有树节点性质的类继承 TreeNode，避免重复定义树工具方法，修改对应的工具类和程序逻辑  
[x] 使用图论工具，建立依赖图，计算拓扑排序  
[x] 可视化依赖图  mermaid
[ ] Go 语言解析  
[ ] Hcl 语言解析  


