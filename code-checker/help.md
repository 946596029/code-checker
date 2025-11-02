## Code Checker - Project Guide

### 1. Overview
This project provides a Markdown preprocessing and parsing pipeline. The preprocessing phase converts
Markdown source into normalized tokens (e.g., `<header:n>`, `<INDENT:n>`, `<tab>`, `<blank[:n]>`). The
ANTLR grammar then parses the normalized text to build a tree for further analysis or transformations.

Key components:
- Preprocessor: applies rules like header conversion and indentation tokenization.
- ANTLR grammars: `MdLexer.g4` and `MdParser.g4` define the token stream and parse rules.
- Runners: `Main` demonstrates preprocessing and parsing. You can add your own entry points as needed.

### 2. Requirements
- Java 11 or newer
- Maven 3.8+ (with network access to download dependencies)
- PowerShell or any shell capable of running Maven on your OS

### 3. Project Structure (relevant parts)
```
code-checker/
  code-checker/
    pom.xml
    src/
      main/
        java/
          org/example/code/checker/parser/markdown/
            preprocess/
              PreProcessor.java
              rule/
                HeaderRule.java
                IndentRule.java
            parser/
              MdLexer.g4
              MdParser.g4
              MdGrammar.java (example runner, optional)
        resources/
      test/
```

Note: For multi-language grammar support, prefer placing all `.g4` files under `src/main/antlr4/...`.
Generated sources will be emitted into `target/generated-sources/antlr4` and automatically included in
the classpath.

### 4. First-Time Setup
1) Ensure Java and Maven are installed and on your PATH.
2) From the module folder (the one containing `pom.xml`), run a compile to warm caches:
```
mvn -q compile
```

### 5. Build, Clean, and Rebuild
- Clean and build:
```
mvn clean package
```
- Force dependency updates and build:
```
mvn clean package -U
```
- Install into local repository:
```
mvn clean install -U
```
- Purge local repo cache for this project and rebuild (heavier):
```
mvn dependency:purge-local-repository -DreResolve=true
mvn clean package -U
```

### 6. Running Examples
Run the main demonstration which preprocesses input, parses it, and prints a tree:
```
mvn --% -q -Dexec.mainClass=org.example.Main exec:java
```

If you add a package-qualified runner (e.g., `org.example.code.checker.checker.markdown.parser.MdGrammar`),
invoke it by changing `-Dexec.mainClass` accordingly.

### 7. ANTLR Generation
You can generate parser/lexer sources via the Maven plugin or standalone JAR. Recommended is the Maven
plugin approach.

Recommended layout for grammars (multi-language friendly):
```
src/main/antlr4/org/example/code/checker/parser/markdown/parser/*.g4
src/main/antlr4/org/example/code/checker/parser/sql/parser/*.g4
src/main/antlr4/org/example/code/checker/parser/json/parser/*.g4
```

Minimal Maven plugin configuration (in `pom.xml`):
```
<build>
  <plugins>
    <plugin>
      <groupId>org.antlr</groupId>
      <artifactId>antlr4-maven-plugin</artifactId>
      <version>4.13.1</version>
      <executions>
        <execution>
          <goals>
            <goal>antlr4</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <visitor>true</visitor>
        <listener>true</listener>
        <sourceDirectory>${project.basedir}/src/main/antlr4</sourceDirectory>
        <includes>
          <include>**/*.g4</include>
        </includes>
        <!-- Prefer default output to target/generated-sources/antlr4 -->
      </configuration>
    </plugin>
  </plugins>
</build>

<dependencies>
  <dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.13.1</version>
  </dependency>
</dependencies>
```

Generate sources and compile:
```
mvn -q generate-sources
mvn -q compile
```

Package names: Either add an ANTLR header in each `.g4`:
```
@header { package org.example.code.checker.checker.markdown.parser; }
```
or set `packageName` in the plugin execution when you need per-language segregation.

### 8. Preprocessing Contract
The preprocessor normalizes Markdown lines. Current rules include:
- Header: convert `#...` to `<header:n>` followed by the original line minus `#` prefix.
- Indentation: convert leading spaces and tabs to `<INDENT:n>` where tabs count as 4 spaces.
- Tabs: optionally represent as `<tab>` if present in content (configurable per rule design).
- Blank lines: suggested token is `<blank>` and `<blank:n>` for runs of blank lines.

Ensure downstream grammars match the chosen token surface (case and exact spelling).

### 9. Encoding and Console Output
Prefer UTF-8 everywhere to avoid mojibake:
- Save all source files as UTF-8 (no BOM).
- Set for Maven and JVM:
```
setx MAVEN_OPTS "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
```
or for the current PowerShell session:
```
$env:MAVEN_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
```
- Switch console code page (Windows):
```
chcp 65001
```

In `pom.xml`, set:
```
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  <maven.compiler.source>11</maven.compiler.source>
  <maven.compiler.target>11</maven.compiler.target>
  <!-- Optional: default charset for exec runs -->
  <file.encoding>UTF-8</file.encoding>
  <sun.jnu.encoding>UTF-8</sun.jnu.encoding>
</properties>
```

### 10. Debugging in Cursor (VS Code engine)
1) Install “Extension Pack for Java”.
2) Put breakpoints in `org.example.Main` or any runner.
3) Press F5 to start debugging. A minimal `launch.json`:
```
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug Main",
      "request": "launch",
      "mainClass": "org.example.Main",
      "projectName": "code-checker"
    }
  ]
}
```

### 11. Common Commands
```
# Build
mvn clean package

# Run main class
mvn --% -q -Dexec.mainClass=org.example.Main exec:java

# Generate ANTLR sources then compile
mvn -q generate-sources && mvn -q compile

# Force dependency update
mvn clean package -U

# Purge local repo entries for current project and rebuild
mvn dependency:purge-local-repository -DreResolve=true
mvn clean package -U
```

### 12. Troubleshooting
- Maven cannot find `pom.xml`:
  Ensure you run commands in the module directory containing `pom.xml`, or use `-f <path-to-pom>`.
- Class not found when running with `exec:java`:
  Run `mvn -q compile` first. Then set `-Dexec.mainClass` to the fully qualified class name.
- Garbled console output (mojibake):
  Ensure shell code page is UTF-8 and `MAVEN_OPTS` sets `-Dfile.encoding=UTF-8`.
- ANTLR generation not triggered:
  Confirm `.g4` files are under `src/main/antlr4` and the plugin execution is configured.
- Parser errors like "no viable alternative":
  Verify the preprocessor emits tokens exactly matching the lexer rules and adjust grammar order so
  structural tokens are recognized before generic `TEXT`.

### 13. Contributing
1) Fork the repository.
2) Create a feature branch.
3) Write tests where applicable.
4) Submit a pull request with a clear description of the change.


