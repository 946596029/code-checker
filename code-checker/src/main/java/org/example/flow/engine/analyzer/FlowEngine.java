package org.example.flow.engine.analyzer;

import org.example.flow.engine.node.TaskNode;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple flow engine that executes {@link TaskNode} instances
 * according to their dependency graph using JGraphT.
 */
public class FlowEngine {

    /**
     * Execute given task nodes in the order defined by their dependencies.
     * Nodes with no dependencies will be executed first, followed by nodes
     * whose dependencies have already completed.
     *
     * @param nodes list of task nodes to execute
     */
    public void execute(List<TaskNode> nodes) {
        List<TaskNode> ordered = topologicalSort(nodes);
        for (TaskNode node : ordered) {
            node.execute();
        }
    }

    /**
     * Build a directed acyclic graph from the given task nodes and return
     * a list of nodes in topological order.
     *
     * @param nodes collection of task nodes
     * @return nodes sorted by dependency order
     */
    List<TaskNode> topologicalSort(Collection<TaskNode> nodes) {
        DirectedAcyclicGraph<TaskNode, DefaultEdge> graph = buildGraph(nodes);

        // Topological order iterator will throw if the graph is not acyclic.
        TopologicalOrderIterator<TaskNode, DefaultEdge> iterator =
            new TopologicalOrderIterator<>(graph);

        List<TaskNode> ordered = new ArrayList<>();
        while (iterator.hasNext()) {
            ordered.add(iterator.next());
        }
        return ordered;
    }

    /**
     * Export dependency graph in DOT format using JGraphT IO.
     *
     * @param nodes task nodes participating in the graph
     * @return DOT representation as a String
     */
    public String exportDot(Collection<TaskNode> nodes) {
        DirectedAcyclicGraph<TaskNode, DefaultEdge> graph = buildGraph(nodes);

        DOTExporter<TaskNode, DefaultEdge> exporter =
            new DOTExporter<>(TaskNode::getId);

        StringWriter writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }

    /**
     * Export dependency graph as Mermaid syntax.
     * Example output:
     *   graph TD
     *     validate[validate]
     *     fetch[fetch]
     *     validate --> fetch
     *
     * @param nodes task nodes participating in the graph
     * @return Mermaid representation as a String
     */
    public String exportMermaid(Collection<TaskNode> nodes) {
        DirectedAcyclicGraph<TaskNode, DefaultEdge> graph = buildGraph(nodes);

        StringBuilder sb = new StringBuilder();
        sb.append("graph TD").append(System.lineSeparator());

        // Declare vertices.
        for (TaskNode node : graph.vertexSet()) {
            String id = node.getId();
            sb.append("  ")
                .append(id)
                .append("[")
                .append(id)
                .append("]")
                .append(System.lineSeparator());
        }

        // Declare edges.
        for (DefaultEdge edge : graph.edgeSet()) {
            TaskNode source = graph.getEdgeSource(edge);
            TaskNode target = graph.getEdgeTarget(edge);
            sb.append("  ")
                .append(source.getId())
                .append(" --> ")
                .append(target.getId())
                .append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * Build a directed acyclic graph from the given task nodes.
     *
     * @param nodes collection of task nodes
     * @return directed acyclic graph
     */
    private DirectedAcyclicGraph<TaskNode, DefaultEdge> buildGraph(
        Collection<TaskNode> nodes
    ) {
        if (nodes == null || nodes.isEmpty()) {
            return new DirectedAcyclicGraph<>(DefaultEdge.class);
        }

        DirectedAcyclicGraph<TaskNode, DefaultEdge> graph =
            new DirectedAcyclicGraph<>(DefaultEdge.class);

        Map<String, TaskNode> nodeById = new HashMap<>();

        // Register vertices and ensure ids are unique.
        for (TaskNode node : nodes) {
            if (node == null) {
                continue;
            }
            String id = node.getId();
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException(
                    "TaskNode id must not be null or empty");
            }
            if (nodeById.containsKey(id)) {
                throw new IllegalArgumentException(
                    "Duplicate TaskNode id detected: " + id);
            }
            nodeById.put(id, node);
            graph.addVertex(node);
        }

        // Add edges based on dependencies.
        for (TaskNode node : nodes) {
            if (node == null) {
                continue;
            }
            for (String dependencyId : node.getDependencies()) {
                TaskNode dependency = nodeById.get(dependencyId);
                if (dependency == null) {
                    throw new IllegalArgumentException(
                        "Missing dependency '" + dependencyId
                            + "' for node '" + node.getId() + "'");
                }
                // Edge from dependency -> node.
                graph.addEdge(dependency, node);
            }
        }

        return graph;
    }
}


