
# Huffman Coding

Java implementation of the Huffman coding algorithm using a greedy strategy.

## Requirements

- Java 17 or later
- Maven

## Build

```bash
mvn test
mvn package
```

## Run

The program accepts up to three command-line arguments:

```bash
java -cp target/classes huffman.Main <input-file> <show-trace> <show-encoded-bits>
```

### Arguments

1. `input-file`: path to the input file. Defaults to `tests/test1_small`.
2. `show-trace`: `true` to display the complete sequence of greedy merge decisions; `false` to hide it. Defaults to `true`.
3. `show-encoded-bits`: `true` to print the complete encoded bit sequence; `false` to omit it. Defaults to `false`.

The generated codes and statistics are displayed regardless of whether the trace or encoded sequence is enabled.

### Examples

Display the complete trace and encoded sequence:

```bash
java -cp target/classes huffman.Main tests/test1_small true true
```

Display the complete trace but omit the encoded sequence:

```bash
java -cp target/classes huffman.Main tests/test1_small true false
```

Hide the trace and omit the encoded sequence:

```bash
java -cp target/classes huffman.Main tests/test1_small false false
```

Run the large test without printing the trace or the potentially very long encoded sequence:

```bash
java -cp target/classes huffman.Main tests/test3_large false false
```

### Output

The program displays:

- Input file and number of input symbols.
- Symbol frequencies and merge decisions when tracing is enabled.
- Generated Huffman codes and their frequencies.
- The encoded bit sequence only when explicitly enabled.
- Number of distinct symbols and total encoded bits.
- Estimated original size, encoded-to-original size ratio, and execution timings.

## Tests

Run the automated tests with:

```bash
mvn test
```

The test suite checks prefix-free codes, encoded length, empty and single-symbol inputs, and frequency counts.
