import com.algorithms.ComponentTS;
import com.algorithms.CondensationGraph;
import com.algorithms.SCC;
import com.model.Graph;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class ComponentTSTest {

    // Test simple condensation graph
    @Test
    public void testSimpleCondensation() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        assertTrue(result.isDAG());
        assertEquals(condensed.getComponentCount(), result.getComponentOrder().size());
        assertEquals(5, result.getTaskOrder().size());
    }

    // Test DAG with no cycles
    @Test
    public void testDAGNoCycles() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        assertTrue(result.isDAG());
        assertEquals(4, result.getComponentOrder().size());
        assertEquals(4, result.getTaskOrder().size());
    }

    // Test multiple SCCs
    @Test
    public void testMultipleSCCs() {
        Graph graph = new Graph(7, true);
        // First SCC: 0->1->2->0
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // Second SCC: 3->4->3
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        // Connection between SCCs
        graph.addEdge(2, 3, 1);
        // Third component
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 6, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        assertTrue(result.isDAG());
        assertEquals(7, result.getTaskOrder().size());
    }

    // Test task order contains all vertices
    @Test
    public void testTaskOrderCompleteness() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        List<Integer> taskOrder = result.getTaskOrder();
        assertEquals(6, taskOrder.size());

        // Verify all vertices are present
        for (int i = 0; i < 6; i++) {
            assertTrue(taskOrder.contains(i));
        }
    }

    // Test single component
    @Test
    public void testSingleComponent() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        assertTrue(result.isDAG());
        assertEquals(1, result.getComponentOrder().size());
        assertEquals(3, result.getTaskOrder().size());
    }

    // Test complex graph structure
    @Test
    public void testComplexStructure() {
        Graph graph = new Graph(8, true);
        // SCC 1: 0->1->0
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        // SCC 2: 2->3->4->2
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 2, 1);
        // Connect SCCs
        graph.addEdge(1, 2, 1);
        // Single nodes
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 6, 1);
        graph.addEdge(6, 7, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        assertTrue(result.isDAG());
        assertEquals(8, result.getTaskOrder().size());
    }

    // Test metrics tracking
    @Test
    public void testMetricsTracking() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult sccResult = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, sccResult.getComponents());
        ComponentTS componentTS = new ComponentTS(condensed);

        ComponentTS.ComponentTopoResult result = componentTS.performSort();

        assertNotNull(result.getMetrics());
        assertTrue(result.getMetrics().getOperations() > 0);
        assertTrue(result.getMetrics().getElapsedNanos() > 0);
    }
}