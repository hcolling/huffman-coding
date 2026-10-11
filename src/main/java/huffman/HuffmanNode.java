package huffman;

final class HuffmanNode {
    // A leaf node stores the actual character and its weight in the input stream.
    final char symbol;
    final int frequency;
    final HuffmanNode left;
    final HuffmanNode right;

    // The smallest visible symbol keeps merge ordering stable when frequencies tie.
    final char minSymbol;

    HuffmanNode(char symbol, int frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
        this.minSymbol = symbol;
    }

    HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.symbol = '\0';
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
        this.minSymbol = (char) Math.min(left.minSymbol, right.minSymbol);
    }

    boolean isLeaf() {
        // Internal nodes are the merged branches; leaves are the original symbols.
        return left == null && right == null;
    }
}
