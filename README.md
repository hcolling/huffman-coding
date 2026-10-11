# Huffman Coding in Java

A didactic Huffman coding project using Java 17 and Maven. Input/output (`Main`) is separated from the algorithm (`Huffman`).

## Requirements
- JDK 17 or newer
- Maven 3.8 or newer

## Run
From the project root:

```bash
mvn test
mvn package
java -cp target/classes huffman.Main tests/test1_small true
java -cp target/classes huffman.Main tests/test2_medium false
java -cp target/classes huffman.Main tests/test3_large false
```

The second argument controls merge tracing (`true`/`false`). For the large input, `false` is recommended because printing every merge does not help performance measurement.

## Test instances
- **Test 1:** `BANANA`, a small input that is easy to trace manually.
- **Test 2:** ASCII text repeated 30 times, for an intermediate-size input.
- **Test 3:** 1,000,000 ASCII characters generated with a fixed seed for reproducibility.

The test files are fixed and reproducible. The large input is generated once and included in the project.

## Reported metrics
The program displays:
- symbol frequencies;
- each merge decision when tracing is enabled;
- codes per symbol and the encoded bit sequence;
- encoded bit count and comparison against an 8-bit-per-symbol baseline;
- time spent building the tree and encoding the sequence.

The comparison assumes 8 bits per input symbol, which is appropriate for the ASCII test files. It does not include the cost of storing the code table, headers, or metadata, so it is not the final size of a complete compressed file.

Timings use `System.nanoTime()` and should be recorded on the machine used for the experiment. They vary with hardware, JVM warm-up, and system load; avoid inventing results or comparing runs without controlling these conditions.

## Implementation decisions
- A priority queue selects the two lowest-frequency nodes.
- Ties are resolved deterministically using the smallest symbol in each node.
- The tree assigns `0` to the left branch and `1` to the right branch.
- For an input with only one distinct symbol, its code is `0`, avoiding an empty code.
- Input is read as UTF-8, but the provided instances are ASCII so the 8-bit baseline is consistent.

## Expected analysis
- In the small test, observe how frequencies determine the tree and how more frequent symbols tend to receive shorter codes.
- In the medium test, examine relative bit reduction and how costs change as input grows.
- In the large test, disable tracing and compare timings. Tree construction depends primarily on the number of distinct symbols (`k`), while encoding processes all `n` input symbols.

Huffman coding is not guaranteed to produce a smaller representation for every input: the comparison above excludes the tree/code table required for decoding.
