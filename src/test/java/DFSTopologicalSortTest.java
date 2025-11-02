import com.algorithms.DFSTopologicalSort;
import com.algorithms.KahnTopologicalSort;
import com.model.Graph;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class DFSTopologicalSortTest {

    // Test simple DAG with DFS topological sort
    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertTrue(result.isDAG());
        assertEquals(4, result.getOrder().size());

        // Verify ordering constraints
        List<Integer> order = result.getOrder();
        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);
        int pos3 = order.indexOf(3);

        assertTrue(pos0 < pos1);
        assertTrue(pos0 < pos2);
        assertTrue(pos1 < pos3);
        assertTrue(pos2 < pos3);
    }

    // Test linear chain
    @Test
    public void testLinearChain() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertTrue(result.isDAG());
        assertEquals(5, result.getOrder().size());
    }

    // Test empty graph
    @Test
    public void testEmptyGraph() {
        Graph graph = new Graph(1, true);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertTrue(result.isDAG());
        assertEquals(1, result.getOrder().size());
    }

    // Test complex DAG
    @Test
    public void testComplexDAG() {
        Graph graph = new Graph(6, true);
        graph.addEdge(5, 2, 1);
        graph.addEdge(5, 0, 1);
        graph.addEdge(4, 0, 1);
        graph.addEdge(4, 1, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertTrue(result.isDAG());
        assertEquals(6, result.getOrder().size());
    }

    // Test disconnected components
    @Test
    public void testDisconnectedComponents() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertTrue(result.isDAG());
        assertEquals(6, result.getOrder().size());
    }

    // Compare DFS with Kahn's algorithm
    @Test
    public void testCompareWithKahn() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);

        KahnTopologicalSort.TopoResult dfsResult = dfs.performSort();
        KahnTopologicalSort.TopoResult kahnResult = kahn.computeTopoOrder();

        assertEquals(dfsResult.isDAG(), kahnResult.isDAG());
        assertEquals(dfsResult.getOrder().size(), kahnResult.getOrder().size());
    }

    // Test graph with multiple sources
    @Test
    public void testMultipleSources() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(2, 4, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertTrue(result.isDAG());
        assertEquals(5, result.getOrder().size());
    }

    // Test metrics tracking
    @Test
    public void testMetricsTracking() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        KahnTopologicalSort.TopoResult result = dfs.performSort();

        assertNotNull(result.getMetrics());
        assertTrue(result.getMetrics().getOperations() > 0);
        assertTrue(result.getMetrics().getElapsedNanos() > 0);
    }
}