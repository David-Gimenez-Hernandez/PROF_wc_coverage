package es.upm.grise.profundizacion.wc;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CounterTest {

    @Test
    public void testEmptyInput() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(""));
        Counter counter = new Counter(reader);
        assertEquals(0, counter.getNumberCharacters());
        assertEquals(0, counter.getNumberLines());
        assertEquals(0, counter.getNumberWords());
    }

    @Test
    public void testCountCharactersWordsAndLines() throws IOException {
        String content = "Esta frase\nes un ejemplo para\nel test de recuento.\n";
        BufferedReader reader = new BufferedReader(new StringReader(content));
        
        Counter counter = new Counter(reader);
        
        assertEquals(51, counter.getNumberCharacters());
        assertEquals(3, counter.getNumberLines());
        assertEquals(10, counter.getNumberWords());
    }
   

}

