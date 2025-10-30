## 查看环境版本
  + java --version
  + mvn --version
    + 这里会获取到 maven 目录地址，进入 conf ，配置 settings.xml 中本地仓库地址
        + <localRepository>

---

## 配置编码
**Cursor**

解决 Maven 控制台与编译期中文乱码，常用做法如下（推荐全部使用 UTF-8）：

### 1) 配置项目编码（pom.xml）
在 `pom.xml` 增加属性与插件编码：
```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  <maven.compiler.source>11</maven.compiler.source>
  <maven.compiler.target>11</maven.compiler.target>
</properties>

<build>
  <plugins>
    <plugin>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.11.0</version>
      <configuration>
        <source>${maven.compiler.source}</source>
        <target>${maven.compiler.target}</target>
        <encoding>${project.build.sourceEncoding}</encoding>
      </configuration>
    </plugin>

    <plugin>
      <artifactId>maven-resources-plugin</artifactId>
      <version>3.3.1</version>
      <configuration>
        <encoding>${project.build.sourceEncoding}</encoding>
      </configuration>
    </plugin>
  </plugins>
</build>
```

### 2) 设置 JVM/Maven 进程默认编码
- 临时（当前会话）PowerShell：
```powershell
$env:MAVEN_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
```
- 永久（系统环境变量）设置 `MAVEN_OPTS` 为：
```
-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
```
- 或在项目根创建 `.mvn/jvm.config`（推荐）：
```
-Dfile.encoding=UTF-8
-Dsun.jnu.encoding=UTF-8
```

### 3) Windows 控制台切换为 UTF-8
- PowerShell/CMD 执行：
```powershell
chcp 65001
```
- PowerShell 7+ 通常默认 UTF-8，更稳定。

### 4) Surefire/测试与打包日志也需 UTF-8（可选）
```xml
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.5</version>
  <configuration>
    <argLine>-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8</argLine>
    <forkCount>1</forkCount>
  </configuration>
</plugin>
```

### 5) 本地 Maven 全局/用户配置（可选）
在 `%USERPROFILE%\.m2\settings.xml` 或 `%MAVEN_HOME%\conf\settings.xml` 中添加（没有就新建 `<settings>`）：
```xml
<settings>
  <profiles>
    <profile>
      <id>utf8-default</id>
      <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
      </properties>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>utf8-default</activeProfile>
  </activeProfiles>
</settings>
```

### 6) 验证
- 查看 Maven/Java 进程编码：
```powershell
mvn -v
mvn -q -Dexec.mainClass=org.example.Main exec:java
```
- 程序内打印：
```java
System.out.println(System.getProperty("file.encoding"));
```

若仍有乱码，优先检查：
- 终端编码（`chcp`）与字体是否支持中文。
- 源文件实际保存编码（确保文件本身为 UTF-8）。

---

## 