package huffman;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AlphabeticCodingTest {
    @Test
    void codesArePrefixFree() {
        var result = AlphabeticCoding.encode("abracadabra", false);
        List<String> codes = result.codes().values().stream().toList();
        for (int i = 0; i < codes.size(); i++) {
            for (int j = 0; j < codes.size(); j++) {
                if (i != j) assertFalse(codes.get(j).startsWith(codes.get(i)));
            }
        }
    }

    @Test
    void codesPreserveSymbolOrder() {
        var result = AlphabeticCoding.encode("cabacbba", false);
        List<Character> symbols = result.codes().keySet().stream().toList();
        List<String> codes = symbols.stream().map(result.codes()::get).toList();
        for (int i = 0; i < codes.size(); i++) {
            for (int j = i + 1; j < codes.size(); j++) {
                assertTrue(codes.get(i).compareTo(codes.get(j)) < 0,
                        "Alphabetic codes must preserve ascending symbol order");
            }
        }
    }

    @Test
    void encodedLengthMatchesDynamicProgrammingCost() {
        String input = "aaabbc";
        var result = AlphabeticCoding.encode(input, false);
        assertEquals(result.optimalCost(), result.encodedBitCount());
    }

    @Test
    void handlesEmptyAndSingleSymbolInputs() {
        assertEquals(0, AlphabeticCoding.encode("", false).encodedBitCount());
        var oneSymbol = AlphabeticCoding.encode("xxxxx", false);
        assertEquals("0", oneSymbol.codes().get('x'));
        assertEquals("00000", oneSymbol.encodedBits());
    }

    @Test
    void dynamicProgrammingCanDifferFromUnrestrictedHuffman() {
        // This input has frequencies a=1, b=1, c=3, d=1.
        String input = "abcccd";
        var dp = AlphabeticCoding.encode(input, false);
        var greedy = Huffman.encode(input, false);
        assertTrue(dp.encodedBitCount() > greedy.encodedBitCount());
    }
}
