// package org.example.flow.engine;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Objects;

// /**
//  * Represents a logical task node in a flow.
//  * <p>
//  * Each node has a unique id, a human readable name and a list of
//  * other node ids it depends on. The {@link #execute()} method is
//  * invoked once all its dependencies have been executed.
//  */
// public abstract class TaskNode {

//     private final String id;
//     private final String name;
//     private final List<String> dependencies;

//     protected TaskNode(String id, String name, List<String> dependencies) {
//         if (id == null || id.isEmpty()) {
//             throw new IllegalArgumentException("TaskNode id must not be null or empty");
//         }
//         this.id = id;
//         this.name = name;
//         if (dependencies == null || dependencies.isEmpty()) {
//             this.dependencies = Collections.emptyList();
//         } else {
//             this.dependencies = Collections.unmodifiableList(new ArrayList<>(dependencies));
//         }
//     }

//     public String getId() {
//         return id;
//     }

//     public String getName() {
//         return name;
//     }

//     /**
//      * Returns a list of node ids that this node depends on.
//      */
//     public List<String> getDependencies() {
//         return dependencies;
//     }

//     /**
//      * Execute the task represented by this node.
//      * <p>
//      * Implementations should contain the business logic that needs
//      * to be performed when this node is reached in the flow.
//      */
//     public abstract void execute();

//     @Override
//     public boolean equals(Object o) {
//         if (this == o) {
//             return true;
//         }
//         if (o == null || getClass() != o.getClass()) {
//             return false;
//         }
//         TaskNode taskNode = (TaskNode) o;
//         return id.equals(taskNode.id);
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(id);
//     }
// }
