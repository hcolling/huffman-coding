# Huffman Coding — Dynamic Programming Branch

This branch studies a dynamic-programming formulation related to the original Huffman Coding project.

## Problem formulation

Classical Huffman coding constructs an optimal binary prefix code using a greedy strategy. This branch implements **optimal alphabetic binary prefix coding** instead. It adds an ordering constraint: when symbols are sorted by character value, their codewords must appear in the same lexicographic order.

This is a genuine variant of optimal prefix coding. It is not a dynamic-programming replacement for the unrestricted classical Huffman problem. The extra alphabetic constraint changes the set of feasible trees and allows the problem to be formulated as interval dynamic programming.

The original `Huffman.java` implementation is retained as a baseline, so both algorithms can be executed on the same input.

## Requirements

- Java 25 LTS or later
- Maven

## Build and Test

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

1. `input-file`: path to the input text file. Defaults to `tests/test1_small`.
2. `show-trace`: `true` to display the interval decisions stored by the DP. Defaults to `true`.
3. `show-encoded-bits`: `true` to print the full encoded stream. Defaults to `false`.

### Examples

```bash
# Small input, including interval trace and encoded bits
java -cp target/classes huffman.Main tests/test1_small true true

# Medium input, hide the trace and encoded stream
java -cp target/classes huffman.Main tests/test2_medium false false

# Large input, print only codes and statistics
java -cp target/classes huffman.Main tests/test3_large false false
```

## Dynamic-programming model

Let the distinct symbols in ascending order be `s[0], ..., s[k-1]`, with frequencies `f[i]`.

- **State:** `dp[i][j]` is the minimum weighted external path length for an alphabetic prefix tree containing the consecutive symbols from `i` through `j`.
- **Decision:** choose the position `m` where the root divides the interval into `[i, m]` and `[m+1, j]`.
- **Interval weight:** `W(i,j) = f[i] + ... + f[j]`.
- **Recurrence:**

  `dp[i][j] = W(i,j) + min(dp[i][m] + dp[m+1][j])`, for `i <= m < j`.

- **Base case:** `dp[i][i] = 0`, because a one-leaf subtree has no edges below its root.
- **Order:** bottom-up, by increasing interval length.
- **Reconstruction:** `split[i][j]` stores the best division. Recursively traversing the left and right subtrees appends `0` and `1`, respectively.

The implementation uses prefix sums to calculate each interval weight in constant time.

## Complexity

Let `n` be the input length, `k` the number of distinct symbols, and `B` the number of encoded bits.

- Frequency counting with the ordered map: `O(n log k)`.
- DP: `O(k^3)` time and `O(k^2)` space.
- Tree reconstruction: `O(k)` nodes; materializing all code strings: up to `O(k^2)`.
- Encoding: `O(n + B)` time.
- Overall: `O(n log k + k^3 + B)` time and `O(k^2 + B)` space.

The DP table is practical for the supplied tests (up to 40 distinct symbols), but the cubic time and quadratic memory make it less suitable for very large alphabets.

## Important notes

- The alphabetic constraint can make the encoded stream longer than unrestricted Huffman coding. The DP solution is optimal **among alphabetic prefix codes**, not among all binary prefix codes.
- For a one-symbol input, the implementation emits `0` for each occurrence to avoid an empty displayed code. The mathematical one-leaf DP cost remains zero; this implementation convention adds one output bit per occurrence.
- The reported comparison against 8 bits per input character is an estimate and excludes code tables, headers, and metadata.
- Timing values should be measured on the target machine and should not be treated as universal benchmarks.

## Project structure

- `Huffman.java`: original greedy Huffman implementation, retained for comparison.
- `HuffmanNode.java`: tree node used by the greedy implementation.
- `HuffmanResult.java`: result of the greedy implementation.
- `AlphabeticCoding.java`: bottom-up interval DP, reconstruction, and encoding.
- `AlphabeticCodingResult.java`: result and metrics for the DP implementation.
- `Main.java`: command-line entry point for the DP implementation.
- `src/test`: automated tests for prefix-freeness, alphabetic ordering, edge cases, and consistency with the unrestricted Huffman baseline.
- `tests`: small, medium, and large input files reused from the previous branch.
