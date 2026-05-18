import java.util.NoSuchElementException;
import java.util.Objects;

public class RingBufferUnitTests {
    private static int testsRun = 0;

    public static void main(String[] args) {
        testRejectsNonPositiveCapacity();
        testOnlyOneWriterCanBeCreated();
        testReaderThrowsWhenNoDataIsAvailable();
        testMultipleReadersReadIndependently();
        testReadDoesNotRemoveDataForOtherReaders();
        testReaderCreatedAfterWritesStartsAtOldestAvailableItem();
        testNewReaderAfterOverwriteStartsAtOldestStillValidItem();
        testSlowReaderSkipsOverwrittenItems();
        testPartiallyCaughtUpReaderSkipsOnlyMissedItems();
        testWrapAroundPreservesReadOrderForActiveReader();
        testCapacityOneKeepsOnlyNewestItem();
        testNullValuesCanBeStoredAndRead();

        System.out.println("All " + testsRun + " tests passed.");
    }

    private static void testRejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer<Integer>(0),
                "zero capacity should be rejected");
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer<Integer>(-1),
                "negative capacity should be rejected");
    }

    private static void testOnlyOneWriterCanBeCreated() {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);

        assertNotNull(buffer.createWriter(), "first writer should be created");
        assertThrows(IllegalStateException.class, buffer::createWriter,
                "second writer should be rejected");
    }

    private static void testReaderThrowsWhenNoDataIsAvailable() {
        RingBuffer<Integer> buffer = new RingBuffer<>(2);
        Reader<Integer> reader = buffer.createReader();

        assertThrows(NoSuchElementException.class, reader::read,
                "empty buffer should not return a value");

        Writer<Integer> writer = buffer.createWriter();
        writer.write(10);
        assertEquals(10, reader.read(), "reader should receive data written later");
        assertThrows(NoSuchElementException.class, reader::read,
                "reader should throw again after catching up");
    }

    private static void testMultipleReadersReadIndependently() {
        RingBuffer<Integer> buffer = new RingBuffer<>(4);
        Writer<Integer> writer = buffer.createWriter();
        Reader<Integer> readerOne = buffer.createReader();
        Reader<Integer> readerTwo = buffer.createReader();

        writer.write(1);
        writer.write(2);
        writer.write(3);

        assertEquals(1, readerOne.read(), "reader one should read first item");
        assertEquals(2, readerOne.read(), "reader one should advance independently");
        assertEquals(1, readerTwo.read(), "reader two should still read first item");
        assertEquals(3, readerOne.read(), "reader one should continue from its own position");
        assertEquals(2, readerTwo.read(), "reader two should continue from its own position");
        assertEquals(3, readerTwo.read(), "reader two should catch up independently");
    }

    private static void testReadDoesNotRemoveDataForOtherReaders() {
        RingBuffer<String> buffer = new RingBuffer<>(2);
        Writer<String> writer = buffer.createWriter();
        Reader<String> readerOne = buffer.createReader();
        Reader<String> readerTwo = buffer.createReader();

        writer.write("A");

        assertEquals("A", readerOne.read(), "first reader should read the value");
        assertEquals("A", readerTwo.read(), "same value should remain available to another reader");
    }

    private static void testReaderCreatedAfterWritesStartsAtOldestAvailableItem() {
        RingBuffer<Integer> buffer = new RingBuffer<>(5);
        Writer<Integer> writer = buffer.createWriter();

        writer.write(11);
        writer.write(12);

        Reader<Integer> reader = buffer.createReader();

        assertEquals(11, reader.read(), "new reader should start at oldest available item");
        assertEquals(12, reader.read(), "new reader should read existing items in write order");
        assertThrows(NoSuchElementException.class, reader::read,
                "new reader should stop after current data is consumed");
    }

    private static void testNewReaderAfterOverwriteStartsAtOldestStillValidItem() {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        Writer<Integer> writer = buffer.createWriter();

        writer.write(1);
        writer.write(2);
        writer.write(3);
        writer.write(4);
        writer.write(5);

        Reader<Integer> reader = buffer.createReader();

        assertEquals(3, reader.read(), "new reader should skip overwritten values");
        assertEquals(4, reader.read(), "new reader should preserve order after overwrite");
        assertEquals(5, reader.read(), "new reader should include newest value");
        assertThrows(NoSuchElementException.class, reader::read,
                "reader should throw after all available values are read");
    }

    private static void testSlowReaderSkipsOverwrittenItems() {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        Writer<Integer> writer = buffer.createWriter();
        Reader<Integer> slowReader = buffer.createReader();

        writer.write(1);
        writer.write(2);
        writer.write(3);
        writer.write(4);
        writer.write(5);

        assertEquals(3, slowReader.read(), "slow reader should resume at oldest still valid item");
        assertEquals(4, slowReader.read(), "slow reader should continue in order");
        assertEquals(5, slowReader.read(), "slow reader should read newest available item");
        assertThrows(NoSuchElementException.class, slowReader::read,
                "slow reader should throw after catching up");
    }

    private static void testPartiallyCaughtUpReaderSkipsOnlyMissedItems() {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        Writer<Integer> writer = buffer.createWriter();
        Reader<Integer> reader = buffer.createReader();

        writer.write(1);
        writer.write(2);
        assertEquals(1, reader.read(), "reader should consume first value before falling behind");

        writer.write(3);
        writer.write(4);
        writer.write(5);

        assertEquals(3, reader.read(), "reader should skip only the overwritten unread value");
        assertEquals(4, reader.read(), "reader should continue with remaining valid values");
        assertEquals(5, reader.read(), "reader should read latest value");
    }

    private static void testWrapAroundPreservesReadOrderForActiveReader() {
        RingBuffer<String> buffer = new RingBuffer<>(3);
        Writer<String> writer = buffer.createWriter();
        Reader<String> reader = buffer.createReader();

        writer.write("A");
        writer.write("B");
        writer.write("C");
        assertEquals("A", reader.read(), "reader should consume first item before wrap-around");

        writer.write("D");

        assertEquals("B", reader.read(), "reader should read second item after wrap-around");
        assertEquals("C", reader.read(), "reader should read third item after wrap-around");
        assertEquals("D", reader.read(), "reader should read wrapped item last");
        assertThrows(NoSuchElementException.class, reader::read,
                "reader should be caught up after wrapped item");
    }

    private static void testCapacityOneKeepsOnlyNewestItem() {
        RingBuffer<Integer> buffer = new RingBuffer<>(1);
        Writer<Integer> writer = buffer.createWriter();
        Reader<Integer> readerOne = buffer.createReader();
        Reader<Integer> readerTwo = buffer.createReader();

        writer.write(100);
        writer.write(200);

        assertEquals(200, readerOne.read(), "capacity-one buffer should keep newest value");
        assertEquals(200, readerTwo.read(), "same newest value should be available to each reader");
        assertThrows(NoSuchElementException.class, readerOne::read,
                "reader one should have no more data");
        assertThrows(NoSuchElementException.class, readerTwo::read,
                "reader two should have no more data");
    }

    private static void testNullValuesCanBeStoredAndRead() {
        RingBuffer<String> buffer = new RingBuffer<>(2);
        Writer<String> writer = buffer.createWriter();
        Reader<String> reader = buffer.createReader();

        writer.write(null);
        writer.write("next");

        assertNull(reader.read(), "reader should receive a stored null value");
        assertEquals("next", reader.read(), "reader should continue after reading null");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        testsRun++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertNull(Object actual, String message) {
        testsRun++;
        if (actual != null) {
            throw new AssertionError(message + " expected <null> but was <" + actual + ">");
        }
    }

    private static void assertNotNull(Object actual, String message) {
        testsRun++;
        if (actual == null) {
            throw new AssertionError(message + " expected a non-null value");
        }
    }

    private static <T extends Throwable> void assertThrows(
            Class<T> expectedType,
            ThrowingRunnable runnable,
            String message) {
        testsRun++;
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw new AssertionError(
                    message + " expected <" + expectedType.getSimpleName() + "> but was <"
                            + actual.getClass().getSimpleName() + ">",
                    actual);
        }
        throw new AssertionError(message + " expected <" + expectedType.getSimpleName() + "> but nothing was thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
