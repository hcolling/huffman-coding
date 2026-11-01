package huffman;

import java.util.*;

/**
 * Optimal alphabetic binary prefix coding using bottom-up dynamic programming.
 *
 * Unlike classical Huffman coding, the order of the leaves must match the
 * ascending order of the symbols. The interval DP finds the minimum weighted
 * external path length under that additional constraint.
 */
public final class AlphabeticCoding {
    private AlphabeticCoding() {}

    public static AlphabeticCodingResult encode(String input, boolean showDecisions) {
        Objects.requireNonNull(input, "input cannot be null");

        Map<Character, Integer> frequencies = new TreeMap<>();
        for (char symbol : input.toCharArray()) {
            frequencies.merge(symbol, 1, Integer::sum);
        }

        if (showDecisions) {
            System.out.println("Frequencies (ascending symbol order): "
                    + printableFrequencies(frequencies));
        }

        int k = frequencies.size();
        if (k == 0) {
            return new AlphabeticCodingResult(
                    Collections.unmodifiableMap(frequencies),
                    Collections.unmodifiableMap(new TreeMap<>()),
                    "", 0, 0, 0, 0, 0);
        }

        char[] symbols = new char[k];
        long[] prefix = new long[k + 1];
        int index = 0;
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            symbols[index] = entry.getKey();
            prefix[index + 1] = prefix[index] + entry.getValue();
            index++;
        }

        // dp[i][j] is the minimum weighted path length for symbols i through j.
        // split[i][j] stores the root split used to reconstruct an optimal tree.
        long[][] dp = new long[k][k];
        int[][] split = new int[k][k];

        long optimizationStart = System.nanoTime();

        // Bottom-up order: solve intervals from length 1 to length k.
        for (int length = 2; length <= k; length++) {
            for (int left = 0; left + length <= k; left++) {
                int right = left + length - 1;
                long intervalWeight = prefix[right + 1] - prefix[left];
                long bestCost = Long.MAX_VALUE;
                int bestSplit = -1;

                // Every split separates the interval into two non-empty subproblems.
                for (int cut = left; cut < right; cut++) {
                    long candidate = dp[left][cut] + dp[cut + 1][right] + intervalWeight;
                    if (candidate < bestCost) {
                        bestCost = candidate;
                        bestSplit = cut;
                    }
                }

                dp[left][right] = bestCost;
                split[left][right] = bestSplit;
            }
        }

        long optimizationNanos = System.nanoTime() - optimizationStart;

        if (showDecisions) {
            System.out.println("DP interval decisions:");
            for (int length = 2; length <= k; length++) {
                for (int left = 0; left + length <= k; left++) {
                    int right = left + length - 1;
                    int cut = split[left][right];
                    System.out.printf(Locale.ROOT,
                            "  [%d..%d] (%s..%s): split after %s, cost=%d%n",
                            left, right,
                            Huffman.printable(symbols[left]),
                            Huffman.printable(symbols[right]),
                            Huffman.printable(symbols[cut]),
                            dp[left][right]);
                }
            }
        }

        Map<Character, String> codes = new TreeMap<>();
        buildCodes(symbols, split, 0, k - 1, "", codes);

        // A single-symbol alphabet is represented with "0" for a non-empty stream.
        if (k == 1) {
            codes.put(symbols[0], "0");
        }

        long encodingStart = System.nanoTime();
        StringBuilder bits = new StringBuilder();
        for (char symbol : input.toCharArray()) {
            bits.append(codes.get(symbol));
        }
        long encodingNanos = System.nanoTime() - encodingStart;

        return new AlphabeticCodingResult(
                Collections.unmodifiableMap(new TreeMap<>(frequencies)),
                Collections.unmodifiableMap(new TreeMap<>(codes)),
                bits.toString(), input.length(), k, optimizationNanos,
                encodingNanos, dp[0][k - 1]);
    }


    private static String printableFrequencies(Map<Character, Integer> frequencies) {
        List<String> parts = new ArrayList<>();
        frequencies.forEach((symbol, frequency) ->
                parts.add("'" + Huffman.printable(symbol) + "'=" + frequency));
        return String.join(", ", parts);
    }

    private static void buildCodes(
            char[] symbols, int[][] split, int left, int right,
            String prefix, Map<Character, String> codes) {
        if (left == right) {
            codes.put(symbols[left], prefix);
            return;
        }

        int cut = split[left][right];
        buildCodes(symbols, split, left, cut, prefix + "0", codes);
        buildCodes(symbols, split, cut + 1, right, prefix + "1", codes);
    }
}
