package lab;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphPathFinderTest {

    private GraphPathFinder initGraph() {
        GraphPathFinder g = new GraphPathFinder();
        g.loadFromText("To explore strange new worlds,\nTo seek out new life and new civilizations");
        return g;
    }

    @Test
    public void testShortestPathExists() {
        GraphPathFinder g = initGraph();
        String expected = "Shortest path from \"to\" to \"life\":\n" +
                "to -> explore -> strange -> new -> life\n" +
                "Path weight = 4";
        assertEquals(expected, g.calcShortestPath("to", "life"));
    }

    @Test
    public void testWord1NotFound() {
        GraphPathFinder g = initGraph();
        assertEquals("No \"banana\" in the graph!", g.calcShortestPath("banana", "life"));
    }

    @Test
    public void testWord2NotFound() {
        GraphPathFinder g = initGraph();
        assertEquals("No \"dragon\" in the graph!", g.calcShortestPath("to", "dragon"));
    }
}

