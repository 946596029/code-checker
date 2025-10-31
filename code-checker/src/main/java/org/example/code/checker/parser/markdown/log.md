## Markdown
### Markdown 需要处理的情况
#### 满足工作需要的情况
    1. 不同级别的标题 ok 语法通过
    ```markdown
    # cdn_top_referrer_statistics
    ## Example Usage
    ```
    ```
    test case
    <header:1><space>Markdown
    <header:2><space>Title 2
    ```
---
    2. 文本描述 ok 语法通过
    ```markdown
    Use this data source to get the TOP100 referrer statistics of CDN domain within HuaweiCloud.
    对于文本中特殊符号的处理
    Domain names are separated by the comma (,) character, for example, **"www.test1.com,www.test2.com"**.
    The value all indicates that all domain names under your account are queried.
    ```
    ```
    test case
    Text after list **what** 123 (what for)
    ```
---
    3. 属性描述  ok 语法通过
    ```markdown
    * `stat_type` - (Required, String) Specifies the statistical type of the query.
    ```
---
    4. 列表描述
    ```markdown
    The valid values are as follows:
    + **mainland_china**: mainland China
    + **outside_mainland_china**: outside mainland China
    + **global**: global
    ```
---
    5. 引用
    ```markdown
    * `statistics` - The list of TOP100 referrer statistics that matched filter parameters.  
    The [statistics](#cdn_top_refer_statistics) structure is documented below.

    <a name="cdn_top_refer_statistics"></a>
    The `statistics` block supports:
    ```
--- 
    6. 代码样例
    ```markdown
    ## Example Usage

    ```hcl
    variable "domain_name" {}
    variable "start_time" {}
    variable "end_time" {}
    
    data "huaweicloud_cdn_top_referrer_statistics" "test" {
      domain_name = var.domain_name
      start_time  = var.start_time
      end_time    = var.end_time
      stat_type   = "req_num"
    }
    ```
    ```

### 过程
    1. 需要检查哪些符号需要进行预处理
    2. 需要针对案例描绘出语法树

### 注意
    1. 语法结构需要时 markdown 通用的,不能将业务逻辑描述进去

### Markdown 预处理
1. 将 markdown 文本中的 符号 转换为 token
    + 将标题头全部转换为 <header:n>
    + 将缩进空格转换为 <indent>
    + 将制表符转换为 <tab>
    + 将航模空格转换为 <space>
    + 将空行转换为 <black:n>

### 清理构建产物并编译
1. 清理构建产物
  + mvn clean package
2. 执行主程序
  + mvn -q exec:java
3. 生成 antlr4 程序
  + mvn -q antlr4-generate