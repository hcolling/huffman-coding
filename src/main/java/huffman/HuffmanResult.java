package huffman;

import java.util.Map;

public record HuffmanResult(
        Map<Character, Integer> frequencies,
        Map<Character, String> codes,
        String encodedBits,
        int inputSymbols,
        int distinctSymbols,
        long buildNanos,
        long encodeNanos) {

    // This is the naive baseline: if every symbol were stored as a plain 8-bit char, this is the size we'd expect.
    public long originalBitsAt8BitsPerSymbol() {
        return (long) inputSymbols * 8;
    }

    // The encoded stream is just the concatenated bit codes for each input symbol.
    public long encodedBitCount() {
        return encodedBits.length();
    }

    // Compare the encoded bit count against the uncompressed 8-bit estimate as a simple percentage.
    public double compressionRatioPercent() {
        long original = originalBitsAt8BitsPerSymbol();
        return original == 0 ? 0.0 : 100.0 * encodedBitCount() / original;
    }
}
