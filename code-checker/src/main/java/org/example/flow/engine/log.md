1. JGraphT + Graphviz-Java（最轻量、最灵活）
   JGraphT 是纯Java的图论算法库，负责依赖关系管理；配合 Graphviz-Java 实现可视化。

// 这是一个典型的依赖图/工作流场景，推荐采用 "依赖注入 + 命令模式 + 访问者模式" 的组合设计，能有效解耦节点逻辑与依赖关系管理。以下是具体实现方案：
// 1. 定义节点接口（将业务逻辑抽象为可执行单元）
public interface Node {
String getId();                    // 节点唯一标识
Set<String> getDependencies();     // 依赖的节点ID
void execute(ExecutionContext context);  // 执行业务逻辑
}

// 2. 执行上下文（传递共享数据）
public class ExecutionContext {
private final Map<String, Object> results = new ConcurrentHashMap<>();
private final Map<String, Exception> errors = new ConcurrentHashMap<>();

    public void setResult(String nodeId, Object result) { /* ... */ }
    public Object getResult(String nodeId) { /* ... */ }
    public boolean hasError(String nodeId) { /* ... */ }
}

// 节点基类（简化实现）
public abstract class AbstractNode implements Node {
private final String id;
private final Set<String> dependencies;

    protected AbstractNode(String id, String... dependencies) {
        this.id = id;
        this.dependencies = Set.of(dependencies);
    }
    
    @Override public String getId() { return id; }
    @Override public Set<String> getDependencies() { return dependencies; }
}

// 具体业务节点示例
public class DataFetchNode extends AbstractNode {
public DataFetchNode() { super("fetch", "validate"); }

    @Override
    public void execute(ExecutionContext context) {
        // 从依赖节点获取数据
        Object validationResult = context.getResult("validate");
        
        // 执行业务逻辑
        String data = fetchFromDatabase();
        
        // 存储结果
        context.setResult(getId(), data);
    }
    
    private String fetchFromDatabase() {
        // 实际业务逻辑
        return "fetched-data";
    }
}

public class ProcessNode extends AbstractNode {
public ProcessNode() { super("process", "fetch"); }

    @Override
    public void execute(ExecutionContext context) {
        String data = (String) context.getResult("fetch");
        String processed = data.toUpperCase();
        context.setResult(getId(), processed);
    }
}

public class NodeExecutor {
private final Map<String, Node> nodeRegistry = new HashMap<>();

    // 注册所有节点
    public void registerNode(Node node) {
        nodeRegistry.put(node.getId(), node);
    }
    
    // 拓扑排序解析执行顺序
    public List<Node> resolveExecutionOrder(String targetNodeId) {
        Set<String> visited = new HashSet<>();
        List<Node> executionOrder = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        
        topologicalSort(targetNodeId, visited, stack);
        
        while (!stack.isEmpty()) {
            String nodeId = stack.pop();
            executionOrder.add(nodeRegistry.get(nodeId));
        }
        
        return executionOrder;
    }
    
    private void topologicalSort(String nodeId, Set<String> visited, 
                                 Deque<String> stack) {
        if (visited.contains(nodeId)) {
            if (stack.contains(nodeId)) {
                throw new IllegalStateException("检测到循环依赖: " + nodeId);
            }
            return;
        }
        
        visited.add(nodeId);
        Node node = nodeRegistry.get(nodeId);
        
        for (String dep : node.getDependencies()) {
            topologicalSort(dep, visited, stack);
        }
        
        stack.push(nodeId);
    }
    
    // 执行节点链
    public void execute(String targetNodeId) {
        List<Node> orderedNodes = resolveExecutionOrder(targetNodeId);
        ExecutionContext context = new ExecutionContext();
        
        for (Node node : orderedNodes) {
            try {
                // 检查依赖是否成功
                if (!allDependenciesSuccess(node, context)) {
                    context.recordError(node.getId(), 
                        new RuntimeException("依赖执行失败"));
                    continue;
                }
                
                node.execute(context);
            } catch (Exception e) {
                context.recordError(node.getId(), e);
                // 根据需求决定：抛出异常 或 继续执行
                throw new NodeExecutionException(node.getId(), e);
            }
        }
    }
    
    private boolean allDependenciesSuccess(Node node, ExecutionContext context) {
        return node.getDependencies().stream()
            .noneMatch(context::hasError);
    }
}