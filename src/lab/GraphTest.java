package lab;

import lab.Graph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {

    private Graph graph;
    private Path tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        graph = new Graph();
        String text = "To explore strange new worlds,\nTo seek out new life and new civilizations";
        tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, text);
        graph.buildGraphFromFile(tempFile.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testBridgeWordExists_seek() {
        String result = graph.queryBridgeWords("to", "out");
        assertEquals("The bridge word from \"to\" to \"out\" is: \"seek\".", result);
    }

    @Test
    public void testNoBridgeWordsBetween_strange_civilizations() {
        String result = graph.queryBridgeWords("strange", "civilizations");
        assertEquals("No bridge words from \"strange\" to \"civilizations\"!", result);
    }

    @Test
    public void testWord1NotExist() {
        String result = graph.queryBridgeWords("xxx", "new");
        assertEquals("No \"xxx\" in the graph!", result);
    }

    @Test
    public void testWord2NotExist() {
        String result = graph.queryBridgeWords("new", "yyy");
        assertEquals("No \"yyy\" in the graph!", result);
    }


    @Test
    public void testBothWordsNotExist() {
        String result = graph.queryBridgeWords("unknown", "mystery");
        assertEquals("No \"unknown\" and \"mystery\" in the graph!", result);
    }
}
