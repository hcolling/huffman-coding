package huffman;

import java.util.*;

public final class Huffman {
    private Huffman() {}

    public static HuffmanResult encode(String input, boolean showDecisions) {
        Objects.requireNonNull(input, "input cannot be null");

        // Count each symbol once so the tree can favor the most frequent characters with shorter codes.
        Map<Character, Integer> frequencies = new TreeMap<>();
        for (char c : input.toCharArray()) {
            frequencies.merge(c, 1, Integer::sum);
        }

        // A small symbol value acts as a stable tiebreaker so equivalent weights still produce a deterministic tree.
        Comparator<HuffmanNode> order = Comparator
                .comparingInt((HuffmanNode n) -> n.frequency)
                .thenComparingInt(n -> n.minSymbol);
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(order);
        frequencies.forEach((symbol, frequency) -> queue.add(new HuffmanNode(symbol, frequency)));

        if (showDecisions) {
            System.out.println("Frequencies: " + printableFrequencies(frequencies));
        }

        // Repeatedly merge the two lightest fragments; the parent holds the combined weight for the next round.
        long buildStart = System.nanoTime();
        int mergeStep = 1;
        while (queue.size() > 1) {
            HuffmanNode first = queue.remove();
            HuffmanNode second = queue.remove();
            HuffmanNode parent = new HuffmanNode(first, second);
            queue.add(parent);

            if (showDecisions) {
                System.out.printf(Locale.ROOT,
                        "Merge %d: %s(%d) + %s(%d) -> node(%d)%n",
                        mergeStep++, describe(first), first.frequency,
                        describe(second), second.frequency, parent.frequency);
            }
        }
        HuffmanNode root = queue.poll();
        long buildNanos = System.nanoTime() - buildStart;

        // Traverse the completed tree and record the bit prefix for each leaf character.
        Map<Character, String> codes = new TreeMap<>();
        if (root != null) {
            buildCodes(root, "", codes);
        }

        // Once every symbol has a code, translate the input into the compact binary representation.
        long encodeStart = System.nanoTime();
        StringBuilder bits = new StringBuilder();
        for (char c : input.toCharArray()) {
            bits.append(codes.get(c));
        }
        long encodeNanos = System.nanoTime() - encodeStart;

        return new HuffmanResult(
                Collections.unmodifiableMap(new TreeMap<>(frequencies)),
                Collections.unmodifiableMap(new TreeMap<>(codes)),
                bits.toString(), input.length(), frequencies.size(), buildNanos, encodeNanos);
    }

    private static void buildCodes(HuffmanNode node, String prefix, Map<Character, String> codes) {
        if (node.isLeaf()) {
            // A single symbol still needs a valid code; using "0" keeps the leaf path unambiguous in a one-node tree.
            codes.put(node.symbol, prefix.isEmpty() ? "0" : prefix);
            return;
        }
        // Left and right branches encode the same logic as binary choices: 0 for one side, 1 for the other.
        buildCodes(node.left, prefix + "0", codes);
        buildCodes(node.right, prefix + "1", codes);
    }

    private static String printableFrequencies(Map<Character, Integer> frequencies) {
        List<String> parts = new ArrayList<>();
        frequencies.forEach((c, f) -> parts.add("'" + printable(c) + "'=" + f));
        return String.join(", ", parts);
    }

    private static String describe(HuffmanNode node) {
        return node.isLeaf() ? "'" + printable(node.symbol) + "'" : "group";
    }

    public static String printable(char c) {
        // Human-friendly names make debug output easier to read without changing the actual data.
        return switch (c) {
            case ' ' -> "SPACE";
            case '\n' -> "\\n";
            case '\t' -> "\\t";
            default -> Character.toString(c);
        };
    }
}
