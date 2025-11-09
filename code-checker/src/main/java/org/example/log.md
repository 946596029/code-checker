# 功能设计

## Markdown 代码规范检查
1. markdown 语法解析                完成
    原计划通过 antlr4 进行解析
    替换为 markdown common java 包进行解析
2. 业务规则代码结构设计             完成
    源代码 => AST => 领域模型 => 规则检查
        AST => 领域模型 相当于做语义处理
3. 设计领域模型                    完成
    划分两层
        基础的Markdown层
        具体的业务层
            具体的业务层平铺，无父子关系
    同时改写 BusinessParser
    
    领域模型实现的问题
        错误处理不足
            缺少对解析失败的处理
                引入解析结果封装（成功、失败）
            缺少对格式不符的验证与提示
                收集并宝宝解析错误和警告
        边界情况
            对缺失节点，格式异常的处理不够健壮

    领域对象不应该持有 AST 节点
        以 纯业务数据 + 轻量来源引用，不直接持有完整 AST
    可以细化领域对象到行级别以方便校验器使用
4. 编写领域模型检查规则


今天要做的事情
    1. 写完 Business 层 Domain
        需要 MarkdownDomainBuilder 生成的 Domain 树是可进行遍历的
            StdBlock
            StdInline
            Section
            添加一个 walker 用于 Markdown Domain 的遍历
        并需要一个用于寻找节点的工具类
            现在的时间已经不够写一个完备的工具类了，只需要编写要用的就可以了
        编写的 Business Domain 内容由 Markdown Domain 组装
            并不直接按照业务粒度来组装，业务粒度分析在规则中进行分析
    2. 编写第一个 Markdown 的检查规则

