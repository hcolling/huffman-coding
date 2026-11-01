package huffman;

import java.util.Map;

/**
 * Result of optimal alphabetic prefix coding.
 */
public record AlphabeticCodingResult(
        Map<Character, Integer> frequencies,
        Map<Character, String> codes,
        String encodedBits,
        int inputSymbols,
        int distinctSymbols,
        long optimizationNanos,
        long encodingNanos,
        long optimalCost) {

    public long originalBitsAt8BitsPerSymbol() {
        return (long) inputSymbols * 8;
    }

    public long encodedBitCount() {
        return encodedBits.length();
    }

    public double compressionRatioPercent() {
        long original = originalBitsAt8BitsPerSymbol();
        return original == 0 ? 0.0 : 100.0 * encodedBitCount() / original;
    }
}
