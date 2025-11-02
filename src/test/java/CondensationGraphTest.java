import com.algorithms.CondensationGraph;
import com.algorithms.SCC;
import com.model.Graph;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class CondensationGraphTest {

    // Test basic condensation graph creation
    @Test
    public void testBasicCondensation() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        assertTrue(condensed.getComponentCount() <= 5);
        assertTrue(condensed.isDAG());
        assertNotNull(condensed.getCondensationGraph());
    }

    // Test vertex to component mapping
    @Test
    public void testVertexToComponentMapping() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        // Vertices in same SCC should have same component ID
        int comp0 = condensed.getComponentId(0);
        int comp1 = condensed.getComponentId(1);
        int comp2 = condensed.getComponentId(2);

        assertEquals(comp0, comp1);
        assertEquals(comp1, comp2);

        int comp3 = condensed.getComponentId(3);
        int comp4 = condensed.getComponentId(4);
        int comp5 = condensed.getComponentId(5);

        assertEquals(comp3, comp4);
        assertEquals(comp4, comp5);

        // Different SCCs should have different IDs
        assertNotEquals(comp0, comp3);
    }

    // Test getVerticesInComponent
    @Test
    public void testGetVerticesInComponent() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 0, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        int sccWithCycle = condensed.getComponentId(0);
        List<Integer> vertices = condensed.getVerticesInComponent(sccWithCycle);

        assertTrue(vertices.size() >= 3);
        assertTrue(vertices.contains(0));
        assertTrue(vertices.contains(1));
        assertTrue(vertices.contains(2));
    }

    // Test DAG property of condensation
    @Test
    public void testCondensationIsDAG() {
        Graph graph = new Graph(7, true);
        // Create cycles
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 2, 1);
        // Connect components
        graph.addEdge(1, 2, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 6, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        assertTrue(condensed.isDAG());
    }

    // Test export structure
    @Test
    public void testExportStructure() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        String structure = condensed.exportStructure();

        assertNotNull(structure);
        assertTrue(structure.contains("Condensation Graph Structure"));
        assertTrue(structure.contains("Components:"));
    }

    // Test single vertex graph
    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1, true);

        List<List<Integer>> components = new ArrayList<>();
        List<Integer> comp = new ArrayList<>();
        comp.add(0);
        components.add(comp);

        CondensationGraph condensed = new CondensationGraph(graph, components);

        assertEquals(1, condensed.getComponentCount());
        assertEquals(0, condensed.getComponentId(0));
        assertTrue(condensed.isDAG());
    }

    // Test disconnected components
    @Test
    public void testDisconnectedComponents() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        assertTrue(condensed.getComponentCount() >= 2);
        assertTrue(condensed.isDAG());
    }

    // Test no duplicate edges in condensation
    @Test
    public void testNoDuplicateEdges() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(0, 3, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());
        Graph cGraph = condensed.getCondensationGraph();

        // Count edges from SCC containing 0,1,2 to component containing 3
        int sccComp = condensed.getComponentId(0);
        int targetComp = condensed.getComponentId(3);

        if (sccComp != targetComp) {
            int edgeCount = 0;
            for (Graph.Edge edge : cGraph.getEdgesFrom(sccComp)) {
                if (edge.getDestination() == targetComp) {
                    edgeCount++;
                }
            }
            // Should only have one edge despite multiple edges in original graph
            assertEquals(1, edgeCount);
        }
    }

    // Test getComponents
    @Test
    public void testGetComponents() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);

        SCC scc = new SCC(graph);
        SCC.SCCResult result = scc.computeSCCs();

        CondensationGraph condensed = new CondensationGraph(graph, result.getComponents());

        List<List<Integer>> components = condensed.getComponents();

        assertNotNull(components);
        assertEquals(condensed.getComponentCount(), components.size());

        // Verify total vertices
        int totalVertices = 0;
        for (List<Integer> component : components) {
            totalVertices += component.size();
        }
        assertEquals(5, totalVertices);
    }
}
