// package org.example.flow.engine.analyzer;

// import org.example.flow.engine.TaskNode;
// import org.jgrapht.graph.DefaultEdge;
// import org.jgrapht.graph.DirectedAcyclicGraph;
// import org.jgrapht.traverse.TopologicalOrderIterator;

// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// /**
//  * Build a dependency graph for {@link TaskNode} instances and
//  * provide a topological execution order.
//  */
// public class TaskGraphAnalyzer {

//     /**
//      * Build a directed acyclic graph of the given nodes and return
//      * a list of nodes in topological order.
//      *
//      * @param nodes collection of task nodes
//      * @return nodes sorted by dependency order
//      * @throws IllegalArgumentException if there are duplicate ids,
//      *                                  missing dependencies or cycles
//      */
//     public List<TaskNode> topologicalSort(Collection<TaskNode> nodes) {
//         if (nodes == null || nodes.isEmpty()) {
//             return new ArrayList<>();
//         }

//         DirectedAcyclicGraph<TaskNode, DefaultEdge> graph =
//             new DirectedAcyclicGraph<>(DefaultEdge.class);

//         Map<String, TaskNode> nodeById = new HashMap<>();

//         // Register vertices and ensure ids are unique.
//         for (TaskNode node : nodes) {
//             if (node == null) {
//                 continue;
//             }
//             if (nodeById.containsKey(node.getId())) {
//                 throw new IllegalArgumentException(
//                     "Duplicate TaskNode id detected: " + node.getId()
//                 );
//             }
//             nodeById.put(node.getId(), node);
//             graph.addVertex(node);
//         }

//         // Add edges based on dependencies.
//         for (TaskNode node : nodes) {
//             if (node == null) {
//                 continue;
//             }
//             for (String dependencyId : node.getDependencies()) {
//                 TaskNode dependency = nodeById.get(dependencyId);
//                 if (dependency == null) {
//                     throw new IllegalArgumentException(
//                         "Missing dependency '" + dependencyId
//                             + "' for node '" + node.getId() + "'"
//                     );
//                 }
//                 // Directed edge from dependency -> node
//                 graph.addEdge(dependency, node);
//             }
//         }

//         // Topological order iterator will throw if the graph is not acyclic
//         TopologicalOrderIterator<TaskNode, DefaultEdge> iterator =
//             new TopologicalOrderIterator<>(graph);

//         List<TaskNode> ordered = new ArrayList<>();
//         while (iterator.hasNext()) {
//             ordered.add(iterator.next());
//         }
//         return ordered;
//     }
// }


