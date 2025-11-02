import com.algorithms.SCC;
import com.model.Graph;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class SCCTest {

    // Test simple cycle detection
    @Test
    public void testSimpleCycle() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        assertEquals(1, result.getComponents().size());
        assertEquals(3, result.getComponents().get(0).size());
    }

    // Test multiple SCCs
    @Test
    public void testMultipleSCCs() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        assertEquals(2, result.getComponents().size());
    }

    // Test DAG (no cycles)
    @Test
    public void testDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        assertEquals(4, result.getComponents().size());
        // Each component should have size 1
        for (List<Integer> component : result.getComponents()) {
            assertEquals(1, component.size());
        }
    }

    // Test condensation graph
    @Test
    public void testCondensationGraph() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();
        Graph condensation = scc.createCondensedGraph(result);

        assertTrue(condensation.getNumVertices() <= 3);
        assertTrue(condensation.isDirected());
    }

    // Test single vertex
    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1, true);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        assertEquals(1, result.getComponents().size());
        assertEquals(1, result.getComponents().get(0).size());
    }

    // Test complex graph with multiple SCCs
    @Test
    public void testComplexGraph() {
        Graph graph = new Graph(8, true);
        // First SCC: 0->1->2->0
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // Second SCC: 3->4->3
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        // Connections between SCCs
        graph.addEdge(2, 3, 1);
        graph.addEdge(4, 5, 1);
        // Isolated vertices
        graph.addEdge(6, 7, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        assertTrue(result.getComponents().size() >= 4);
    }

    // Test getSizes method
    @Test
    public void testGetSizes() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        int[] sizes = result.getSizes();
        assertEquals(result.getComponents().size(), sizes.length);

        int totalVertices = 0;
        for (int size : sizes) {
            totalVertices += size;
        }
        assertEquals(5, totalVertices);
    }

    // Test metrics tracking
    @Test
    public void testMetricsTracking() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        assertNotNull(result.getMetrics());
        assertTrue(result.getMetrics().getOperations() > 0);
        assertTrue(result.getMetrics().getElapsedNanos() > 0);
    }
}