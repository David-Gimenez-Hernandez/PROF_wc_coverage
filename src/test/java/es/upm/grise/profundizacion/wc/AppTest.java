package es.upm.grise.profundizacion.wc;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AppTest {

    private static Path testFile = Paths.get("ejemplo.txt");

    private static ByteArrayOutputStream output;

    @BeforeAll
    public static void setup() throws IOException {
        Files.writeString(testFile, "kjdbvws wonvwofjw\n sdnfwijf ooj    kjndfohwouer 21374 vehf\n jgfosj\n\nskfjwoief ewjf\n\n\ndkfgwoihgpw vs wepfjwfin");
    }

    @BeforeEach
    public void setupTest() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterAll
    public static void teardown() {
        try {
            Files.deleteIfExists(testFile);
        } catch (IOException e) {
            System.err.println("Error deleting test file: " + e.getMessage());
            try {
                Thread.sleep(100);
                Files.deleteIfExists(testFile);
            } catch (IOException | InterruptedException ex) {
                System.err.println("Failed to delete test file on retry: " + ex.getMessage());
            }
        }
    }


    @Test
    public void testUsageMessageWhenNoArgs() {
        App.main(new String[]{});
        assertEquals("Usage: wc [-clw file]\n".trim(), output.toString().trim());
    }

    @Test
    public void testUsageMessageWhenWrongArgsLength() {
        App.main(new String[]{"a", "b", "c"});
        assertEquals("Wrong arguments!\n".trim(), output.toString().trim());
    }

    @Test
    public void testUsageMessageWhenNoFile() {
        App.main(new String[]{"-clw", "nofile"});
        assertEquals("Cannot find file: nofile\n".trim(), output.toString().trim());
    }

    @Test
    public void testUsageMessageWhenIOExceptionReadingFile() {
        // We mock the buffered reader so it throws an IOException when it reads
        try (MockedConstruction<BufferedReader> mocked =
                     mockConstruction(BufferedReader.class,
                             (mock, context) -> {
                                 when(mock.read()).thenThrow(new IOException("forced IOException reading File"));
                             })) {
            App.main(new String[]{"-clw", testFile.toString()});
            assertEquals(("Error reading file: " + testFile.toString() + "\n").trim(), output.toString().trim());
        }
    }

    @Test
    public void testUsageMessageWhenWrongStartArg() {
        App.main(new String[]{"clw", testFile.toString()});
        assertEquals("The commands do not start with -\n".trim(), output.toString().trim());
    }

    @Test
    public void testUsageMessageWhenWrongArgs() {
        App.main(new String[]{"-aclw", testFile.toString()});
        assertEquals("Unrecognized command: a\n".trim(), output.toString().trim());
    }

    @Test
    public void testCorrectUsage() {
        try (
                // Mock BufferedReader to avoid real file I/O
                MockedConstruction<BufferedReader> brMock =
                        mockConstruction(BufferedReader.class,
                                (mock, context) -> when(mock.read()).thenReturn(-1));

                // Mock Counter with fixed values to assert for them
                MockedConstruction<Counter> counterMock =
                        mockConstruction(Counter.class,
                                (mock, context) -> {
                                    when(mock.getNumberCharacters()).thenReturn(10);
                                    when(mock.getNumberLines()).thenReturn(2);
                                    when(mock.getNumberWords()).thenReturn(5);
                                })
        ) {
            App.main(new String[]{"-clw", testFile.toString()});
            assertEquals(("10\t2\t5\t" + testFile.toString() + "\n").trim(), output.toString().trim());
        }
    }

    @Test
    public void testAppConstructor() {
        //Needed for coverage
        App app = new App();
        assertNotNull(app);
    }

}
