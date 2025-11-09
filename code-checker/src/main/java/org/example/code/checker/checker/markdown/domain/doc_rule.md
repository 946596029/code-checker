# 文档规则
## 作用于 Document 节点
1. 要求块顺序按照如下顺序
    FrontMatter，Title，Example Usage，Arguments, Attributes, Import, Timeout
2. 其中 FrontMatter，Title，Example Usage，Arguments, Attributes 不能为null
3. 整篇文章不可以有多余空格

## 作用于 FrontMatter 节点
1. description 源代码要符合格式
    ```
    description: |-
      Use this resource to manage a channel member within HuaweiCloud.
    ```
2. description 描述必须符合格式 use this (data source/resource) to do something within HuaweiCloud.

## 作用于 Title 节点
1. Title 节点描述需要与 FrontMatter 节点描述保持一致

## 作用于 ExampleUsage 节点
1. 只有一个样例时，无需副标题
2. 多个样例时，需要副标题
3. HCL 代码检查
   1. 应用 HCL 检查规则

## 作用于 Arguments 节点
1. 必须使用标题 Argument Reference
2. 必须使用描述 The following arguments are supported:
3. 参数规则
    1. 