//package org.example.flow.engine;
//
//import org.example.flow.engine.analyzer.TaskGraphAnalyzer;
//
//import java.util.List;
//
///**
// * Simple flow engine that executes {@link TaskNode} instances
// * according to their dependency graph.
// */
//public class FlowEngine {
//
//    private final TaskGraphAnalyzer analyzer = new TaskGraphAnalyzer();
//
//    /**
//     * Execute given task nodes in the order defined by their
//     * dependencies. Nodes with no dependencies will be executed
//     * first, followed by nodes whose dependencies have already
//     * completed.
//     *
//     * @param nodes list of task nodes to execute
//     */
//    public void execute(List<TaskNode> nodes) {
//        List<TaskNode> ordered = analyzer.topologicalSort(nodes);
//        for (TaskNode node : ordered) {
//            node.execute();
//        }
//    }
//}
