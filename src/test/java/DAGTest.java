import com.algorithms.DAG;
import com.model.Graph;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class DAGTest {

    // Test shortest path in simple DAG
    @Test
    public void testShortestPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 7);

        DAG dag = new DAG(graph);
        DAG.PathResult result = dag.computeShortestPaths(0);

        assertEquals(0, result.getDistances()[0]);
        assertEquals(5, result.getDistances()[1]);
        assertEquals(3, result.getDistances()[2]);
        assertEquals(7, result.getDistances()[3]);
        assertFalse(result.isLongest());
    }

    // Test longest path in DAG
    @Test
    public void testLongestPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 7);

        DAG dag = new DAG(graph);
        DAG.PathResult result = dag.computeLongestPaths(0);

        assertEquals(10, result.getDistances()[3]);
        assertTrue(result.isLongest());
    }

    // Test path reconstruction
    @Test
    public void testPathReconstruction() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);

        DAG dag = new DAG(graph);
        DAG.PathResult result = dag.computeShortestPaths(0);

        List<Integer> path = result.reconstructPath(3);
        assertEquals(4, path.size());
        assertEquals(Integer.valueOf(0), path.get(0));
        assertEquals(Integer.valueOf(1), path.get(1));
        assertEquals(Integer.valueOf(2), path.get(2));
        assertEquals(Integer.valueOf(3), path.get(3));
    }

    // Test critical path
    @Test
    public void testCriticalPath() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 2);

        DAG dag = new DAG(graph);
        DAG.CriticalPathResult result = dag.findCriticalPath();

        assertTrue(result.getTotalLength() > 0);
        assertFalse(result.getPath().isEmpty());
        assertEquals(9, result.getTotalLength());
    }

    // Test unreachable vertices
    @Test
    public void testUnreachableVertices() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(2, 3, 3);

        DAG dag = new DAG(graph);
        DAG.PathResult result = dag.computeShortestPaths(0);

        assertEquals(0, result.getDistances()[0]);
        assertEquals(5, result.getDistances()[1]);
        assertEquals(Integer.MAX_VALUE, result.getDistances()[2]);
        assertEquals(Integer.MAX_VALUE, result.getDistances()[3]);
    }

    // Test metrics tracking
    @Test
    public void testMetricsTracking() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);

        DAG dag = new DAG(graph);
        DAG.PathResult result = dag.computeShortestPaths(0);

        assertNotNull(result.getMetrics());
        assertTrue(result.getMetrics().getOperations() > 0);
        assertTrue(result.getMetrics().getElapsedNanos() > 0);
    }
}