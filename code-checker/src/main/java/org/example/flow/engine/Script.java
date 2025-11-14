package org.example.flow.engine;

public class Script {
}

//import org.jgrapht.*;
//import org.jgrapht.graph.*;
//import org.jgrapht.nio.*;
//import org.jgrapht.nio.dot.*;
//import guru.nidi.graphviz.engine.*;
//
//// 创建有向依赖图
//Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
//graph.addVertex("validate");
//graph.addVertex("fetch");
//graph.addVertex("process");
//graph.addEdge("validate", "fetch");
//graph.addEdge("fetch", "process");
//
//// 拓扑排序（自动解析执行顺序）
//TopologicalOrderIterator<String, DefaultEdge> orderIterator =
//        new TopologicalOrderIterator<>(graph);
//orderIterator.forEachRemaining(nodeId -> System.out.println("执行: " + nodeId));
//
//// 导出Graphviz DOT格式并渲染为图片
//DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
//exporter.exportGraph(graph, new FileWriter("graph.dot"));
//
//// 渲染为PNG
//        Graphviz.fromFile(new File("graph.dot"))
//        .render(Format.PNG)
//        .toFile(new File("dependency.png"));