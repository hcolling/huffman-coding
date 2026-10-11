package huffman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HuffmanTest {
    @Test
    void codesArePrefixFree() {
        // Prefix-free codes are essential: no code may be the prefix of another, or decoding becomes ambiguous.
        HuffmanResult result = Huffman.encode("abracadabra", false);
        var codes = result.codes().values().stream().toList();
        for (int i = 0; i < codes.size(); i++) {
            for (int j = 0; j < codes.size(); j++) {
                if (i != j) assertFalse(codes.get(j).startsWith(codes.get(i)));
            }
        }
    }

    @Test
    void encodedLengthMatchesWeightedCodeLengths() {
        // The encoded bit count should exactly equal the sum of each symbol's frequency times its code length.
        String input = "aaabbc";
        HuffmanResult result = Huffman.encode(input, false);
        long weightedLength = result.frequencies().entrySet().stream()
                .mapToLong(e -> (long) e.getValue() * result.codes().get(e.getKey()).length())
                .sum();
        assertEquals(weightedLength, result.encodedBitCount());
    }

    @Test
    void handlesEmptyAndSingleSymbolInputs() {
        // The empty case should encode to zero bits, and a one-symbol tree gets a minimal single-bit code.
        assertEquals(0, Huffman.encode("", false).encodedBitCount());
        HuffmanResult oneSymbol = Huffman.encode("xxxxx", false);
        assertEquals("0", oneSymbol.codes().get('x'));
        assertEquals("00000", oneSymbol.encodedBits());
    }

    @Test
    void frequencyCountsCoverAllInputSymbols() {
        // A frequency map is valid only if it accounts for every character in the input, no more and no less.
        String input = "banana";
        HuffmanResult result = Huffman.encode(input, false);
        assertEquals(input.length(), result.frequencies().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(3, result.distinctSymbols());
    }
}
