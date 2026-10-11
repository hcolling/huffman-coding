package huffman;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws IOException {
        // Default to a bundled sample file, but allow a different input path to be passed in.
        Path inputPath = args.length > 0 ? Path.of(args[0]) : Path.of("tests/test1_small");

        // The second argument decides whether to print the merge-by-merge decision trace.
        boolean trace = args.length < 2 || Boolean.parseBoolean(args[1]);
        String input = Files.readString(inputPath, StandardCharsets.UTF_8);

        System.out.println("=== Huffman Coding ===");
        System.out.println("File: " + inputPath);
        System.out.println("Input Symbols: " + input.length());
        HuffmanResult result = Huffman.encode(input, trace);

        System.out.println("\nGenerated codes:");
        for (Map.Entry<Character, String> entry : result.codes().entrySet()) {
            System.out.printf("'%s' -> %s (frequency: %d)%n",
                    Huffman.printable(entry.getKey()), entry.getValue(),
                    result.frequencies().get(entry.getKey()));
        }
        System.out.println("\nEncoded sequence:");
        System.out.println(result.encodedBits());
        System.out.println("\n=== Statistics ===");
        System.out.println("Distinct symbols: " + result.distinctSymbols());
        System.out.println("Encoded bits: " + result.encodedBitCount());
        System.out.println("Estimated original bits (8 bits per symbol): " + result.originalBitsAt8BitsPerSymbol());
        System.out.printf("Encoded size / original size: %.2f%%%n", result.compressionRatioPercent());
        System.out.printf("Tree construction: %.3f ms%n", result.buildNanos() / 1_000_000.0);
        System.out.printf("Encoding: %.3f ms%n", result.encodeNanos() / 1_000_000.0);
        System.out.println("Note: timings vary by machine, JVM, and system load.");
    }
}
