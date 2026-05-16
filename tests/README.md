# Unit Tests

These tests validate the existing ring buffer behavior without changing the
implementation code.

## Run

From the repository root:

```bash
./tests/run_tests.sh
```

The script compiles the production classes and the test runner into a temporary
directory, then executes `RingBufferUnitTests`.

## Coverage

The tests cover:

- invalid capacity handling
- single-writer enforcement
- reads from an empty buffer
- independent read positions for multiple readers
- read operations not removing values for other readers
- reader start position before and after overwrite
- slow-reader behavior when old items are overwritten
- wrap-around ordering
- capacity-one buffers
- stored `null` values

## PR Notes

This project does not include Maven, Gradle, or JUnit configuration. To keep the
PR limited to test-related changes, the tests use a small standard-Java runner
instead of modifying the source layout or adding a full build system.

Concurrent access behavior is not covered by these unit tests because the
implementation exposes no synchronization contract. Testing that reliably would
require clarifying or changing production code, which is outside the scope of
this PR.
